package com.miclaw.notification.filter

import android.util.Log
import com.miclaw.notification.NotificationMcpApp
import com.miclaw.notification.model.MaskingRule
import com.miclaw.notification.model.NotificationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 数据脱敏引擎
 *
 * 在通知转发到电脑端之前，对敏感信息进行脱敏处理。
 * 支持三种脱敏模式：
 * 1. 正则替换 — 用正则匹配敏感字段并替换为掩码
 * 2. 关键词替换 — 匹配关键词后脱敏其后 N 个字符
 * 3. 字段级脱敏 — 对整个字段（如 content）进行脱敏
 *
 * 内置脱敏规则：
 * - 手机号：138****1234
 * - 身份证号：110***********1234
 * - 银行卡号：6222 **** **** 1234
 * - 邮箱：z***g@example.com
 * - 验证码：[验证码已脱敏]
 */
class DataMaskingEngine {

    companion object {
        private const val TAG = "DataMaskingEngine"

        // ═══════════════════════════════════════
        //  内置正则模式
        // ═══════════════════════════════════════

        /** 手机号（中国大陆 11 位） */
        private val REGEX_PHONE = Regex("(1[3-9]\\d)\\d{4}(\\d{4})")

        /** 身份证号（18 位） */
        private val REGEX_ID_CARD = Regex("(\\d{3})\\d{11}(\\d{4})")

        /** 银行卡号（16-19 位） */
        private val REGEX_BANK_CARD = Regex("(\\d{4})\\d{8,12}(\\d{4})")

        /** 邮箱地址 */
        private val REGEX_EMAIL = Regex("([a-zA-Z0-9])[a-zA-Z0-9.]*@([a-zA-Z0-9.]+)")

        /** 验证码（4-6 位纯数字，常见于短信验证码场景） */
        private val REGEX_VERIFICATION_CODE = Regex("[验证码校验码确认码动态码]{0,4}[：:]?\\s*(\\d{4,6})")

        /** IP 地址 */
        private val REGEX_IP = Regex("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})")

        /** 密码关键词后的文本 */
        private val REGEX_PASSWORD = Regex("[密码口令passpwd]{1,4}[：:=]?\\s*(\\S{4,})", RegexOption.IGNORE_CASE)
    }

    /**
     * 对通知数据进行脱敏处理
     * 返回脱敏后的新对象（不修改原始数据）
     */
    suspend fun mask(data: NotificationData): NotificationData = withContext(Dispatchers.IO) {
        val rules = NotificationMcpApp.instance.database.maskingRuleDao().getEnabled()

        var maskedTitle = data.title
        var maskedContent = data.content
        var maskedBigText = data.bigText
        var maskedSubText = data.subText

        // 1. 应用内置规则
        maskedTitle = applyBuiltinRules(maskedTitle)
        maskedContent = applyBuiltinRules(maskedContent)
        maskedBigText = maskedBigText?.let { applyBuiltinRules(it) }
        maskedSubText = maskedSubText?.let { applyBuiltinRules(it) }

        // 2. 应用自定义规则
        for (rule in rules) {
            maskedTitle = applyCustomRule(maskedTitle, rule)
            maskedContent = applyCustomRule(maskedContent, rule)
            maskedBigText = maskedBigText?.let { applyCustomRule(it, rule) }
            maskedSubText = maskedSubText?.let { applyCustomRule(it, rule) }
        }

        // 3. 如果内容被修改，标记已脱敏
        val isMasked = maskedTitle != data.title ||
                maskedContent != data.content ||
                maskedBigText != data.bigText ||
                maskedSubText != data.subText

        return@withContext if (isMasked) {
            data.copy(
                title = maskedTitle,
                content = maskedContent,
                bigText = maskedBigText,
                subText = maskedSubText
            )
        } else {
            data
        }
    }

    /**
     * 应用内置脱敏规则
     */
    private fun applyBuiltinRules(text: String): String {
        if (text.isEmpty()) return text

        var result = text

        // 手机号脱敏：138****1234
        result = REGEX_PHONE.replace(result) { match ->
            "${match.groupValues[1]}****${match.groupValues[2]}"
        }

        // 身份证脱敏：110***********1234
        result = REGEX_ID_CARD.replace(result) { match ->
            "${match.groupValues[1]}***********${match.groupValues[2]}"
        }

        // 银行卡脱敏：6222 **** **** 1234
        result = REGEX_BANK_CARD.replace(result) { match ->
            "${match.groupValues[1]} **** **** ${match.groupValues[2]}"
        }

        // 邮箱脱敏：z***g@example.com
        result = REGEX_EMAIL.replace(result) { match ->
            "${match.groupValues[1]}***@${match.groupValues[2]}"
        }

        // 验证码脱敏
        result = REGEX_VERIFICATION_CODE.replace(result) { match ->
            "[验证码已脱敏]"
        }

        // 密码脱敏
        result = REGEX_PASSWORD.replace(result) { match ->
            "密码：******"
        }

        return result
    }

    /**
     * 应用自定义脱敏规则
     */
    private fun applyCustomRule(text: String, rule: MaskingRule): String {
        if (text.isEmpty() || rule.pattern.isEmpty()) return text

        return try {
            val regex = Regex(rule.pattern, RegexOption.IGNORE_CASE)
            regex.replace(text, rule.replacement)
        } catch (e: Exception) {
            Log.w(TAG, "自定义脱敏规则 ${rule.id} 正则解析失败: ${e.message}")
            text
        }
    }

    /**
     * 获取所有脱敏规则
     */
    suspend fun getAllRules(): List<MaskingRule> = withContext(Dispatchers.IO) {
        NotificationMcpApp.instance.database.maskingRuleDao().getAll()
    }

    /**
     * 添加脱敏规则
     */
    suspend fun addRule(rule: MaskingRule) = withContext(Dispatchers.IO) {
        NotificationMcpApp.instance.database.maskingRuleDao().insert(rule)
        Log.i(TAG, "添加脱敏规则: ${rule.id} - ${rule.name}")
    }

    /**
     * 删除脱敏规则
     */
    suspend fun deleteRule(ruleId: String) = withContext(Dispatchers.IO) {
        NotificationMcpApp.instance.database.maskingRuleDao().deleteById(ruleId)
        Log.i(TAG, "删除脱敏规则: $ruleId")
    }

    /**
     * 启用/禁用脱敏规则
     */
    suspend fun setRuleEnabled(ruleId: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        NotificationMcpApp.instance.database.maskingRuleDao().setEnabled(ruleId, enabled)
    }

    /**
     * 预览脱敏效果（不实际修改数据）
     */
    fun previewMask(text: String, rules: List<MaskingRule> = emptyList()): String {
        var result = applyBuiltinRules(text)
        for (rule in rules) {
            result = applyCustomRule(result, rule)
        }
        return result
    }
}
