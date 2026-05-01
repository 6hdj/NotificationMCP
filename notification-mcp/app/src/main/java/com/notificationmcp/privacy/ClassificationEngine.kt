package com.notificationmcp.privacy

import com.notificationmcp.data.db.entity.NotificationEntity
import com.notificationmcp.data.model.ClassificationResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rule-based notification classifier.
 * TODO: Replace with TFLite model for better accuracy.
 */
@Singleton
class ClassificationEngine @Inject constructor() {

    private data class Rule(
        val category: String,
        val keywords: List<String>,
        val packagePatterns: List<String>,
        val confidence: Float,
        val urgentKeywords: List<String> = emptyList()
    )

    private val rules = listOf(
        Rule(
            category = "社交",
            keywords = listOf("消息", "聊天", "通话", "语音", "视频", "好友", "群聊", "朋友圈", "评论", "点赞", "回复", "私信"),
            packagePatterns = listOf("com.tencent.mm", "com.tencent.mobileqq", "com.alibaba.android.rimet", "com.ss.android.ugc.aweme"),
            confidence = 0.85f,
            urgentKeywords = listOf("通话", "语音", "视频")
        ),
        Rule(
            category = "金融",
            keywords = listOf("转账", "支付", "收款", "余额", "账单", "还款", "贷款", "理财", "基金", "股票", "交易", "提现", "充值"),
            packagePatterns = listOf("com.eg.android.AlipayGphone", "com.icbc", "com.chinamworld.bocmbci", "cmb.pb"),
            confidence = 0.9f,
            urgentKeywords = listOf("转账", "还款", "逾期")
        ),
        Rule(
            category = "快递",
            keywords = listOf("快递", "包裹", "签收", "派送", "取件", "驿站", "物流", "运单", "已发出", "运输中"),
            packagePatterns = listOf("com.sf.activity", "com.yunda", "com.zto", "com.cainiao"),
            confidence = 0.88f
        ),
        Rule(
            category = "验证码",
            keywords = listOf("验证码", "校验码", "动态码", "安全码", "确认码", "verification", "code", "otp"),
            packagePatterns = emptyList(),
            confidence = 0.95f,
            urgentKeywords = listOf("验证码")
        ),
        Rule(
            category = "促销",
            keywords = listOf("优惠", "折扣", "促销", "红包", "满减", "秒杀", "限时", "抢购", "券", "返现", "特价"),
            packagePatterns = listOf("com.taobao.taobao", "com.jingdong.app.mall", "com.xunmeng.pinduoduo"),
            confidence = 0.75f
        ),
        Rule(
            category = "系统",
            keywords = listOf("更新", "升级", "安全", "备份", "同步", "存储", "电量", "温度"),
            packagePatterns = listOf("com.android", "com.miui", "com.huawei"),
            confidence = 0.7f
        )
    )

    fun classify(notification: NotificationEntity): ClassificationResult {
        val text = "${notification.title.orEmpty()} ${notification.content.orEmpty()}".lowercase()
        val pkg = notification.packageName.lowercase()

        var bestMatch: Rule? = null
        var bestScore = 0f

        for (rule in rules) {
            var score = 0f

            // Package name match (high weight)
            if (rule.packagePatterns.any { pkg.contains(it) }) {
                score += rule.confidence * 0.6f
            }

            // Keyword match
            val matchedKeywords = rule.keywords.filter { text.contains(it) }
            if (matchedKeywords.isNotEmpty()) {
                score += rule.confidence * 0.4f * (matchedKeywords.size.toFloat() / rule.keywords.size).coerceAtMost(1f)
            }

            if (score > bestScore) {
                bestScore = score
                bestMatch = rule
            }
        }

        val category = bestMatch?.category ?: "未知"
        val confidence = if (bestMatch != null) (bestScore.coerceIn(0f, 1f)) else 0.1f
        val isUrgent = bestMatch?.urgentKeywords?.any { text.contains(it) } ?: false

        return ClassificationResult(
            notification_id = notification.id,
            category = category,
            confidence = confidence,
            is_urgent = isUrgent
        )
    }

    fun classifyBatch(notifications: List<NotificationEntity>): List<ClassificationResult> {
        return notifications.map { classify(it) }
    }
}
