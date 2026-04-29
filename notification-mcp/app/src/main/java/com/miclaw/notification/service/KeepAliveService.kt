package com.miclaw.notification.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.miclaw.notification.NotificationMcpApp
import com.miclaw.notification.R
import com.miclaw.notification.ui.MainActivity
import kotlinx.coroutines.*

/**
 * 前台保活服务
 *
 * 核心职责：
 * 1. 以前台服务形式运行，降低被系统 LMK 杀死的概率
 * 2. 持有 WakeLock，防止 CPU 休眠导致 WebSocket 断连
 * 3. 定时心跳，维持与电脑端 Miclaw 的连接
 * 4. 监控 NotificationListenerService 状态，异常时触发重启
 */
class KeepAliveService : Service() {

    companion object {
        private const val TAG = "KeepAliveService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "notification_service"
        private const val HEARTBEAT_INTERVAL_MS = 30_000L   // 30 秒心跳
        private const val HEALTH_CHECK_INTERVAL_MS = 60_000L // 60 秒健康检查
        private const val WAKELOCK_TAG = "NotificationMcp::KeepAlive"

        private var instance: KeepAliveService? = null

        fun getInstance(): KeepAliveService? = instance

        /**
         * 确保服务正在运行
         */
        fun ensureRunning(context: Context) {
            try {
                val intent = Intent(context, KeepAliveService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "启动保活服务失败: ${e.message}", e)
            }
        }

        /**
         * 停止服务
         */
        fun stop(context: Context) {
            context.stopService(Intent(context, KeepAliveService::class.java))
        }
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var heartbeatJob: Job? = null
    private var healthCheckJob: Job? = null

    // ═══════════════════════════════════════════════════
    //  生命周期
    // ═══════════════════════════════════════════════════

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i(TAG, "保活服务已创建")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        instance = this

        // 立即提升为前台服务
        startForeground(NOTIFICATION_ID, buildNotification("服务运行中"))

        // 获取 WakeLock
        acquireWakeLock()

        // 启动心跳和健康检查
        startHeartbeat()
        startHealthCheck()

        // 确保 WebSocket 连接
        ensureWebSocketConnection()

        Log.i(TAG, "保活服务已启动，前台通知已显示")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        releaseWakeLock()
        scope.cancel()
        Log.w(TAG, "保活服务已销毁")
    }

    // ═══════════════════════════════════════════════════
    //  前台通知
    // ═══════════════════════════════════════════════════

    private fun buildNotification(statusText: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        return builder
            .setContentTitle("通知读取服务")
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(statusText: String) {
        try {
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager?.notify(NOTIFICATION_ID, buildNotification(statusText))
        } catch (e: Exception) {
            Log.e(TAG, "更新通知失败: ${e.message}")
        }
    }

    // ═══════════════════════════════════════════════════
    //  WakeLock
    // ═══════════════════════════════════════════════════

    @Suppress("DEPRECATION")
    private fun acquireWakeLock() {
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return
            wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                WAKELOCK_TAG
            ).apply {
                acquire(10 * 60 * 1000L) // 10 分钟超时，定时续期
            }
            Log.d(TAG, "WakeLock 已获取")
        } catch (e: Exception) {
            Log.e(TAG, "获取 WakeLock 失败: ${e.message}", e)
        }
    }

    private fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) it.release()
            }
            wakeLock = null
        } catch (e: Exception) {
            Log.e(TAG, "释放 WakeLock 失败: ${e.message}")
        }
    }

    // ═══════════════════════════════════════════════════
    //  心跳
    // ═══════════════════════════════════════════════════

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive) {
                delay(HEARTBEAT_INTERVAL_MS)
                try {
                    val wsManager = McpWebSocketManager.getInstance()
                    if (wsManager.isConnected()) {
                        wsManager.sendHeartbeat()
                        updateNotification("🟢 已连接 · ${formatTime()}")
                    } else {
                        updateNotification("🟡 重连中...")
                        wsManager.reconnect()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "心跳异常: ${e.message}")
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════
    //  健康检查
    // ═══════════════════════════════════════════════════

    private fun startHealthCheck() {
        healthCheckJob?.cancel()
        healthCheckJob = scope.launch {
            while (isActive) {
                delay(HEALTH_CHECK_INTERVAL_MS)
                try {
                    performHealthCheck()
                } catch (e: Exception) {
                    Log.e(TAG, "健康检查异常: ${e.message}")
                }
            }
        }
    }

    private suspend fun performHealthCheck() {
        // 检查 NotificationListenerService 是否存活
        if (!NotificationListenerServiceImpl.isRunning) {
            Log.w(TAG, "NotificationListenerService 未运行，尝试重启")
            restartNotificationListener()
        }

        // 续期 WakeLock
        if (wakeLock?.isHeld != true) {
            acquireWakeLock()
        }

        // 检查 WebSocket 连接
        val wsManager = McpWebSocketManager.getInstance()
        if (!wsManager.isConnected()) {
            Log.w(TAG, "WebSocket 未连接，尝试重连")
            wsManager.reconnect()
        }

        // 更新统计信息到通知
        val stats = NotificationListenerServiceImpl.stats
        val received = stats.totalReceived
        val forwarded = stats.totalForwarded
        updateNotification("📊 已接收 $received · 已转发 $forwarded · ${formatTime()}")
    }

    // ═══════════════════════════════════════════════════
    //  重启机制
    // ═══════════════════════════════════════════════════

    private fun restartNotificationListener() {
        try {
            // 方法1：通过 requestRebind 重新绑定
            val intent = Intent("android.service.notification.NotificationListenerService")
            sendBroadcast(intent)
            Log.i(TAG, "已发送 NotificationListener 重启广播")
        } catch (e: Exception) {
            Log.e(TAG, "重启 NotificationListener 失败: ${e.message}", e)
        }
    }

    private fun ensureWebSocketConnection() {
        scope.launch {
            val wsManager = McpWebSocketManager.getInstance()
            if (!wsManager.isConnected()) {
                wsManager.connect()
            }
            // 连接成功后补传积压通知
            delay(3000)
            NotificationListenerServiceImpl().replayUnforwardedNotifications()
        }
    }

    // ═══════════════════════════════════════════════════
    //  辅助
    // ═══════════════════════════════════════════════════

    private fun formatTime(): String {
        val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}
