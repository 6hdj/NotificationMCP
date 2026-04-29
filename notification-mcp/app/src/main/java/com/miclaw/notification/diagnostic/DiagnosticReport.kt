package com.miclaw.notification.diagnostic

/**
 * 诊断报告数据模型
 * 一次完整诊断的所有结果、统计和修复建议
 */

/** 单项检查结果 */
data class CheckItem(
    val name: String,                    // 检查项名称
    val category: CheckCategory,         // 所属类别
    val status: CheckStatus,             // 检查结果
    val detail: String,                  // 详细说明
    val fixAction: String? = null,       // 修复动作描述（null 表示无需修复）
    val fixIntent: String? = null,       // 修复跳转 Intent action
    val fixExtra: Map<String, String>? = null // 修复跳转附加参数
)

/** 检查类别 */
enum class CheckCategory(val label: String) {
    PERMISSION("权限校验"),
    SERVICE("服务状态"),
    SYSTEM_BLOCK("系统拦截"),
    CONNECTION("连接状态"),
    RESOURCE("资源状态"),
    LOG("日志分析")
}

/** 检查状态 */
enum class CheckStatus(val icon: String, val label: String) {
    PASS("✅", "正常"),
    WARN("⚠️", "警告"),
    FAIL("❌", "异常"),
    INFO("ℹ️", "提示")
}

/** 完整诊断报告 */
data class DiagnosticReport(
    val timestamp: Long,
    val deviceModel: String,
    val systemVersion: String,
    val checks: List<CheckItem>
) {
    val overallStatus: CheckStatus
        get() = when {
            checks.any { it.status == CheckStatus.FAIL } -> CheckStatus.FAIL
            checks.any { it.status == CheckStatus.WARN } -> CheckStatus.WARN
            else -> CheckStatus.PASS
        }

    val summary: String
        get() {
            val fail = checks.count { it.status == CheckStatus.FAIL }
            val warn = checks.count { it.status == CheckStatus.WARN }
            val pass = checks.count { it.status == CheckStatus.PASS }
            return "共 ${checks.size} 项检查：$pass 通过 / $warn 警告 / $fail 异常"
        }

    val fixableCount: Int
        get() = checks.count { it.fixAction != null }

    /** 按类别分组 */
    fun groupedByCategory(): Map<CheckCategory, List<CheckItem>> =
        checks.groupBy { it.category }

    /** 生成可读文本报告 */
    fun toDisplayText(): String {
        val sb = StringBuilder()
        sb.appendLine("═══════════════════════════════════════")
        sb.appendLine("  📋 通知读取服务 — 全链路诊断报告")
        sb.appendLine("═══════════════════════════════════════")
        sb.appendLine("📱 设备: $deviceModel")
        sb.appendLine("🔧 系统: $systemVersion")
        sb.appendLine("🕐 时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(timestamp)}")
        sb.appendLine("📊 结果: $summary")
        sb.appendLine()

        for ((category, items) in groupedByCategory()) {
            val catIcon = when {
                items.any { it.status == CheckStatus.FAIL } -> "❌"
                items.any { it.status == CheckStatus.WARN } -> "⚠️"
                else -> "✅"
            }
            sb.appendLine("$catIcon ${category.label}")
            sb.appendLine("─".repeat(40))
            for (item in items) {
                sb.appendLine("  ${item.status.icon} ${item.name}: ${item.detail}")
                if (item.fixAction != null) {
                    sb.appendLine("     🔧 修复: ${item.fixAction}")
                }
            }
            sb.appendLine()
        }

        if (fixableCount > 0) {
            sb.appendLine("💡 发现 $fixableCount 个可自动修复的问题")
            sb.appendLine("   说「一键修复」即可自动处理")
        } else {
            sb.appendLine("✅ 所有检查通过，无需修复")
        }
        sb.appendLine("═══════════════════════════════════════")
        return sb.toString()
    }

    /** 生成 JSON 摘要 */
    fun toJsonSummary(): Map<String, Any> = mapOf(
        "timestamp" to timestamp,
        "deviceModel" to deviceModel,
        "systemVersion" to systemVersion,
        "overallStatus" to overallStatus.name,
        "summary" to summary,
        "fixableCount" to fixableCount,
        "checks" to checks.map { mapOf(
            "name" to it.name,
            "category" to it.category.name,
            "status" to it.status.name,
            "detail" to it.detail,
            "fixAction" to (it.fixAction ?: "")
        )}
    )
}
