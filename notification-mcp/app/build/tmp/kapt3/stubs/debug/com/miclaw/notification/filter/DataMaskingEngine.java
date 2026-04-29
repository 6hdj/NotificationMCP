package com.miclaw.notification.filter;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u0000 \u001d2\u00020\u0001:\u0001\u001dB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0002J\u0018\u0010\u000b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0016\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00060\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u001e\u0010\u0016\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u000e\b\u0002\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00060\u0010J\u001e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\r\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010\u001c\u00a8\u0006\u001e"}, d2 = {"Lcom/miclaw/notification/filter/DataMaskingEngine;", "", "()V", "addRule", "", "rule", "Lcom/miclaw/notification/model/MaskingRule;", "(Lcom/miclaw/notification/model/MaskingRule;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "applyBuiltinRules", "", "text", "applyCustomRule", "deleteRule", "ruleId", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllRules", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "mask", "Lcom/miclaw/notification/model/NotificationData;", "data", "(Lcom/miclaw/notification/model/NotificationData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "previewMask", "rules", "setRuleEnabled", "", "enabled", "", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class DataMaskingEngine {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "DataMaskingEngine";
    
    /**
     * 手机号（中国大陆 11 位）
     */
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex REGEX_PHONE = null;
    
    /**
     * 身份证号（18 位）
     */
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex REGEX_ID_CARD = null;
    
    /**
     * 银行卡号（16-19 位）
     */
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex REGEX_BANK_CARD = null;
    
    /**
     * 邮箱地址
     */
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex REGEX_EMAIL = null;
    
    /**
     * 验证码（4-6 位纯数字，常见于短信验证码场景）
     */
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex REGEX_VERIFICATION_CODE = null;
    
    /**
     * IP 地址
     */
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex REGEX_IP = null;
    
    /**
     * 密码关键词后的文本
     */
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex REGEX_PASSWORD = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.miclaw.notification.filter.DataMaskingEngine.Companion Companion = null;
    
    public DataMaskingEngine() {
        super();
    }
    
    /**
     * 对通知数据进行脱敏处理
     * 返回脱敏后的新对象（不修改原始数据）
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object mask(@org.jetbrains.annotations.NotNull()
    com.miclaw.notification.model.NotificationData data, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.miclaw.notification.model.NotificationData> $completion) {
        return null;
    }
    
    /**
     * 应用内置脱敏规则
     */
    private final java.lang.String applyBuiltinRules(java.lang.String text) {
        return null;
    }
    
    /**
     * 应用自定义脱敏规则
     */
    private final java.lang.String applyCustomRule(java.lang.String text, com.miclaw.notification.model.MaskingRule rule) {
        return null;
    }
    
    /**
     * 获取所有脱敏规则
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllRules(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.miclaw.notification.model.MaskingRule>> $completion) {
        return null;
    }
    
    /**
     * 添加脱敏规则
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addRule(@org.jetbrains.annotations.NotNull()
    com.miclaw.notification.model.MaskingRule rule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    /**
     * 删除脱敏规则
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteRule(@org.jetbrains.annotations.NotNull()
    java.lang.String ruleId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    /**
     * 启用/禁用脱敏规则
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setRuleEnabled(@org.jetbrains.annotations.NotNull()
    java.lang.String ruleId, boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 预览脱敏效果（不实际修改数据）
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String previewMask(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.util.List<com.miclaw.notification.model.MaskingRule> rules) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/miclaw/notification/filter/DataMaskingEngine$Companion;", "", "()V", "REGEX_BANK_CARD", "Lkotlin/text/Regex;", "REGEX_EMAIL", "REGEX_ID_CARD", "REGEX_IP", "REGEX_PASSWORD", "REGEX_PHONE", "REGEX_VERIFICATION_CODE", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}