package com.miclaw.notification.model;

/**
 * 服务统计信息
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b,\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010$\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001Bc\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\fJ\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0003H\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010*\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJ\u0010\u0010+\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJ\t\u0010,\u001a\u00020\u0003H\u00c6\u0003Jl\u0010-\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010.J\u0013\u0010/\u001a\u0002002\b\u00101\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\u0010\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\u0003H\u0002J\t\u00105\u001a\u000206H\u00d6\u0001J\u0012\u00107\u001a\u000e\u0012\u0004\u0012\u000203\u0012\u0004\u0012\u00020\u000108J\t\u00109\u001a\u000203H\u00d6\u0001R\u001e\u0010\n\u001a\u0004\u0018\u00010\u0003X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u0011\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001e\u0010\t\u001a\u0004\u0018\u00010\u0003X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u0011\u001a\u0004\b\u0012\u0010\u000e\"\u0004\b\u0013\u0010\u0010R\u001a\u0010\u000b\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0015\"\u0004\b\u0019\u0010\u0017R\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0015\"\u0004\b\u001b\u0010\u0017R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u0015\"\u0004\b\u001d\u0010\u0017R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u0015\"\u0004\b\u001f\u0010\u0017R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u0015\"\u0004\b!\u0010\u0017R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u0015\"\u0004\b#\u0010\u0017\u00a8\u0006:"}, d2 = {"Lcom/miclaw/notification/model/ServiceStats;", "", "totalReceived", "", "totalFiltered", "totalForwarded", "totalDuplicates", "totalErrors", "totalRestarts", "lastNotificationTime", "lastHeartbeatTime", "startTime", "(JJJJJJLjava/lang/Long;Ljava/lang/Long;J)V", "getLastHeartbeatTime", "()Ljava/lang/Long;", "setLastHeartbeatTime", "(Ljava/lang/Long;)V", "Ljava/lang/Long;", "getLastNotificationTime", "setLastNotificationTime", "getStartTime", "()J", "setStartTime", "(J)V", "getTotalDuplicates", "setTotalDuplicates", "getTotalErrors", "setTotalErrors", "getTotalFiltered", "setTotalFiltered", "getTotalForwarded", "setTotalForwarded", "getTotalReceived", "setTotalReceived", "getTotalRestarts", "setTotalRestarts", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(JJJJJJLjava/lang/Long;Ljava/lang/Long;J)Lcom/miclaw/notification/model/ServiceStats;", "equals", "", "other", "formatTime", "", "ts", "hashCode", "", "toMap", "", "toString", "app_debug"})
public final class ServiceStats {
    private long totalReceived;
    private long totalFiltered;
    private long totalForwarded;
    private long totalDuplicates;
    private long totalErrors;
    private long totalRestarts;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long lastNotificationTime;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long lastHeartbeatTime;
    private long startTime;
    
    public ServiceStats(long totalReceived, long totalFiltered, long totalForwarded, long totalDuplicates, long totalErrors, long totalRestarts, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastNotificationTime, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastHeartbeatTime, long startTime) {
        super();
    }
    
    public final long getTotalReceived() {
        return 0L;
    }
    
    public final void setTotalReceived(long p0) {
    }
    
    public final long getTotalFiltered() {
        return 0L;
    }
    
    public final void setTotalFiltered(long p0) {
    }
    
    public final long getTotalForwarded() {
        return 0L;
    }
    
    public final void setTotalForwarded(long p0) {
    }
    
    public final long getTotalDuplicates() {
        return 0L;
    }
    
    public final void setTotalDuplicates(long p0) {
    }
    
    public final long getTotalErrors() {
        return 0L;
    }
    
    public final void setTotalErrors(long p0) {
    }
    
    public final long getTotalRestarts() {
        return 0L;
    }
    
    public final void setTotalRestarts(long p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLastNotificationTime() {
        return null;
    }
    
    public final void setLastNotificationTime(@org.jetbrains.annotations.Nullable()
    java.lang.Long p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLastHeartbeatTime() {
        return null;
    }
    
    public final void setLastHeartbeatTime(@org.jetbrains.annotations.Nullable()
    java.lang.Long p0) {
    }
    
    public final long getStartTime() {
        return 0L;
    }
    
    public final void setStartTime(long p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Object> toMap() {
        return null;
    }
    
    private final java.lang.String formatTime(long ts) {
        return null;
    }
    
    public ServiceStats() {
        super();
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final long component3() {
        return 0L;
    }
    
    public final long component4() {
        return 0L;
    }
    
    public final long component5() {
        return 0L;
    }
    
    public final long component6() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component8() {
        return null;
    }
    
    public final long component9() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.miclaw.notification.model.ServiceStats copy(long totalReceived, long totalFiltered, long totalForwarded, long totalDuplicates, long totalErrors, long totalRestarts, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastNotificationTime, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastHeartbeatTime, long startTime) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}