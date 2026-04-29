package com.miclaw.notification.filter;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\u0007J\u0016\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00060\rH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u001e\u0010\u0013\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u0016\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0017\u00a8\u0006\u0019"}, d2 = {"Lcom/miclaw/notification/filter/NotificationFilterEngine;", "", "()V", "addRule", "", "rule", "Lcom/miclaw/notification/model/FilterRule;", "(Lcom/miclaw/notification/model/FilterRule;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteRule", "ruleId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllRules", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "matchRule", "", "data", "Lcom/miclaw/notification/model/NotificationData;", "setRuleEnabled", "enabled", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "shouldFilter", "(Lcom/miclaw/notification/model/NotificationData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class NotificationFilterEngine {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "FilterEngine";
    
    /**
     * 默认黑名单 — 这些 APP 的通知默认不转发
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> DEFAULT_BLACKLIST = null;
    
    /**
     * 高优先级 APP — 这些 APP 的通知始终转发
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> PRIORITY_WHITELIST = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.miclaw.notification.filter.NotificationFilterEngine.Companion Companion = null;
    
    public NotificationFilterEngine() {
        super();
    }
    
    /**
     * 判断通知是否应该被过滤（不转发）
     *
     * @return true = 应该过滤（不转发），false = 正常转发
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object shouldFilter(@org.jetbrains.annotations.NotNull()
    com.miclaw.notification.model.NotificationData data, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * 检查通知是否匹配某条过滤规则
     */
    private final boolean matchRule(com.miclaw.notification.model.NotificationData data, com.miclaw.notification.model.FilterRule rule) {
        return false;
    }
    
    /**
     * 获取所有过滤规则
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllRules(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.miclaw.notification.model.FilterRule>> $completion) {
        return null;
    }
    
    /**
     * 添加过滤规则
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addRule(@org.jetbrains.annotations.NotNull()
    com.miclaw.notification.model.FilterRule rule, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    /**
     * 删除过滤规则
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteRule(@org.jetbrains.annotations.NotNull()
    java.lang.String ruleId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    /**
     * 启用/禁用过滤规则
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setRuleEnabled(@org.jetbrains.annotations.NotNull()
    java.lang.String ruleId, boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/miclaw/notification/filter/NotificationFilterEngine$Companion;", "", "()V", "DEFAULT_BLACKLIST", "", "", "PRIORITY_WHITELIST", "TAG", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}