package com.miclaw.notification.db;

/**
 * 通知数据 DAO — 高频读写优化
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b!\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\r\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ&\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\f0\u00112\u0006\u0010\u0012\u001a\u00020\u000e2\b\b\u0002\u0010\u0013\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0014J$\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\f0\u00112\u0006\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u001e\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\f0\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u0016\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u001f\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010 \u001a\b\u0012\u0004\u0012\u00020\f0\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u000e\u0010!\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\"\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010$J\"\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u0010&\u001a\b\u0012\u0004\u0012\u00020\f0\u0011H\u00a7@\u00a2\u0006\u0002\u0010\'J$\u0010(\u001a\u00020\n2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\u0006\u0010*\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010+J\u001e\u0010,\u001a\u00020\n2\u0006\u0010-\u001a\u00020\u00072\u0006\u0010*\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\u0018J&\u0010.\u001a\b\u0012\u0004\u0012\u00020\f0\u00112\u0006\u0010/\u001a\u00020\u000e2\b\b\u0002\u0010\u0013\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0014J.\u00100\u001a\b\u0012\u0004\u0012\u00020\f0\u00112\u0006\u0010/\u001a\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\u00072\b\b\u0002\u0010\u0013\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u00101\u00a8\u00062"}, d2 = {"Lcom/miclaw/notification/db/NotificationDao;", "", "cleanupAllForwarded", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "cleanupOldForwarded", "cutoffTime", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "", "findByFingerprint", "Lcom/miclaw/notification/model/NotificationData;", "fingerprint", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getByPackage", "", "pkg", "limit", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getByTimeRange", "startTime", "endTime", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getRecent", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTodayCount", "dayStart", "getTodayFilteredCount", "getTodayForwardedCount", "getTotalCount", "getUnforwarded", "getUnforwardedCount", "insert", "notification", "(Lcom/miclaw/notification/model/NotificationData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertAll", "notifications", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markBatchForwarded", "dbIds", "forwardTime", "(Ljava/util/List;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markForwarded", "dbId", "searchByKeyword", "keyword", "searchByKeywordInTimeRange", "(Ljava/lang/String;JILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface NotificationDao {
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.miclaw.notification.model.NotificationData notification, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.miclaw.notification.model.NotificationData> notifications, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.Long>> $completion);
    
    /**
     * 按指纹查重
     */
    @androidx.room.Query(value = "SELECT * FROM notifications WHERE fingerprint = :fingerprint LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findByFingerprint(@org.jetbrains.annotations.NotNull()
    java.lang.String fingerprint, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.miclaw.notification.model.NotificationData> $completion);
    
    /**
     * 获取未转发的通知（按时间排序）
     */
    @androidx.room.Query(value = "SELECT * FROM notifications WHERE forwarded = 0 ORDER BY postTime ASC LIMIT :limit")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUnforwarded(int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.miclaw.notification.model.NotificationData>> $completion);
    
    /**
     * 按包名查询
     */
    @androidx.room.Query(value = "SELECT * FROM notifications WHERE packageName = :pkg ORDER BY postTime DESC LIMIT :limit")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByPackage(@org.jetbrains.annotations.NotNull()
    java.lang.String pkg, int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.miclaw.notification.model.NotificationData>> $completion);
    
    /**
     * 按关键词搜索标题和内容
     */
    @androidx.room.Query(value = "\n        SELECT * FROM notifications \n        WHERE title LIKE \'%\' || :keyword || \'%\' \n           OR content LIKE \'%\' || :keyword || \'%\'\n        ORDER BY postTime DESC \n        LIMIT :limit\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchByKeyword(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword, int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.miclaw.notification.model.NotificationData>> $completion);
    
    /**
     * 按关键词搜索标题和内容（限定时间范围，避免全表扫描）
     */
    @androidx.room.Query(value = "\n        SELECT * FROM notifications \n        WHERE postTime >= :startTime\n          AND (title LIKE \'%\' || :keyword || \'%\' \n               OR content LIKE \'%\' || :keyword || \'%\')\n        ORDER BY postTime DESC \n        LIMIT :limit\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchByKeywordInTimeRange(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword, long startTime, int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.miclaw.notification.model.NotificationData>> $completion);
    
    /**
     * 获取最近 N 条通知
     */
    @androidx.room.Query(value = "SELECT * FROM notifications ORDER BY postTime DESC LIMIT :limit")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getRecent(int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.miclaw.notification.model.NotificationData>> $completion);
    
    /**
     * 获取指定时间范围内的通知
     */
    @androidx.room.Query(value = "SELECT * FROM notifications WHERE postTime BETWEEN :startTime AND :endTime ORDER BY postTime DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByTimeRange(long startTime, long endTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.miclaw.notification.model.NotificationData>> $completion);
    
    /**
     * 获取今日通知统计
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM notifications WHERE postTime >= :dayStart")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTodayCount(long dayStart, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * 获取今日已转发数
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM notifications WHERE postTime >= :dayStart AND forwarded = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTodayForwardedCount(long dayStart, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * 获取今日过滤数
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM notifications WHERE postTime >= :dayStart AND forwarded = 0 AND isRead = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTodayFilteredCount(long dayStart, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * 获取未转发通知数量
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM notifications WHERE forwarded = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUnforwardedCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * 获取数据库总条数
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM notifications")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTotalCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    /**
     * 标记已转发
     */
    @androidx.room.Query(value = "UPDATE notifications SET forwarded = 1, forwardTime = :forwardTime WHERE dbId = :dbId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object markForwarded(long dbId, long forwardTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 批量标记已转发
     */
    @androidx.room.Query(value = "UPDATE notifications SET forwarded = 1, forwardTime = :forwardTime WHERE dbId IN (:dbIds)")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object markBatchForwarded(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> dbIds, long forwardTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 删除已转发且超过 N 天的通知
     */
    @androidx.room.Query(value = "DELETE FROM notifications WHERE forwarded = 1 AND postTime < :cutoffTime")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object cleanupOldForwarded(long cutoffTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * 删除所有已转发通知
     */
    @androidx.room.Query(value = "DELETE FROM notifications WHERE forwarded = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object cleanupAllForwarded(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * 清空全部数据
     */
    @androidx.room.Query(value = "DELETE FROM notifications")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 通知数据 DAO — 高频读写优化
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}