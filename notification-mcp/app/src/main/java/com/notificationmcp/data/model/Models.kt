package com.notificationmcp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationData(
    val id: Long = 0,
    val notification_id: Int = 0,
    val package_name: String = "",
    val app_name: String = "",
    val title: String? = null,
    val content: String? = null,
    val big_text: String? = null,
    val sub_text: String? = null,
    val info_text: String? = null,
    val channel_id: String? = null,
    val category: String? = null,
    val priority: Int = 0,
    val is_clearable: Boolean = true,
    val is_group_summary: Boolean = false,
    val group_key: String? = null,
    val conversation_title: String? = null,
    val conversation_messages: String? = null,
    val timestamp: Long = 0,
    val is_verification_code: Boolean = false,
    val classification: String? = null,
    val classification_confidence: Float = 0f,
    val is_urgent: Boolean = false
)

@Serializable
data class NotificationStats(
    val total_count: Int = 0,
    val today_count: Int = 0,
    val top_apps: List<AppStat> = emptyList(),
    val filtered_count: Int = 0,
    val hourly_distribution: List<HourlyStat> = emptyList()
)

@Serializable
data class AppStat(
    val package_name: String,
    val count: Int
)

@Serializable
data class HourlyStat(
    val hour: String,
    val count: Int
)

@Serializable
data class ClassificationResult(
    val notification_id: Long,
    val category: String,
    val confidence: Float,
    val is_urgent: Boolean
)

@Serializable
data class ConversationMessage(
    val sender: String? = null,
    val text: String? = null,
    val timestamp: Long = 0
)

@Serializable
data class ConversationThread(
    val app_name: String,
    val package_name: String,
    val contact: String? = null,
    val messages: List<ConversationMessage>,
    val last_timestamp: Long = 0
)

@Serializable
data class AutomationRule(
    val id: Long = 0,
    val name: String = "",
    val trigger_package: String? = null,
    val trigger_content_regex: String? = null,
    val trigger_category: String? = null,
    val trigger_priority_min: Int? = null,
    val action_type: String = "",
    val action_config: String = "{}",
    val is_enabled: Boolean = true,
    val match_count: Int = 0
)

@Serializable
data class WebhookConfig(
    val url: String = "",
    val secret: String? = null,
    val is_enabled: Boolean = false
)

@Serializable
data class AuditLogEntry(
    val id: Long = 0,
    val tool_name: String = "",
    val parameters_summary: String = "",
    val client_id: String? = null,
    val result_count: Int = 0,
    val timestamp: Long = 0
)

@Serializable
data class AnalyticsData(
    val hourly_heatmap: List<HourlyStat> = emptyList(),
    val top_apps_pie: List<AppStat> = emptyList(),
    val total_notifications: Int = 0,
    val avg_daily: Float = 0f
)

@Serializable
data class MCPToolResult(
    val content: List<MCPContent> = emptyList(),
    val isError: Boolean = false
)

@Serializable
data class MCPContent(
    val type: String = "text",
    val text: String = ""
)

@Serializable
data class PrivacySettings(
    val autoRedact: Boolean = true,
    val redactLevel: RedactLevel = RedactLevel.NORMAL,
    val confidentialApps: Set<String> = emptySet(),
    val verificationCodeExpiryMinutes: Int = 10
)

enum class RedactLevel {
    STRICT,   // 隐藏所有敏感信息
    NORMAL,   // 脱敏替换
    NONE      // 不脱敏
}
