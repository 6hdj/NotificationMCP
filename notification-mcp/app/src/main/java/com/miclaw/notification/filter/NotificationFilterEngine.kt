package com.miclaw.notification.filter

import android.util.Log
import com.miclaw.notification.NotificationMcpApp
import com.miclaw.notification.model.FilterRule
import com.miclaw.notification.model.NotificationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 通知过滤引擎
 *
 * 支持五种过滤维度，任意一条规则命中即过滤：
 * 1. 按包名过滤 — 屏蔽指定 APP 的所有通知
 * 2. 按 APP 名称过滤 — 模糊匹配
 * 3. 按关键词过滤 — 正则匹配标题和内容
 * 4. 按优先级过滤 — 只转发高优先级通知
 * 5. 按通知分类过滤 — Android 通知 category
 *
 * 过滤策略：白名单模式（只转发匹配的通知）或 黑名单模式（屏蔽匹配的通知）
 */
class NotificationFilterEngine {

    companion object {
        private const val TAG = "FilterEngine"

        /** 默认黑名单 — 这些 APP 的通知默认不转发 */
        private val DEFAULT_BLACKLIST = setOf(
            "com.android.systemui",
            "com.android.providers.downloads",
            "com.miui.securitycenter",
            "com.miui.cleanmaster",
            "com.miui.securityadd",
            "com.xiaomi.joyose",      // 小米推送服务
            "com.xiaomi.xmsf",        // 小米推送框架
            "com.google.android.gms", // Google Play Services
        )

                /** 高优先级 APP — 这些 APP 的通知始终转发 */
        private val PRIORITY_WHITELIST = setOf(
            "com.tencent.mm",         // 微信
            "com.tencent.mobileqq",   // QQ
            "com.alibaba.android.rimet", // 钉钉
            "com.eg.android.AlipayGphone", // 支付宝
            "com.autonavi.minimap",   // 高德地图
            "com.ss.android.ugc.aweme", // 抖音
        )
    }

    /**
     * 判断通知是否应该被过滤（不转发）
     *
     * @return true = 应该过滤（不转发），false = 正常转发
     */
    suspend fun shouldFilter(data: NotificationData): Boolean = withContext(Dispatchers.IO) {
        // 1. 高优先级 APP 永不过滤
        if (data.packageName in PRIORITY_WHITELIST) {
            return@withContext false
        }

        // 2. 默认黑名单过滤
        if (data.packageName in DEFAULT_BLACKLIST) {
            Log.d(TAG, "过滤 [默认黑名单]: ${data.appName}")
            return@withContext true
        }

        // 3. 自定义规则过滤
        val rules = NotificationMcpApp.instance.database.filterRuleDao().getEnabled()
        for (rule in rules) {
            if (matchRule(data, rule)) {
                Log.d(TAG, "过滤 [规则 ${rule.id}]: ${data.appName} - ${data.title}")
                return@withContext true
            }
        }

        return@withContext false
    }

    /**
     * 检查通知是否匹配某条过滤规则
     */
    private fun matchRule(data: NotificationData, rule: FilterRule): Boolean {
        // 包名匹配
        if (rule.packageName != null && data.packageName.contains(rule.packageName, ignoreCase = true)) {
            return true
        }

        // APP 名称匹配
        if (rule.appName != null && data.appName.contains(rule.appName, ignoreCase = true)) {
            return true
        }

        // 关键词正则匹配
        if (rule.keywordPattern != null) {
            val regex = Regex(rule.keywordPattern, RegexOption.IGNORE_CASE)
            if (regex.containsMatchIn(data.title) || regex.containsMatchIn(data.content)) {
                return true
            }
        }

        // 优先级匹配
        if (rule.priority != null) {
            val minPriority = rule.priority.toIntOrNull() ?: return false
            if (data.priority <= minPriority) {
                return true
            }
        }

        // 分类匹配
        if (rule.category != null && data.category == rule.category) {
            return true
        }

        return false
    }

    /**
     * 获取所有过滤规则
     */
    suspend fun getAllRules(): List<FilterRule> = withContext(Dispatchers.IO) {
        NotificationMcpApp.instance.database.filterRuleDao().getAll()
    }

    /**
     * 添加过滤规则
     */
    suspend fun addRule(rule: FilterRule) = withContext(Dispatchers.IO) {
        NotificationMcpApp.instance.database.filterRuleDao().insert(rule)
        Log.i(TAG, "添加过滤规则: ${rule.id}")
    }

    /**
     * 删除过滤规则
     */
    suspend fun deleteRule(ruleId: String) = withContext(Dispatchers.IO) {
        NotificationMcpApp.instance.database.filterRuleDao().deleteById(ruleId)
        Log.i(TAG, "删除过滤规则: $ruleId")
    }

    /**
     * 启用/禁用过滤规则
     */
    suspend fun setRuleEnabled(ruleId: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        NotificationMcpApp.instance.database.filterRuleDao().setEnabled(ruleId, enabled)
        Log.i(TAG, "过滤规则 $ruleId: ${if (enabled) "启用" else "禁用"}")
    }
}
