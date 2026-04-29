package com.miclaw.notification.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.miclaw.notification.R
import com.miclaw.notification.diagnostic.CheckCategory
import com.miclaw.notification.diagnostic.CheckItem
import com.miclaw.notification.diagnostic.CheckStatus
import com.miclaw.notification.diagnostic.DiagnosticEngine
import com.miclaw.notification.diagnostic.DiagnosticReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 一键故障排查 UI 界面
 *
 * 用户可以：
 * 1. 点击「开始诊断」执行全链路排查
 * 2. 查看每个检查项的详细结果
 * 3. 点击「一键修复」自动修复所有可修复的问题
 * 4. 点击单个失败项跳转到对应的设置页面
 */
class DiagnosticActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvSummary: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnDiagnose: Button
    private lateinit var btnAutoFix: Button
    private lateinit var containerChecks: LinearLayout
    private lateinit var scrollResult: ScrollView
    private lateinit var tvStats: TextView

    private val scope = CoroutineScope(Dispatchers.Main)
    private var lastReport: DiagnosticReport? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnostic)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        tvStatus = findViewById(R.id.tv_status)
        tvSummary = findViewById(R.id.tv_summary)
        progressBar = findViewById(R.id.progress_bar)
        btnDiagnose = findViewById(R.id.btn_diagnose)
        btnAutoFix = findViewById(R.id.btn_auto_fix)
        containerChecks = findViewById(R.id.container_checks)
        scrollResult = findViewById(R.id.scroll_result)
        tvStats = findViewById(R.id.tv_stats)

        scrollResult.visibility = View.GONE
        btnAutoFix.visibility = View.GONE
    }

    private fun setupListeners() {
        btnDiagnose.setOnClickListener {
            startDiagnosis()
        }

        btnAutoFix.setOnClickListener {
            executeAutoFix()
        }
    }

    private fun startDiagnosis() {
        // UI 状态：诊断中
        tvStatus.text = "🔍 正在执行全链路诊断..."
        tvStatus.setTextColor(getColor(R.color.diagnostic_running))
        tvSummary.text = ""
        progressBar.visibility = View.VISIBLE
        btnDiagnose.isEnabled = false
        btnAutoFix.visibility = View.GONE
        containerChecks.removeAllViews()
        scrollResult.visibility = View.GONE

        scope.launch {
            try {
                val engine = DiagnosticEngine(this@DiagnosticActivity)
                val report = withContext(Dispatchers.IO) {
                    engine.runFullDiagnostic()
                }

                lastReport = report
                displayReport(report)

            } catch (e: Exception) {
                tvStatus.text = "❌ 诊断执行失败"
                tvStatus.setTextColor(getColor(R.color.diagnostic_fail))
                tvSummary.text = "错误: ${e.message}"
                progressBar.visibility = View.GONE
                btnDiagnose.isEnabled = true
            }
        }
    }

    private fun displayReport(report: DiagnosticReport) {
        progressBar.visibility = View.GONE
        btnDiagnose.isEnabled = true
        scrollResult.visibility = View.VISIBLE

        // 状态标题
        val (statusText, statusColor) = when (report.overallStatus) {
            CheckStatus.PASS -> "✅ 诊断完成 — 一切正常" to getColor(R.color.diagnostic_pass)
            CheckStatus.WARN -> "⚠️ 诊断完成 — 发现警告" to getColor(R.color.diagnostic_warn)
            CheckStatus.FAIL -> "❌ 诊断完成 — 发现问题" to getColor(R.color.diagnostic_fail)
            CheckStatus.INFO -> "❓ 诊断完成 — 部分项无法检测" to getColor(R.color.diagnostic_warn)
        }
        tvStatus.text = statusText
        tvStatus.setTextColor(statusColor)

        // 摘要
        tvSummary.text = report.summary

        // 统计
        val passCount = report.checks.count { it.status == CheckStatus.PASS }
        val warnCount = report.checks.count { it.status == CheckStatus.WARN }
        val failCount = report.checks.count { it.status == CheckStatus.FAIL }
        tvStats.text = "✅ $passCount 通过  ⚠️ $warnCount 警告  ❌ $failCount 失败  |  ${report.checks.size} 项检查"

        // 一键修复按钮
        val fixableItems = report.checks.filter { it.fixAction != null }
        if (fixableItems.isNotEmpty()) {
            btnAutoFix.visibility = View.VISIBLE
            btnAutoFix.text = "🔧 一键修复 (${fixableItems.size} 项)"
        } else {
            btnAutoFix.visibility = View.GONE
        }

        // 检查项列表
        containerChecks.removeAllViews()
        val grouped = report.checks.groupBy { it.category }

        for ((category, items) in grouped) {
            // 类别标题
            val categoryView = createCategoryHeader(category, items)
            containerChecks.addView(categoryView)

            // 检查项
            for (item in items) {
                val itemView = createCheckItemView(item)
                containerChecks.addView(itemView)
            }
        }
    }

    private fun createCategoryHeader(category: CheckCategory, items: List<CheckItem>): View {
        val hasFail = items.any { it.status == CheckStatus.FAIL }
        val hasWarn = items.any { it.status == CheckStatus.WARN }

        val icon = when {
            hasFail -> "❌"
            hasWarn -> "⚠️"
            else -> "✅"
        }

        return TextView(this).apply {
            text = "$icon ${category.label}"
            textSize = 16f
            setTextColor(getColor(
                when {
                    hasFail -> R.color.diagnostic_fail
                    hasWarn -> R.color.diagnostic_warn
                    else -> R.color.diagnostic_pass
                }
            ))
            setPadding(0, 24, 0, 8)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }

    private fun createCheckItemView(item: CheckItem): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 12, 16, 12)
            setBackgroundColor(getColor(R.color.diagnostic_item_bg))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 4, 0, 4)
            layoutParams = params
        }

        // 状态 + 名称
        val statusIcon = when (item.status) {
            CheckStatus.PASS -> "✅"
            CheckStatus.WARN -> "⚠️"
            CheckStatus.FAIL -> "❌"
            CheckStatus.INFO -> "❓"
        }

        val titleView = TextView(this).apply {
            text = "$statusIcon ${item.name}"
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        layout.addView(titleView)

        // 详情
        val detailView = TextView(this).apply {
            text = item.detail
            textSize = 12f
            setPadding(20, 4, 0, 0)
        }
        layout.addView(detailView)

        // 修复按钮（仅失败/警告项）
        if (item.fixAction != null && item.fixIntent != null) {
            val fixButton = TextView(this).apply {
                text = "🔧 ${item.fixAction}"
                textSize = 12f
                setTextColor(getColor(R.color.diagnostic_fix))
                setPadding(20, 8, 0, 0)
                setOnClickListener {
                    try {
                        val intent = Intent(item.fixIntent).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (item.fixIntent.contains("BATTERY_OPTIMIZATIONS")) {
                                data = android.net.Uri.parse("package:${packageName}")
                            }
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        // 尝试备用 intent
                        try {
                            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = android.net.Uri.parse("package:$packageName")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                        } catch (e2: Exception) {
                            // 静默失败
                        }
                    }
                }
            }
            layout.addView(fixButton)
        }

        return layout
    }

    private fun executeAutoFix() {
        val report = lastReport ?: return

        btnAutoFix.isEnabled = false
        btnAutoFix.text = "🔧 正在修复..."

        scope.launch {
            try {
                val engine = DiagnosticEngine(this@DiagnosticActivity)
                val results = withContext(Dispatchers.IO) {
                    engine.executeAutoFix(report)
                }

                // 显示修复结果
                val resultText = results.joinToString("\n")
                tvSummary.text = "修复结果:\n$resultText"

                btnAutoFix.text = "✅ 修复完成"
                btnAutoFix.isEnabled = false

                // 3 秒后允许重新诊断
                kotlinx.coroutines.delay(3000)
                btnAutoFix.visibility = View.GONE
                btnDiagnose.isEnabled = true

            } catch (e: Exception) {
                btnAutoFix.text = "❌ 修复失败: ${e.message}"
                btnAutoFix.isEnabled = true
            }
        }
    }
}
