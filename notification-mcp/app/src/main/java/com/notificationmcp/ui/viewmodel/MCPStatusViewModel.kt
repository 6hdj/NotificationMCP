package com.notificationmcp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notificationmcp.data.repository.NotificationRepository
import com.notificationmcp.mcp.MCPServer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MCPLogEntry(
    val timestamp: String,
    val message: String,
    val isError: Boolean = false
)

data class MCPStatusState(
    val isServerRunning: Boolean = false,
    val connectedClients: Int = 0,
    val todayCount: Int = 0,
    val totalCount: Int = 0,
    val logs: List<MCPLogEntry> = emptyList()
)

@HiltViewModel
class MCPStatusViewModel @Inject constructor(
    private val repository: NotificationRepository,
    private val mcpServer: MCPServer
) : ViewModel() {

    private val _state = MutableStateFlow(MCPStatusState())
    val state: StateFlow<MCPStatusState> = _state.asStateFlow()

    init {
        refreshState()
    }

    fun refreshState() {
        viewModelScope.launch {
            val todayCount = repository.getTodayCount()
            val totalCount = repository.getTotalCount()

            _state.value = _state.value.copy(
                isServerRunning = mcpServer.isRunning(),
                todayCount = todayCount,
                totalCount = totalCount
            )
        }
    }

    fun toggleServer() {
        if (mcpServer.isRunning()) {
            mcpServer.stop()
            addLog("MCP 服务已停止", isError = true)
        } else {
            mcpServer.start()
            addLog("MCP 服务已启动，端口 8765")
        }
        _state.value = _state.value.copy(isServerRunning = mcpServer.isRunning())
    }

    private fun addLog(message: String, isError: Boolean = false) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        val entry = MCPLogEntry(timestamp, message, isError)
        _state.value = _state.value.copy(
            logs = (listOf(entry) + _state.value.logs).take(50)
        )
    }
}
