package com.notificationmcp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.notificationmcp.service.KeepAliveService
import com.notificationmcp.service.KeepAliveWorker
import com.notificationmcp.ui.theme.NotificationMCPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // 无论授权与否都启动服务（未授权时服务仍可运行，只是不显示通知）
        startKeepAliveService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // WorkManager 兜底：即使服务被杀，15分钟后自动拉起
        KeepAliveWorker.schedule(applicationContext)

        // 请求通知权限后再启动前台服务
        requestNotificationPermissionThenStart()

        setContent {
            NotificationMCPTheme {
                HyperOSBackground()
                MainNavigation()
            }
        }
    }

    private fun requestNotificationPermissionThenStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) {
                startKeepAliveService()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startKeepAliveService()
        }
    }

    private fun startKeepAliveService() {
        try {
            startForegroundService(Intent(this, KeepAliveService::class.java))
        } catch (_: Exception) {
            // 即使前台服务启动失败，也不影响 App 使用
        }
    }
}

@Composable
private fun HyperOSBackground() {
    val isDark = isSystemInDarkTheme()

    val colors = if (isDark) {
        listOf(
            Color(0xFF0A0A14),
            Color(0xFF0F0F1A),
            Color(0xFF0A0A14)
        )
    } else {
        listOf(
            Color(0xFFF0F0F5),
            Color(0xFFE8E8ED),
            Color(0xFFF0F0F5)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = colors,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    )
}
