package com.miclaw.notification.diagnostic;

/**
 * 完整诊断报告
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\u0002\u0010\nJ\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0003J7\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0001J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\u0018\u0010$\u001a\u0014\u0012\u0004\u0012\u00020&\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0%J\t\u0010\'\u001a\u00020\u0010H\u00d6\u0001J\u0006\u0010(\u001a\u00020\u0005J\u0012\u0010)\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010%J\t\u0010*\u001a\u00020\u0005H\u00d6\u0001R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u00108F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0013\u001a\u00020\u00148F\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0017\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b\u0018\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001b\u00a8\u0006+"}, d2 = {"Lcom/miclaw/notification/diagnostic/DiagnosticReport;", "", "timestamp", "", "deviceModel", "", "systemVersion", "checks", "", "Lcom/miclaw/notification/diagnostic/CheckItem;", "(JLjava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "getChecks", "()Ljava/util/List;", "getDeviceModel", "()Ljava/lang/String;", "fixableCount", "", "getFixableCount", "()I", "overallStatus", "Lcom/miclaw/notification/diagnostic/CheckStatus;", "getOverallStatus", "()Lcom/miclaw/notification/diagnostic/CheckStatus;", "summary", "getSummary", "getSystemVersion", "getTimestamp", "()J", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "groupedByCategory", "", "Lcom/miclaw/notification/diagnostic/CheckCategory;", "hashCode", "toDisplayText", "toJsonSummary", "toString", "app_debug"})
public final class DiagnosticReport {
    private final long timestamp = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String deviceModel = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String systemVersion = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.miclaw.notification.diagnostic.CheckItem> checks = null;
    
    public DiagnosticReport(long timestamp, @org.jetbrains.annotations.NotNull()
    java.lang.String deviceModel, @org.jetbrains.annotations.NotNull()
    java.lang.String systemVersion, @org.jetbrains.annotations.NotNull()
    java.util.List<com.miclaw.notification.diagnostic.CheckItem> checks) {
        super();
    }
    
    public final long getTimestamp() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDeviceModel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSystemVersion() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.miclaw.notification.diagnostic.CheckItem> getChecks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.miclaw.notification.diagnostic.CheckStatus getOverallStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSummary() {
        return null;
    }
    
    public final int getFixableCount() {
        return 0;
    }
    
    /**
     * 按类别分组
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<com.miclaw.notification.diagnostic.CheckCategory, java.util.List<com.miclaw.notification.diagnostic.CheckItem>> groupedByCategory() {
        return null;
    }
    
    /**
     * 生成可读文本报告
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String toDisplayText() {
        return null;
    }
    
    /**
     * 生成 JSON 摘要
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Object> toJsonSummary() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.miclaw.notification.diagnostic.CheckItem> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.miclaw.notification.diagnostic.DiagnosticReport copy(long timestamp, @org.jetbrains.annotations.NotNull()
    java.lang.String deviceModel, @org.jetbrains.annotations.NotNull()
    java.lang.String systemVersion, @org.jetbrains.annotations.NotNull()
    java.util.List<com.miclaw.notification.diagnostic.CheckItem> checks) {
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