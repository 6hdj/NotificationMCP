package com.notificationmcp.ui.theme

import android.content.Context
import android.content.SharedPreferences
import com.notificationmcp.data.model.RedactLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 管理所有应用设置的持久化存储
 */
object PreferencesManager {
    private const val PREFS_NAME = "notification_mcp_prefs"
    
    // Keys
    private const val KEY_LIQUID_GLASS = "liquid_glass_enabled"
    private const val KEY_FILTER_ENABLED = "filter_enabled"
    private const val KEY_AI_SUMMARY_ENABLED = "ai_summary_enabled"
    private const val KEY_AUTO_COPY_ENABLED = "auto_copy_enabled"
    private const val KEY_AUTO_REDACT = "auto_redact"
    private const val KEY_REDACT_LEVEL = "redact_level"

    private lateinit var prefs: SharedPreferences

    // ── In-memory state for Compose observation ──
    private val _liquidGlassEnabled = MutableStateFlow(true)
    val liquidGlassEnabled: StateFlow<Boolean> = _liquidGlassEnabled.asStateFlow()

    private val _filterEnabled = MutableStateFlow(true)
    val filterEnabled: StateFlow<Boolean> = _filterEnabled.asStateFlow()

    private val _aiSummaryEnabled = MutableStateFlow(false)
    val aiSummaryEnabled: StateFlow<Boolean> = _aiSummaryEnabled.asStateFlow()

    private val _autoCopyEnabled = MutableStateFlow(true)
    val autoCopyEnabled: StateFlow<Boolean> = _autoCopyEnabled.asStateFlow()

    private val _autoRedact = MutableStateFlow(true)
    val autoRedact: StateFlow<Boolean> = _autoRedact.asStateFlow()

    private val _redactLevel = MutableStateFlow(RedactLevel.NORMAL)
    val redactLevel: StateFlow<RedactLevel> = _redactLevel.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _liquidGlassEnabled.value = prefs.getBoolean(KEY_LIQUID_GLASS, true)
        _filterEnabled.value = prefs.getBoolean(KEY_FILTER_ENABLED, true)
        _aiSummaryEnabled.value = prefs.getBoolean(KEY_AI_SUMMARY_ENABLED, false)
        _autoCopyEnabled.value = prefs.getBoolean(KEY_AUTO_COPY_ENABLED, true)
        _autoRedact.value = prefs.getBoolean(KEY_AUTO_REDACT, true)
        _redactLevel.value = try {
            RedactLevel.valueOf(prefs.getString(KEY_REDACT_LEVEL, RedactLevel.NORMAL.name) ?: RedactLevel.NORMAL.name)
        } catch (_: Exception) {
            RedactLevel.NORMAL
        }
    }

    // ── Setters ──
    fun setLiquidGlassEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LIQUID_GLASS, enabled).apply()
        _liquidGlassEnabled.value = enabled
    }

    fun setFilterEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FILTER_ENABLED, enabled).apply()
        _filterEnabled.value = enabled
    }

    fun setAISummaryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AI_SUMMARY_ENABLED, enabled).apply()
        _aiSummaryEnabled.value = enabled
    }

    fun setAutoCopyEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_COPY_ENABLED, enabled).apply()
        _autoCopyEnabled.value = enabled
    }

    fun setAutoRedact(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_REDACT, enabled).apply()
        _autoRedact.value = enabled
    }

    fun setRedactLevel(level: RedactLevel) {
        prefs.edit().putString(KEY_REDACT_LEVEL, level.name).apply()
        _redactLevel.value = level
    }

    // ── Getters ──
    fun isLiquidGlassEnabled(): Boolean = _liquidGlassEnabled.value
    fun isFilterEnabled(): Boolean = _filterEnabled.value
    fun isAISummaryEnabled(): Boolean = _aiSummaryEnabled.value
    fun isAutoCopyEnabled(): Boolean = _autoCopyEnabled.value
    fun isAutoRedactEnabled(): Boolean = _autoRedact.value
    fun getRedactLevel(): RedactLevel = _redactLevel.value
}
