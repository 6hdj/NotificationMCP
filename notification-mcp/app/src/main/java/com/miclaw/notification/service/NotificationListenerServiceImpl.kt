package com.miclaw.notification.service

import android.app.Notification
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.miclaw.notification.NotificationMcpApp
import com.miclaw.notification.diagnostic.DiagnosticEngine
import com.miclaw.notification.model.NotificationData
import com.miclaw.notification.model.ServiceStats
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * 通知监听服务 — 核心入口
 *
 * 职责：
 * 1. 实时捕获系统通知栏的每一条通知
 * 2. 全字段无损解析通知内容
 * 3. 去重过滤（基于 notificationId + packageName + postTime 指纹）
 * 4. 写入本地数据库（Room）
 * 5. 通过 WebSocket 实时转发到电脑端 Miclaw
 * 6. 异常时自动重启并补传积压通知
 */
class NotificationListenerServiceImpl : NotificationListenerService() {

    companion object {
        private const val TAG = "NotificationListener"

        /** 服务是否存活的全局标志 */
        @Volatile
        var isRunning = false
            private set

        /** 服务统计信息 */
        val stats = ServiceStats()

        /** 通知处理回调（供 UI 和诊断使用） */
        var onNotificationReceived: ((NotificationData) -> Unit)? = null
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val isProcessing = AtomicBoolean(false)
    private val processedFingerprints = ConcurrentLinkedQueue<String>()
    private val maxFingerprintCache = 500

    // ═══════════════════════════════════════════════════
    //  生命周期
    // ═══════════════════════════════════════════════════

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        stats.startTime = System.currentTimeMillis()
        Log.i(TAG, "通知监听服务已创建")
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        scope.cancel()
        Log.w(TAG, "通知监听服务已销毁")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        isRunning = true
        Log.i(TAG, "通知监听服务已连接")
        stats.lastHeartbeatTime = System.currentTimeMillis()

        // 连接后立即补传积压通知
        scope.launch {
            delay(2000) // 等待 2 秒让系统稳定
            replayActiveNotifications()
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isRunning = false
        Log.w(TAG, "通知监听服务已断开，尝试重新请求连接")
        // 请求系统重新连接
        requestRebind(ComponentName(this, javaClass))
    }

    // ═══════════════════════════════════════════════════
    //  通知捕获
    // ═══════════════════════════════════════════════════

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn == null) return

        // 快速过滤：系统自身通知和正在运行的服务通知
        if (shouldSkip(sbn)) return

        stats.totalReceived++
        stats.lastNotificationTime = System.currentTimeMillis()

