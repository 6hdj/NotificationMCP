package com.notificationmcp.privacy

import com.notificationmcp.data.model.RedactLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrivacyEngine @Inject constructor() {

    // Regex patterns for sensitive data detection
    private val phonePattern = Regex("""1[3-9]\d{9}""")
    private val idCardPattern = Regex("""\d{17}[\dXx]""")
    private val bankCardPattern = Regex("""\d{16,19}""")

    // Strict verification code context — must appear as a prefix/label near the code
    // e.g. "验证码 123456" / "您的验证码是123456" / "verification code: 123456"
    private val verificationCodePatterns = listOf(
        Regex("""验证码\s*[:：]?\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""校验码\s*[:：]?\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""动态码\s*[:：]?\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""安全码\s*[:：]?\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""确认码\s*[:：]?\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""验证码\s*[:：]?\s*(\d{4,8})""", RegexOption.IGNORE_CASE),
        Regex("""verification\s*code\s*[:：]?\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""one[- ]?time\s*code\s*[:：]?\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""otp\s*[:：]?\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        // "您的验证码为123456" / "验证码123456，"
        Regex("""验证码\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""验证码\s*为\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""验证码\s*是\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""验证码\s*：\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
        Regex("""验证码\s*:\s*(\d{4,6})""", RegexOption.IGNORE_CASE),
    )

    // Words that indicate the number is NOT a verification code
    private val nonVerificationKeywords = listOf(
        "端口", "路径", "ip", "http", "https", "url", "地址",
        "port", "path", "host", "server", "服务", "在线",
        "版本", "version", "build", "sdk", "api",
        "内存", "cpu", "gpu", "电池", "电量", "存储",
        "温度", "湿度", "频率", "bandwidth", "speed",
        "价格", "金额", "余额", "积分", "score",
        "编号", "序号", "number", "no\\."
    )

    /**
     * Apply redaction based on the given level
     */
    fun redact(text: String?, level: RedactLevel): String? {
        if (text.isNullOrBlank()) return text
        return when (level) {
            RedactLevel.NONE -> text
            RedactLevel.NORMAL -> applyNormalRedaction(text)
            RedactLevel.STRICT -> applyStrictRedaction(text)
        }
    }

    private fun applyNormalRedaction(text: String): String {
        var result = text
        result = phonePattern.replace(result) { match ->
            val full = match.value
            "${full.take(3)}****${full.takeLast(4)}"
        }
        result = idCardPattern.replace(result, "[REDACTED-ID]")
        result = bankCardPattern.replace(result) { match ->
            val full = match.value
            if (full.length >= 16) "****${full.takeLast(4)}" else "[REDACTED-BANK]"
        }
        return result
    }

    private fun applyStrictRedaction(text: String): String {
        var result = text
        result = phonePattern.replace(result, "[REDACTED-PHONE]")
        result = idCardPattern.replace(result, "[REDACTED-ID]")
        result = bankCardPattern.replace(result, "[REDACTED-BANK]")
        result = result.replace(Regex("""[\w.]+@[\w.]+\.\w+"""), "[REDACTED-EMAIL]")
        return result
    }

    /**
     * Check if text contains a verification code and extract it.
     * Uses strict pattern matching: the number must appear right after a
     * verification code label/prefix (e.g. "验证码 123456").
     * Generic 4-6 digit numbers without a label are NOT treated as codes.
     */
    fun extractVerificationCode(text: String?): String? {
        if (text.isNullOrBlank()) return null
        for (pattern in verificationCodePatterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.groupValues[1]
            }
        }
        return null
    }

    /**
     * Check if a notification is verification code related.
     * Strict: must match a verification code pattern (label + number),
     * AND must not be in a non-verification context.
     */
    fun isVerificationCode(title: String?, content: String?): Boolean {
        val combined = "${title.orEmpty()} ${content.orEmpty()}"

        // Check for exclusion keywords first
        val lowerCombined = combined.lowercase()
        val hasExclusion = nonVerificationKeywords.any { lowerCombined.contains(it) }
        if (hasExclusion) return false

        // Must match a strict verification code pattern
        return extractVerificationCode(combined) != null
    }

    /**
     * Check if app is in confidential list
     */
    fun isConfidentialApp(packageName: String, confidentialApps: Set<String>): Boolean {
        return confidentialApps.any { packageName.contains(it, ignoreCase = true) }
    }
}
