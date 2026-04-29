package com.miclaw.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.miclaw.notification.db.AppDatabase
import com.miclaw.notification.server.LocalMcpServer
import com.miclaw.notification.service.KeepAliveService
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class NotificationMcpApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val database by lazy { AppDatabase.getInstance(this) }

        override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
        KeepAliveService.ensureRunning(this)
        
        // 启动本地 MCP 服务器
        try {
            LocalMcpServer.start(8765)
            Log.i("NotificationMcpApp", "本地 MCP 服务器已启动，端口: 8765")
        } catch (e: Exception) {
            Log.e("NotificationMcpApp", "启动 MCP 服务器失败: ${e.message}", e)
        }
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java) ?: return

        // 服务常驻通知
        val serviceChannel = NotificationChannel(
            CHANNEL_SERVICE,
            "通知读取服务",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "保持通知读取服务在后台运行"
            setShowBadge(false)
        }

        // 告警通知
        val alertChannel = NotificationChannel(
            CHANNEL_ALERT,
            "服务告警",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "服务异常、权限丢失等告警通知"
            enableVibration(true)
        }

        manager.createNotificationChannels(listOf(serviceChannel, alertChannel))
    }

    companion object {
        const val CHANNEL_SERVICE = "notification_service"
        const val CHANNEL_ALERT = "notification_alert"

        lateinit var instance: NotificationMcpApp
            private set
    }
}
