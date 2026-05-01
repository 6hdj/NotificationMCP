package com.notificationmcp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.notificationmcp.data.model.AuditLogEntry
import com.notificationmcp.data.model.RedactLevel
import com.notificationmcp.data.repository.NotificationRepository
import com.notificationmcp.ui.theme.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class SettingsState(
    // Service
    val serviceEnabled: Boolean = true,
    // Appearance
    val darkMode: Boolean = true,
    val liquidGlassEnabled: Boolean = true,
    // Notification
    val filterEnabled: Boolean = true,
    val aiSummaryEnabled: Boolean = false,
    val autoCopyEnabled: Boolean = true,
    // Privacy
    val autoRedact: Boolean = true,
    val redactLevel: RedactLevel = RedactLevel.NORMAL,
    val confidentialApps: Set<String> = emptySet(),
    val retentionDays: Int = 7,
    val transparency: Float = 0.25f,
    // Audit
    val auditLogs: List<AuditLogEntry> = emptyList(),
    val showAuditLogs: Boolean = false,
    // Export
    val exportMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val repository: NotificationRepository
) : AndroidViewModel(application) {

            private val _state = MutableStateFlow(
        SettingsState(
            liquidGlassEnabled = PreferencesManager.isLiquidGlassEnabled(),
            filterEnabled = PreferencesManager.isFilterEnabled(),
            aiSummaryEnabled = PreferencesManager.isAISummaryEnabled(),
            autoCopyEnabled = PreferencesManager.isAutoCopyEnabled(),
            autoRedact = PreferencesManager.isAutoRedactEnabled(),
            redactLevel = PreferencesManager.getRedactLevel()
        )
    )
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    // ── Toggle methods for SettingsScreen ──
    fun toggleService() {
        _state.value = _state.value.copy(serviceEnabled = !_state.value.serviceEnabled)
    }

    fun toggleDarkMode() {
        _state.value = _state.value.copy(darkMode = !_state.value.darkMode)
    }

        fun toggleFilter() {
        val newValue = !_state.value.filterEnabled
        _state.value = _state.value.copy(filterEnabled = newValue)
        PreferencesManager.setFilterEnabled(newValue)
    }

        fun toggleAISummary() {
        val newValue = !_state.value.aiSummaryEnabled
        _state.value = _state.value.copy(aiSummaryEnabled = newValue)
        PreferencesManager.setAISummaryEnabled(newValue)
    }

            fun toggleAutoCopy() {
        val newValue = !_state.value.autoCopyEnabled
        _state.value = _state.value.copy(autoCopyEnabled = newValue)
        PreferencesManager.setAutoCopyEnabled(newValue)
    }

        fun toggleLiquidGlass() {
        val newValue = !_state.value.liquidGlassEnabled
        _state.value = _state.value.copy(liquidGlassEnabled = newValue)
        PreferencesManager.setLiquidGlassEnabled(newValue)
    }

    fun clearData() {
        viewModelScope.launch {
            repository.softDeleteAll()
        }
    }

    // ── Privacy settings ──
        fun setAutoRedact(enabled: Boolean) {
        _state.value = _state.value.copy(autoRedact = enabled)
        PreferencesManager.setAutoRedact(enabled)
    }

    fun setRedactLevel(level: RedactLevel) {
        _state.value = _state.value.copy(redactLevel = level)
        PreferencesManager.setRedactLevel(level)
    }

    fun addConfidentialApp(packageName: String) {
        _state.value = _state.value.copy(
            confidentialApps = _state.value.confidentialApps + packageName
        )
    }

    fun removeConfidentialApp(packageName: String) {
        _state.value = _state.value.copy(
            confidentialApps = _state.value.confidentialApps - packageName
        )
    }

    fun setRetentionDays(days: Int) {
        _state.value = _state.value.copy(retentionDays = days)
    }

    fun setTransparency(value: Float) {
        _state.value = _state.value.copy(transparency = value)
    }

    // ── Audit logs ──
    fun loadAuditLogs() {
        viewModelScope.launch {
            val logs = repository.getAuditLogs(100).map { log ->
                AuditLogEntry(
                    id = log.id,
                    tool_name = log.toolName,
                    parameters_summary = log.parametersSummary,
                    client_id = log.clientId,
                    result_count = log.resultCount,
                    timestamp = log.timestamp
                )
            }
            _state.value = _state.value.copy(auditLogs = logs, showAuditLogs = true)
        }
    }

    fun hideAuditLogs() {
        _state.value = _state.value.copy(showAuditLogs = false)
    }

    // ── Export ──
    fun exportData(format: String) {
        viewModelScope.launch {
            try {
                val notifications = repository.getAllForExport(5000)
                val dir = File(getApplication<Application>().filesDir, "exports")
                dir.mkdirs()

                val timestamp = System.currentTimeMillis()
                val file = when (format) {
                    "csv" -> {
                        val f = File(dir, "notifications_$timestamp.csv")
                        f.writeText(buildCSV(notifications))
                        f
                    }
                    else -> {
                        val f = File(dir, "notifications_$timestamp.json")
                        f.writeText(buildJSON(notifications))
                        f
                    }
                }

                _state.value = _state.value.copy(
                    exportMessage = "导出完成: ${file.absolutePath}"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    exportMessage = "导出失败: ${e.message}"
                )
            }
        }
    }

    private fun buildCSV(notifications: List<com.notificationmcp.data.db.entity.NotificationEntity>): String {
        val sb = StringBuilder()
        sb.appendLine("ID,Package,App,Title,Content,Category,Priority,Timestamp")
        for (n in notifications) {
            sb.appendLine(
                "${n.id},${esc(n.packageName)},${esc(n.appName)},${esc(n.title ?: "")}," +
                "${esc(n.content ?: "")},${esc(n.category ?: "")},${n.priority},${n.timestamp}"
            )
        }
        return sb.toString()
    }

    private fun buildJSON(notifications: List<com.notificationmcp.data.db.entity.NotificationEntity>): String {
        val sb = StringBuilder("[")
        notifications.forEachIndexed { i, n ->
            if (i > 0) sb.append(",")
            sb.append("{")
            sb.append("\"id\":${n.id},")
            sb.append("\"package_name\":\"${esc(n.packageName)}\",")
            sb.append("\"app_name\":\"${esc(n.appName)}\",")
            sb.append("\"title\":\"${esc(n.title ?: "")}\",")
            sb.append("\"content\":\"${esc(n.content ?: "")}\",")
            sb.append("\"category\":\"${esc(n.category ?: "")}\",")
            sb.append("\"timestamp\":${n.timestamp}")
            sb.append("}")
        }
        sb.append("]")
        return sb.toString()
    }

    private fun esc(s: String) = s.replace("\"", "\"\"").replace("\n", " ")
}
