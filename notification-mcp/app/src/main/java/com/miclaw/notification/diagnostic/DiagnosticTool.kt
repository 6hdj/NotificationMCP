package com.miclaw.notification.diagnostic

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * MCP 工具接口 — 一键故障排查
 *
 * 暴露给电脑端 Miclaw 的 MCP tool：
 *   - run_diagnostic     执行全链路诊断
 *   - auto_fix           一键自动修复
 *   - get_diagnostic_history  获取诊断历史
 */
object DiagnosticTool {

    private val gson = Gson()

    /**
     * 处理 MCP tool 调用
     */
    suspend fun handle(toolName: String, params: JsonObject, context: Context): String {
        return when (toolName) {
            "run_diagnostic" -> runDiagnostic(params, context)
            "auto_fix" -> autoFix(context)
            "get_diagnostic_history" -> getHistory(context)
            else -> errorResult("未知工具: $toolName")
        }
    }

    /**
     * 执行诊断
     */
    private suspend fun runDiagnostic(params: JsonObject, context: Context): String {
        val categoryParam = params.get("category")?.asString ?: "all"

        val engine = DiagnosticEngine(context)
        val report = if (categoryParam == "all") {
            engine.runFullDiagnostic()
        } else {
            val category = try {
                CheckCategory.valueOf(categoryParam.uppercase())
            } catch (e: Exception) {
                CheckCategory.PERMISSION
            }
            engine.runCategoryDiagnostic(category)
        }

        // 保存历史
        saveHistory(context, report)

        return gson.toJson(mapOf(
            "success" to true,
            "overallStatus" to report.overallStatus.name,
            "summary" to report.summary,
            "report" to report.toDisplayText(),
            "fixableCount" to report.fixableCount,
            "checkCount" to report.checks.size,
            "passCount" to report.checks.count { it.status == CheckStatus.PASS },
            "warnCount" to report.checks.count { it.status == CheckStatus.WARN },
            "failCount" to report.checks.count { it.status == CheckStatus.FAIL },
            "autoFixAvailable" to report.checks
                .filter { it.fixAction != null }
                .map { it.fixAction }
        ))
    }

    /**
     * 一键修复
     */
    private suspend fun autoFix(context: Context): String {
        // 先跑一遍诊断
        val engine = DiagnosticEngine(context)
        val report = engine.runFullDiagnostic()

        if (report.overallStatus == CheckStatus.PASS) {
            return gson.toJson(mapOf(
                "success" to true,
                "message" to "所有检查通过，无需修复",
                "results" to emptyList<String>()
            ))
        }

        val results = engine.executeAutoFix(report)

        return gson.toJson(mapOf(
            "success" to true,
            "message" to "已执行修复操作，请在弹出的设置页面中确认",
            "results" to results,
            "totalFixed" to results.count { it.startsWith("✅") },
            "totalFailed" to results.count { it.startsWith("❌") }
        ))
    }

    /**
     * 获取诊断历史
     */
    private fun getHistory(context: Context): String {
        val prefs = context.getSharedPreferences("diagnostic_history", Context.MODE_PRIVATE)
        val json = prefs.getString("recent_reports", "[]") ?: "[]"

        @Suppress("UNCHECKED_CAST")
        val history = try {
            gson.fromJson(json, List::class.java) as? List<Map<String, Any>> ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        return gson.toJson(mapOf(
            "success" to true,
            "history" to history.takeLast(10).reversed(),
            "totalCount" to history.size
        ))
    }

    /**
     * 保存诊断历史
     */
    private fun saveHistory(context: Context, report: DiagnosticReport) {
        val prefs = context.getSharedPreferences("diagnostic_history", Context.MODE_PRIVATE)
        val existing = prefs.getString("recent_reports", "[]") ?: "[]"

        @Suppress("UNCHECKED_CAST")
        val history = try {
            (gson.fromJson(existing, List::class.java) as? MutableList<Map<String, Any>>
                ?: mutableListOf()).toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }

        history.add(mapOf(
            "timestamp" to report.timestamp,
            "overallStatus" to report.overallStatus.name,
            "summary" to report.summary,
            "checkCount" to report.checks.size,
            "failCount" to report.checks.count { it.status == CheckStatus.FAIL },
            "warnCount" to report.checks.count { it.status == CheckStatus.WARN }
        ))

        // 保留最近 20 条
        val trimmed = if (history.size > 20) history.takeLast(20).toMutableList() else history
        prefs.edit().putString("recent_reports", gson.toJson(trimmed)).apply()
    }

    private fun errorResult(message: String): String {
        return gson.toJson(mapOf("success" to false, "error" to message))
    }
}
