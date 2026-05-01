package com.notificationmcp.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.notificationmcp.data.model.NotificationStats
import com.notificationmcp.data.model.AppStat
import com.notificationmcp.data.repository.NotificationRepository
import com.notificationmcp.mcp.MCPServer
import com.notificationmcp.service.NotificationListenerServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val isServerRunning: Boolean = false,
    val isListenerEnabled: Boolean = false,
    val connectedClients: Int = 0,
    val todayCount: Int = 0,
    val totalCount: Int = 0,
    val filteredCount: Int = 0,
    val topApps: List<AppStat> = emptyList(),
    val searchQuery: String = "",
    val searchResult: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    private val repository: NotificationRepository,
    private val mcpServer: MCPServer
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

        init {
        refreshState()
    }

    /**
     * 每次页面可见时调用，确保 MCP 状态实时更新
     */
    fun refreshState() {
        viewModelScope.launch {
            val isListenerEnabled = isNotificationListenerEnabled()
            val todayCount = repository.getTodayCount()
            val totalCount = repository.getTotalCount()
            val filteredCount = repository.getFilteredCount()
            val topApps = repository.getTopApps(5).map { AppStat(it.packageName, it.count) }

            _state.value = _state.value.copy(
                isServerRunning = mcpServer.isRunning(),
                isListenerEnabled = isListenerEnabled,
                todayCount = todayCount,
                totalCount = totalCount,
                filteredCount = filteredCount,
                topApps = topApps
            )
        }
    }

    /**
     * 仅刷新 MCP 服务器状态（轻量级，用于从其他页面返回时）
     */
    fun refreshServerStatus() {
        _state.value = _state.value.copy(
            isServerRunning = mcpServer.isRunning()
        )
    }

    fun toggleServer() {
        if (mcpServer.isRunning()) {
            mcpServer.stop()
        } else {
            mcpServer.start()
        }
        _state.value = _state.value.copy(isServerRunning = mcpServer.isRunning())
    }

    fun openNotificationListenerSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
    }

    fun openBatteryOptimization() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun executeSearch() {
        val query = _state.value.searchQuery
        if (query.isBlank()) return

        viewModelScope.launch {
            val results = repository.searchNotifications(contentKeyword = query, limit = 5)
            val summary = if (results.isEmpty()) {
                "未找到匹配的通知"
            } else {
                "找到 ${results.size} 条匹配：\n" + results.joinToString("\n") { notif ->
                    "• [${notif.appName}] ${notif.title ?: notif.content?.take(50) ?: "无标题"}"
                }
            }
            _state.value = _state.value.copy(searchResult = summary)
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val pkgName = getApplication<Application>().packageName
        val flat = Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return flat.contains(ComponentName(getApplication(), NotificationListenerServiceImpl::class.java).flattenToString())
    }
}
