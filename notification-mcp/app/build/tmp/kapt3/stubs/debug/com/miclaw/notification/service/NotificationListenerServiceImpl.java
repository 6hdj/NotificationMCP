package com.miclaw.notification.service;

/**
 * 通知监听服务 — 核心入口
 *
 * 职责：
 * 1. 实时捕获系统通知栏的每一条通知
 * 2. 全字段无损解析通知内容
 * 3. 去重过滤（基于 notificationId + packageName + postTime 指纹）
 * 4. 写入本地数据库（Room）
 * 5. 通过 WebSocket 实时转发到电脑端 Miclaw
 * 6. 异常时自动重启并补传积压通知
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u0000 )2\u00020\u0001:\u0001)B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\tH\u0002J\u0016\u0010\u000f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u0011H\u0082@\u00a2\u0006\u0002\u0010\u0012J\b\u0010\u0013\u001a\u00020\rH\u0016J\b\u0010\u0014\u001a\u00020\rH\u0016J\b\u0010\u0015\u001a\u00020\rH\u0016J\b\u0010\u0016\u001a\u00020\rH\u0016J\u0010\u0010\u0017\u001a\u00020\r2\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\r2\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001b\u001a\u00020\t2\u0006\u0010\u001c\u001a\u00020\u001dH\u0002J\u0010\u0010\u001e\u001a\u00020\t2\u0006\u0010\u001f\u001a\u00020 H\u0002J\u0012\u0010!\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0016\u0010\"\u001a\u00020\r2\u0006\u0010\u0018\u001a\u00020\u0019H\u0082@\u00a2\u0006\u0002\u0010#J\u000e\u0010$\u001a\u00020\rH\u0082@\u00a2\u0006\u0002\u0010%J\u000e\u0010&\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010%J\u0010\u0010\'\u001a\u00020(2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2 = {"Lcom/miclaw/notification/service/NotificationListenerServiceImpl;", "Landroid/service/notification/NotificationListenerService;", "()V", "isProcessing", "Ljava/util/concurrent/atomic/AtomicBoolean;", "maxFingerprintCache", "", "processedFingerprints", "Ljava/util/concurrent/ConcurrentLinkedQueue;", "", "scope", "Lkotlinx/coroutines/CoroutineScope;", "addToFingerprintCache", "", "fingerprint", "forwardToDesktop", "data", "Lcom/miclaw/notification/model/NotificationData;", "(Lcom/miclaw/notification/model/NotificationData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onCreate", "onDestroy", "onListenerConnected", "onListenerDisconnected", "onNotificationPosted", "sbn", "Landroid/service/notification/StatusBarNotification;", "onNotificationRemoved", "parseActions", "notification", "Landroid/app/Notification;", "parseExtras", "extras", "Landroid/os/Bundle;", "parseNotification", "processNotification", "(Landroid/service/notification/StatusBarNotification;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "replayActiveNotifications", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "replayUnforwardedNotifications", "shouldSkip", "", "Companion", "app_debug"})
public final class NotificationListenerServiceImpl extends android.service.notification.NotificationListenerService {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "NotificationListener";
    
    /**
     * 服务是否存活的全局标志
     */
    @kotlin.jvm.Volatile()
    private static volatile boolean isRunning = false;
    
    /**
     * 服务统计信息
     */
    @org.jetbrains.annotations.NotNull()
    private static final com.miclaw.notification.model.ServiceStats stats = null;
    
    /**
     * 通知处理回调（供 UI 和诊断使用）
     */
    @org.jetbrains.annotations.Nullable()
    private static kotlin.jvm.functions.Function1<? super com.miclaw.notification.model.NotificationData, kotlin.Unit> onNotificationReceived;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.atomic.AtomicBoolean isProcessing = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentLinkedQueue<java.lang.String> processedFingerprints = null;
    private final int maxFingerprintCache = 500;
    @org.jetbrains.annotations.NotNull()
    public static final com.miclaw.notification.service.NotificationListenerServiceImpl.Companion Companion = null;
    
    public NotificationListenerServiceImpl() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @java.lang.Override()
    public void onListenerConnected() {
    }
    
    @java.lang.Override()
    public void onListenerDisconnected() {
    }
    
    @java.lang.Override()
    public void onNotificationPosted(@org.jetbrains.annotations.NotNull()
    android.service.notification.StatusBarNotification sbn) {
    }
    
    @java.lang.Override()
    public void onNotificationRemoved(@org.jetbrains.annotations.NotNull()
    android.service.notification.StatusBarNotification sbn) {
    }
    
    private final java.lang.Object processNotification(android.service.notification.StatusBarNotification sbn, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 全字段无损解析 StatusBarNotification
     */
    private final com.miclaw.notification.model.NotificationData parseNotification(android.service.notification.StatusBarNotification sbn) {
        return null;
    }
    
    private final java.lang.String parseActions(android.app.Notification notification) {
        return null;
    }
    
    private final java.lang.String parseExtras(android.os.Bundle extras) {
        return null;
    }
    
    private final java.lang.Object forwardToDesktop(com.miclaw.notification.model.NotificationData data, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 重放当前活跃通知（服务连接后调用）
     */
    private final java.lang.Object replayActiveNotifications(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 补传积压的未转发通知（WebSocket 重连后调用）
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object replayUnforwardedNotifications(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 判断是否应跳过该通知
     */
    private final boolean shouldSkip(android.service.notification.StatusBarNotification sbn) {
        return false;
    }
    
    private final void addToFingerprintCache(java.lang.String fingerprint) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0006@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR(\u0010\t\u001a\u0010\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f\u0018\u00010\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0015"}, d2 = {"Lcom/miclaw/notification/service/NotificationListenerServiceImpl$Companion;", "", "()V", "TAG", "", "<set-?>", "", "isRunning", "()Z", "onNotificationReceived", "Lkotlin/Function1;", "Lcom/miclaw/notification/model/NotificationData;", "", "getOnNotificationReceived", "()Lkotlin/jvm/functions/Function1;", "setOnNotificationReceived", "(Lkotlin/jvm/functions/Function1;)V", "stats", "Lcom/miclaw/notification/model/ServiceStats;", "getStats", "()Lcom/miclaw/notification/model/ServiceStats;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * 服务是否存活的全局标志
         */
        public final boolean isRunning() {
            return false;
        }
        
        /**
         * 服务统计信息
         */
        @org.jetbrains.annotations.NotNull()
        public final com.miclaw.notification.model.ServiceStats getStats() {
            return null;
        }
        
        /**
         * 通知处理回调（供 UI 和诊断使用）
         */
        @org.jetbrains.annotations.Nullable()
        public final kotlin.jvm.functions.Function1<com.miclaw.notification.model.NotificationData, kotlin.Unit> getOnNotificationReceived() {
            return null;
        }
        
        /**
         * 通知处理回调（供 UI 和诊断使用）
         */
        public final void setOnNotificationReceived(@org.jetbrains.annotations.Nullable()
        kotlin.jvm.functions.Function1<? super com.miclaw.notification.model.NotificationData, kotlin.Unit> p0) {
        }
    }
}