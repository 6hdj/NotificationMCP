package com.notificationmcp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Pre-built automation rule templates for the template market.
 * Each template auto-fills all configuration fields.
 */
data class AutomationTemplate(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val accentColor: Color,
    val icon: ImageVector,
    // Pre-filled config
    val triggerTemplateType: String? = null,
    val triggerTimeMode: String = "always",
    val actionAutoCopy: Boolean = false,
    val actionForceSound: Boolean = false,
    val actionPinInSummary: Boolean = false,
    val actionSilentRemove: Boolean = false,
    val actionType: String = "report",
    val actionConfig: String = "{}",
    val triggerContentRegex: String? = null,
    val selectedApps: String? = null // JSON array of package names
)

val automationTemplates = listOf(
    AutomationTemplate(
        id = "sms_otp",
        emoji = "🔑",
        title = "验证码自动复制",
        description = "短信验证码自动复制到剪贴板，省去手动输入",
        accentColor = Color(0xFF34C759),
        icon = Icons.Filled.ContentCopy,
        triggerTemplateType = "verification_code",
        actionAutoCopy = true,
        actionType = "clipboard"
    ),
    AutomationTemplate(
        id = "wechat_girlfriend",
        emoji = "💌",
        title = "微信消息强提醒",
        description = "微信消息强制声音提醒，静音也不放过",
        accentColor = Color(0xFFFF2D55),
        icon = Icons.Filled.NotificationsActive,
        triggerTemplateType = "messaging",
        selectedApps = """["com.tencent.mm"]""",
        actionForceSound = true,
        actionType = "sound"
    ),
    AutomationTemplate(
        id = "spam_filter",
        emoji = "♻️",
        title = "自动清理垃圾广告",
        description = "检测广告关键词，自动静默移除",
        accentColor = Color(0xFF8E8E93),
        icon = Icons.Filled.Block,
        triggerTemplateType = "keyword",
        triggerContentRegex = ".*(退订|取消|回复TD|中奖|免费领取|恭喜您).*",
        actionSilentRemove = true,
        actionType = "silent_remove"
    ),
    AutomationTemplate(
        id = "expense_tracker",
        emoji = "💳",
        title = "消费提醒自动记账",
        description = "银行卡消费通知转发到记账 Webhook",
        accentColor = Color(0xFFFF9500),
        icon = Icons.Filled.AccountBalance,
        triggerTemplateType = "amount",
        actionType = "webhook",
        actionConfig = """{"url":""}"""
    ),
    AutomationTemplate(
        id = "logistics",
        emoji = "📦",
        title = "快递物流追踪",
        description = "自动识别快递通知，置顶到 AI 摘要",
        accentColor = Color(0xFF007AFF),
        icon = Icons.Filled.LocalShipping,
        triggerTemplateType = "logistics",
        actionPinInSummary = true,
        actionType = "notification"
    ),
    AutomationTemplate(
        id = "important_pin",
        emoji = "⭐",
        title = "重要通知置顶",
        description = "包含「重要」「紧急」的通知自动置顶到 AI 摘要",
        accentColor = Color(0xFFAF52DE),
        icon = Icons.Filled.PushPin,
        triggerContentRegex = ".*(重要|紧急|urgent|important).*",
        actionPinInSummary = true,
        actionType = "notification"
    )
)

/**
 * Trigger template types for the semantic selector
 */
data class TriggerTemplate(
    val type: String,
    val label: String,
    val emoji: String,
    val description: String,
    val defaultRegex: String? = null
)

val triggerTemplates = listOf(
    TriggerTemplate("verification_code", "验证码", "🔑", "短信中的数字验证码", "\\d{4,6}"),
    TriggerTemplate("amount", "金额消费", "💰", "包含 ¥/$ 金额的消费提醒", "[¥$€£]\\s*\\d+[.,]?\\d*"),
    TriggerTemplate("messaging", "即时通讯", "💬", "MessagingStyle 通知（微信/QQ等）", null),
    TriggerTemplate("logistics", "物流快递", "📦", "快递单号或物流更新", "\\d{12,15}"),
    TriggerTemplate("keyword", "自定义关键词", "🏷️", "包含特定关键词的通知", null)
)

/**
 * Time mode options
 */
data class TimeModeOption(
    val mode: String,
    val label: String,
    val emoji: String
)

val timeModeOptions = listOf(
    TimeModeOption("always", "始终", "🕐"),
    TimeModeOption("daytime", "仅白天 8:00-22:00", "☀️"),
    TimeModeOption("nighttime", "仅夜间 22:00-8:00", "🌙"),
    TimeModeOption("custom", "自定义时段", "⚙️")
)

/**
 * Action toggle definitions
 */
data class ActionToggle(
    val key: String,
    val label: String,
    val description: String,
    val emoji: String,
    val color: Color
)

val actionToggles = listOf(
    ActionToggle(
        key = "auto_copy",
        label = "自动复制验证码",
        description = "检测到验证码后自动复制到剪贴板",
        emoji = "📋",
        color = Color(0xFF34C759)
    ),
    ActionToggle(
        key = "force_sound",
        label = "重要通知强提醒",
        description = "强制播放提示音，即使手机静音",
        emoji = "🔊",
        color = Color(0xFFFF9500)
    ),
    ActionToggle(
        key = "webhook",
        label = "转发到 Webhook",
        description = "将通知转发到你的 URL",
        emoji = "🌐",
        color = Color(0xFF007AFF)
    ),
    ActionToggle(
        key = "pin_summary",
        label = "AI 摘要置顶",
        description = "标记为重要，AI 摘要时自动置顶",
        emoji = "⭐",
        color = Color(0xFFAF52DE)
    ),
    ActionToggle(
        key = "silent_remove",
        label = "过滤废通知",
        description = "从 MCP 输出和数据库中自动静默移除",
        emoji = "🗑️",
        color = Color(0xFFFF3B30)
    )
)
