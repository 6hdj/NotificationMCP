package com.miclaw.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.miclaw.notification.service.KeepAliveService
import com.miclaw.notification.service.WatchdogService

/**
 * 开机自启广播接收器
 *
 * 系统重启后自动恢复所有服务：
 * 1. 启动 KeepAliveService（前台保活）
 * 2. 启动 WatchdogService（守护进程）
 * 3. NotificationListenerService 由系统自动恢复（已注册的 Listener）
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        when (action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.i(TAG, "收到开机广播，启动服务...")
                startServices(context)
            }
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.i(TAG, "收到应用更新广播，重启服务...")
                startServices(context)
            }
            "android.intent.action.QUICKBOOT_POWERON" -> {
                Log.i(TAG, "收到快速开机广播，启动服务...")
                startServices(context)
            }
        }
    }

    private fun startServices(context: Context) {
        try {
            // 启动保活服务
            KeepAliveService.ensureRunning(context)

            // 启动守护进程
            WatchdogService.start(context)

            Log.i(TAG, "所有服务已启动")
        } catch (e: Exception) {
            Log.e(TAG, "启动服务失败: ${e.message}", e)
        }
    }
}