        scope.launch {
            try {
                processNotification(sbn)
            } catch (e: Exception) {
                Log.e(TAG, "处理通知异常: ${e.message}", e)
                stats.totalErrors++
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // 通知被移除时不做处理，保持已记录的数据
    }

    // ═══════════════════════════════════════════════════
    //  核心处理流程
    // ═══════════════════════════════════════════════════

    private suspend fun processNotification(sbn: StatusBarNotification) {
        // 1. 解析通知全字段
        val data = parseNotification(sbn) ?: return

        // 2. 生成去重指纹
        val fingerprint = NotificationData.generateFingerprint(
            data.notificationId, data.packageName, data.postTime
        )

        // 3. 内存级快速去重（避免频繁查库）
        if (processedFingerprints.contains(fingerprint)) {
            stats.totalDuplicates++
            return
        }

        // 4. 数据库级去重
        val db = NotificationMcpApp.instance.database
        val existing = db.notificationDao().findByFingerprint(fingerprint)
        if (existing != null) {
            stats.totalDuplicates++
            addToFingerprintCache(fingerprint)
            return
        }

        // 5. 写入数据库
        val dbId = db.notificationDao().insert(data)
        if (dbId == -1L) {
            // UNIQUE 约束冲突，说明已存在
            stats.totalDuplicates++
            return
        }
        val savedData = data.copy(dbId = dbId)
        addToFingerprintCache(fingerprint)

        // 6. 回调通知（供 UI 和诊断）
        onNotificationReceived?.invoke(savedData)

        // 7. 转发到电脑端
        forwardToDesktop(savedData)

        Log.d(TAG, "通知已处理: [${data.appName}] ${data.title} - ${data.content.take(50)}")
    }

    /**
     * 全字段无损解析 StatusBarNotification
     */
    private fun parseNotification(sbn: StatusBarNotification): NotificationData? {
        val notification = sbn.notification ?: return null
        val extras = notification.extras ?: return null

        val packageName = sbn.packageName
        val appName = try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }

        return NotificationData(
            notificationId = sbn.id,
            channelId = notification.channelId,
            fingerprint = "", // 稍后生成
            postTime = sbn.postTime,
            receiveTime = System.currentTimeMillis(),
            packageName = packageName,
            appName = appName,
            appVersion = try {
                packageManager.getPackageInfo(packageName, 0).versionName ?: ""
            } catch (e: Exception) { "" },
            uid = sbn.uid,
            title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: "",
            content = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: "",
            subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString(),
            bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString(),
            priority = notification.priority,
            isOngoing = (notification.flags and Notification.FLAG_ONGOING_EVENT) != 0,
            isClearable = (notification.flags and 0x00000010) != 0,
            isRead = false,
            category = notification.category,
            isGroupSummary = (notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0,
            actions = parseActions(notification),
            extras = parseExtras(extras)
        )
    }

    private fun parseActions(notification: Notification): String {
        val actions = notification.actions ?: return "[]"
        return try {
            val jsonArray = org.json.JSONArray()
            for (action in actions) {
                val obj = org.json.JSONObject()
                obj.put("title", action.title?.toString() ?: "")
                obj.put("actionIntent", action.actionIntent?.toString() ?: "")
                jsonArray.put(obj)
            }
            jsonArray.toString()
        } catch (e: Exception) {
            "[]"
        }
    }

    private fun parseExtras(extras: Bundle): String {
        return try {
            val map = mutableMapOf<String, String>()
            for (key in extras.keySet()) {
                val value = extras.get(key)
                if (value != null && key != Notification.EXTRA_PICTURE) {
                    map[key] = value.toString().take(200)
                }
            }
            org.json.JSONObject(map as Map<*, *>).toString()
        } catch (e: Exception) {
            "{}"
        }
    }

    // ═══════════════════════════════════════════════════
    //  转发
    // ═══════════════════════════════════════════════════

    private suspend fun forwardToDesktop(data: NotificationData) {
        val wsManager = McpWebSocketManager.getInstance()

        if (wsManager.isConnected()) {
            val success = wsManager.sendNotification(data)
            if (success) {
                // 标记已转发
                NotificationMcpApp.instance.database.notificationDao()
                    .markForwarded(data.dbId, System.currentTimeMillis())
                stats.totalForwarded++
            } else {
                // 发送失败，保留在未转发队列，等待重连后补传
                Log.w(TAG, "转发失败，保留在队列: ${data.title}")
            }
        } else {
            // WebSocket 未连接，数据已在数据库中，等待重连后补传
            Log.d(TAG, "WebSocket 未连接，通知已缓存: ${data.title}")
        }
    }

    // ═══════════════════════════════════════════════════
    //  补传机制
    // ═══════════════════════════════════════════════════

    /**
     * 重放当前活跃通知（服务连接后调用）
     */
    private suspend fun replayActiveNotifications() {
        try {
            val active = activeNotifications ?: return
            Log.i(TAG, "开始重放 ${active.size} 条活跃通知")

            for (sbn in active) {
                if (shouldSkip(sbn)) continue
                processNotification(sbn)
            }

            Log.i(TAG, "活跃通知重放完成")
        } catch (e: Exception) {
            Log.e(TAG, "重放活跃通知失败: ${e.message}", e)
        }
    }

    /**
     * 补传积压的未转发通知（WebSocket 重连后调用）
     */
    suspend fun replayUnforwardedNotifications() {
        try {
            val db = NotificationMcpApp.instance.database
            val unforwarded = db.notificationDao().getUnforwarded(100)

            if (unforwarded.isEmpty()) return

            Log.i(TAG, "开始补传 ${unforwarded.size} 条积压通知")
            val wsManager = McpWebSocketManager.getInstance()

            var forwardedCount = 0
            for (data in unforwarded) {
                if (!wsManager.isConnected()) break

                val success = wsManager.sendNotification(data)
                if (success) {
                    db.notificationDao().markForwarded(data.dbId, System.currentTimeMillis())
                    forwardedCount++
                    stats.totalForwarded++
                } else {
                    break
                }
            }

            Log.i(TAG, "补传完成: $forwardedCount/${unforwarded.size}")
        } catch (e: Exception) {
            Log.e(TAG, "补传积压通知失败: ${e.message}", e)
        }
    }

    // ═══════════════════════════════════════════════════
    //  辅助方法
    // ═══════════════════════════════════════════════════

    /**
     * 判断是否应跳过该通知
     */
    private fun shouldSkip(sbn: StatusBarNotification): Boolean {
        val pkg = sbn.packageName

        // 跳过自身通知
        if (pkg == packageName) return true

        // 跳过系统 UI
        if (pkg == "com.android.systemui") return true

        // 跳过 Android 系统
        if (pkg == "android") return true

        // 跳过正在运行的服务通知（FLAG_ONGOING_EVENT + 无标题）
        val notification = sbn.notification ?: return true
        if ((notification.flags and Notification.FLAG_ONGOING_EVENT) != 0) {
            val extras = notification.extras
            val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
            val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
            if (title.isEmpty() && text.isEmpty()) return true
        }

        return false
    }

    private fun addToFingerprintCache(fingerprint: String) {
        processedFingerprints.add(fingerprint)
        // 控制缓存大小
        while (processedFingerprints.size > maxFingerprintCache) {
            processedFingerprints.poll()
        }
    }
}
