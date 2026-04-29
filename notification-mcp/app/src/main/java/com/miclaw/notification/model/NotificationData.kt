package com.miclaw.notification.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 通知数据模型 —— 全字段无损解析
 */
@Entity(tableName = "notifications")
data class NotificationData(
    @PrimaryKey(autoGenerate = true)
    val dbId: Long = 0,

    // === 基础标识 ===
    val notificationId: Int,           // 系统通知ID
    val channelId: String?,            // 通知渠道ID
    val fingerprint: String,           // 去重指纹 (notificationId + packageName + postTime)
    val postTime: Long,                // 发送时间戳
    val receiveTime: Long,             // 接收时间戳

    // === 发送方信息 ===
    val packageName: String,           // APP包名
    val appName: String,               // APP名称
    val appVersion: String = "",       // APP版本号
    val uid: Int = 0,                  // UID

    // === 内容信息 ===
    val title: String = "",            // 通知标题
    val content: String = "",          // 通知正文
    val subText: String? = null,       // 子文本
    val bigText: String? = null,       // 大文本（展开后）
    val largeIcon: String? = null,     // 大图标路径
    val picture: String? = null,       // 图片附件路径

    // === 状态信息 ===
    val priority: Int = 0,             // 通知优先级
    val isOngoing: Boolean = false,    // 是否常驻通知
    val isClearable: Boolean = true,   // 是否可清除
    val isRead: Boolean = false,       // 是否已读
    val category: String? = null,      // 通知分类
    val isGroupSummary: Boolean = false, // 是否为折叠组摘要

    // === 动作信息 ===
    val actions: String = "[]",        // 快捷按钮动作列表 (JSON)

    // === 额外信息 ===
    val extras: String = "{}",         // 额外字段 (JSON)

    // === 传输状态 ===
    val forwarded: Boolean = false,    // 是否已转发到电脑端
    val forwardTime: Long? = null      // 转发时间
) {
    companion object {
        /**
         * 生成去重指纹: notificationId + packageName + postTime
         */
        fun generateFingerprint(notificationId: Int, packageName: String, postTime: Long): String {
            return "$notificationId|$packageName|$postTime"
        }
    }
}

/**
 * 过滤规则
 */
@Entity(tableName = "filter_rules")
data class FilterRule(
    @PrimaryKey
    val id: String,
    val packageName: String? = null,
    val appName: String? = null,
    val keywordPattern: String? = null,
    val priority: String? = null,
    val category: String? = null,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 脱敏规则
 */
@Entity(tableName = "masking_rules")
data class MaskingRule(
    @PrimaryKey
    val id: String,
    val name: String,
    val pattern: String,
    val replacement: String,
    val enabled: Boolean = true
)

/**
 * 服务统计信息
 */
data class ServiceStats(
    var totalReceived: Long = 0,
    var totalFiltered: Long = 0,
    var totalForwarded: Long = 0,
    var totalDuplicates: Long = 0,
    var totalErrors: Long = 0,
    var totalRestarts: Long = 0,
    var lastNotificationTime: Long? = null,
    var lastHeartbeatTime: Long? = null,
    var startTime: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "totalReceived" to totalReceived,
        "totalFiltered" to totalFiltered,
        "totalForwarded" to totalForwarded,
        "totalDuplicates" to totalDuplicates,
        "totalErrors" to totalErrors,
        "totalRestarts" to totalRestarts,
        "lastNotificationTime" to (lastNotificationTime?.let { formatTime(it) } ?: "N/A"),
        "lastHeartbeatTime" to (lastHeartbeatTime?.let { formatTime(it) } ?: "N/A")
    )

    private fun formatTime(ts: Long): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(ts))
    }
}

/**
 * 熔断器状态
 */
data class CircuitBreakerState(
    var state: String = "closed",       // closed / open / half-open
    var failureCount: Int = 0,
    var lastFailureTime: Long? = null,
    var lastSuccessTime: Long? = null
) {
    companion object {
        const val STATE_CLOSED = "closed"
        const val STATE_OPEN = "open"
        const val STATE_HALF_OPEN = "half-open"
        const val FAILURE_THRESHOLD = 10  // 连续失败10次触发熔断
    }
}
