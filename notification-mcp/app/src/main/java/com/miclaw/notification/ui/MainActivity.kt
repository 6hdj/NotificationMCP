package com.miclaw.notification.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.miclaw.notification.R
import com.miclaw.notification.NotificationMcpApp
import com.miclaw.notification.service.KeepAliveService
import com.miclaw.notification.service.McpWebSocketManager
import com.miclaw.notification.service.NotificationListenerServiceImpl
import com.miclaw.notification.service.WatchdogService
import kotlinx.coroutines.*

/**
 * 主界面 — 服务状态仪表盘
 *
 * 显示：
 * 1. 通知使用权状态
 * 2. 各服务运行状态
 * 3. WebSocket 连接状态
 * 4. 今日统计（接收/转发/过滤）
 * 5. 快捷操作按钮
 */
class MainActivity : Activity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var statusRefreshJob: Job? = null

    // UI 组件
    private lateinit var tvPermissionStatus: TextView
    private lateinit var tvListenerStatus: TextView
    private lateinit var tvKeepAliveStatus: TextView
    private lateinit var tvWatchdogStatus: TextView
    private lateinit var tvWebSocketStatus: TextView
    private lateinit var tvStatsReceived: TextView
    private lateinit var tvStatsForwarded: TextView
    private lateinit var tvStatsFiltered: TextView
    private lateinit var tvStatsErrors: TextView
    private lateinit var btnToggleService: Button
    private lateinit var btnOpenDiagnostic: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        startStatusRefresh()
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        statusRefreshJob?.cancel()
        scope.cancel()
    }

    private fun initViews() {
        tvPermissionStatus = findViewById(R.id.tv_permission_status)
        tvListenerStatus = findViewById(R.id.tv_listener_status)
        tvKeepAliveStatus = findViewById(R.id.tv_keepalive_status)
        tvWatchdogStatus = findViewById(R.id.tv_watchdog_status)
        tvWebSocketStatus = findViewById(R.id.tv_websocket_status)
        tvStatsReceived = findViewById(R.id.tv_stats_received)
        tvStatsForwarded = findViewById(R.id.tv_stats_forwarded)
        tvStatsFiltered = findViewById(R.id.tv_stats_filtered)
        tvStatsErrors = findViewById(R.id.tv_stats_errors)
        btnToggleService = findViewById(R.id.btn_toggle_service)
        btnOpenDiagnostic = findViewById(R.id.btn_open_diagnostic)
    }

    private fun setupListeners() {
        btnToggleService.setOnClickListener {
            if (isNotificationListenerEnabled()) {
                // 跳转到通知使用权设置
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            } else {
                // 引导开启
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                Toast.makeText(this, "请找到并开启「通知读取」服务", Toast.LENGTH_LONG).show()
            }
        }

        btnOpenDiagnostic.setOnClickListener {
            startActivity(Intent(this, DiagnosticActivity::class.java))
        }
    }

    /**
     * 定时刷新状态（每 3 秒）
     */
    private fun startStatusRefresh() {
        statusRefreshJob = scope.launch {
            while (isActive) {
                refreshStatus()
                delay(3000)
            }
        }
    }

    private fun refreshStatus() {
        // 权限状态
        val listenerEnabled = isNotificationListenerEnabled()
        tvPermissionStatus.text = if (listenerEnabled) "✅ 已授权" else "❌ 未授权"
        tvPermissionStatus.setTextColor(getColor(if (listenerEnabled) R.color.diagnostic_pass else R.color.diagnostic_fail))

        // 服务状态
        val listenerRunning = NotificationListenerServiceImpl.isRunning
        tvListenerStatus.text = if (listenerRunning) "✅ 运行中" else "❌ 已停止"
        tvListenerStatus.setTextColor(getColor(if (listenerRunning) R.color.diagnostic_pass else R.color.diagnostic_fail))

        val keepAliveRunning = KeepAliveService.getInstance() != null
        tvKeepAliveStatus.text = if (keepAliveRunning) "✅ 运行中" else "❌ 已停止"
        tvKeepAliveStatus.setTextColor(getColor(if (keepAliveRunning) R.color.diagnostic_pass else R.color.diagnostic_fail))

        val watchdogRunning = WatchdogService.getInstance() != null
        tvWatchdogStatus.text = if (watchdogRunning) "✅ 运行中" else "❌ 已停止"
        tvWatchdogStatus.setTextColor(getColor(if (watchdogRunning) R.color.diagnostic_pass else R.color.diagnostic_fail))

        // WebSocket 状态
        val wsConnected = McpWebSocketManager.getInstance().isConnected()
        tvWebSocketStatus.text = if (wsConnected) "✅ 已连接" else "❌ 未连接"
        tvWebSocketStatus.setTextColor(getColor(if (wsConnected) R.color.diagnostic_pass else R.color.diagnostic_fail))

        // 统计数据
        val stats = NotificationListenerServiceImpl.stats
        tvStatsReceived.text = "接收: ${stats.totalReceived}"
        tvStatsForwarded.text = "转发: ${stats.totalForwarded}"
        tvStatsFiltered.text = "过滤: ${stats.totalFiltered}"
        tvStatsErrors.text = "异常: ${stats.totalErrors}"

        // 按钮状态
        btnToggleService.text = if (listenerEnabled) "打开通知使用权设置" else "开启通知使用权"
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val cn = ComponentName(this, "com.miclaw.notification.service.NotificationListenerServiceImpl")
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners") ?: ""
        return flat.contains(cn.flattenToString())
    }
}
