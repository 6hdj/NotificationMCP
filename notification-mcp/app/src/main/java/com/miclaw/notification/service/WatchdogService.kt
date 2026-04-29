package com.miclaw.notification.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

/**
 * 守护进程服务 — Watchdog
 *
 * 职责：
 * 1. 定期检查 NotificationListenerService 和 KeepAliveService 是否存活
 * 2. 检测到服务死亡时自动重启
 * 3. 记录崩溃次数，超过阈值时停止重启并通知用户
 * 4. 与 KeepAliveService 互相守护（双进程保活）
 */
class WatchdogService : Service() {

    companion object {
        private const val TAG = "WatchdogService"
        private const val CHECK_INTERVAL_MS = 30_000L // 30 秒检查一次
        private const val MAX_RESTART_COUNT = 20       // 最大重启次数
        private const val RESTART_WINDOW_MS = 3_600_000L // 1 小时窗口

        private var instance: WatchdogService? = null
        private var restartCount = 0
        private var windowStart = System.currentTimeMillis()

        fun getInstance(): WatchdogService? = instance

        fun start(context: Context) {
            try {
                val intent = Intent(context, WatchdogService::class.java)
                context.startService(intent)
            } catch (e: Exception) {
                Log.e(TAG, "启动守护进程失败: ${e.message}", e)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, WatchdogService::class.java))
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var watchdogJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i(TAG, "守护进程已创建")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        instance = this
        startWatchdog()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        watchdogJob?.cancel()
        scope.cancel()
        Log.w(TAG, "守护进程已销毁")
    }

    private fun startWatchdog() {
        watchdogJob?.cancel()
        watchdogJob = scope.launch {
            while (isActive) {
                delay(CHECK_INTERVAL_MS)
                checkAndRecover()
            }
        }
    }

    /**
     * 检查服务状态并恢复
     */
    private suspend fun checkAndRecover() {
        // 检查 1：NotificationListenerService 是否存活
        if (!NotificationListenerServiceImpl.isRunning) {
            Log.w(TAG, "⚠️ NotificationListenerService 已死亡")
            if (!tryRestart("NotificationListener")) return
        }

        // 检查 2：KeepAliveService 是否存活
        if (KeepAliveService.getInstance() == null) {
            Log.w(TAG, "⚠️ KeepAliveService 已死亡")
            if (!tryRestart("KeepAlive")) return
        }

        // 两个服务都正常，重置重启计数（如果窗口已过）
        val now = System.currentTimeMillis()
        if (now - windowStart > RESTART_WINDOW_MS) {
            restartCount = 0
            windowStart = now
        }
    }

    /**
     * 尝试重启服务
     * @return true 表示可以继续，false 表示已达重启上限
     */
    private suspend fun tryRestart(serviceName: String): Boolean {
        val now = System.currentTimeMillis()

        // 重置窗口计数
        if (now - windowStart > RESTART_WINDOW_MS) {
            restartCount = 0
            windowStart = now
        }

        restartCount++
        Log.w(TAG, "重启 #$restartCount: $serviceName")

        if (restartCount >= MAX_RESTART_COUNT) {
            Log.e(TAG, "❌ 已达最大重启次数 ($MAX_RESTART_COUNT)，停止守护")
            // TODO: 通知用户服务异常
            return false
        }

        // 延迟重启，避免频繁重启
        delay(2000)

        when (serviceName) {
            "NotificationListener" -> {
                try {
                    val intent = Intent("android.service.notification.NotificationListenerService")
                    sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "重启 NotificationListener 失败: ${e.message}")
                }
            }
            "KeepAlive" -> {
                KeepAliveService.ensureRunning(this)
            }
        }

        return true
    }
}
