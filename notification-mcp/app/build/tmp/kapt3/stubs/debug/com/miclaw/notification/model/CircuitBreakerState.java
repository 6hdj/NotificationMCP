package com.miclaw.notification.model;

/**
 * 熔断器状态
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0086\b\u0018\u0000 $2\u00020\u0001:\u0001$B1\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ:\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0007H\u00c6\u0001\u00a2\u0006\u0002\u0010\u001eJ\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020\u0005H\u00d6\u0001J\t\u0010#\u001a\u00020\u0003H\u00d6\u0001R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001e\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u0012\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001e\u0010\b\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u0012\u001a\u0004\b\u0013\u0010\u000f\"\u0004\b\u0014\u0010\u0011R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018\u00a8\u0006%"}, d2 = {"Lcom/miclaw/notification/model/CircuitBreakerState;", "", "state", "", "failureCount", "", "lastFailureTime", "", "lastSuccessTime", "(Ljava/lang/String;ILjava/lang/Long;Ljava/lang/Long;)V", "getFailureCount", "()I", "setFailureCount", "(I)V", "getLastFailureTime", "()Ljava/lang/Long;", "setLastFailureTime", "(Ljava/lang/Long;)V", "Ljava/lang/Long;", "getLastSuccessTime", "setLastSuccessTime", "getState", "()Ljava/lang/String;", "setState", "(Ljava/lang/String;)V", "component1", "component2", "component3", "component4", "copy", "(Ljava/lang/String;ILjava/lang/Long;Ljava/lang/Long;)Lcom/miclaw/notification/model/CircuitBreakerState;", "equals", "", "other", "hashCode", "toString", "Companion", "app_debug"})
public final class CircuitBreakerState {
    @org.jetbrains.annotations.NotNull()
    private java.lang.String state;
    private int failureCount;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long lastFailureTime;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long lastSuccessTime;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String STATE_CLOSED = "closed";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String STATE_OPEN = "open";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String STATE_HALF_OPEN = "half-open";
    public static final int FAILURE_THRESHOLD = 10;
    @org.jetbrains.annotations.NotNull()
    public static final com.miclaw.notification.model.CircuitBreakerState.Companion Companion = null;
    
    public CircuitBreakerState(@org.jetbrains.annotations.NotNull()
    java.lang.String state, int failureCount, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastFailureTime, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastSuccessTime) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getState() {
        return null;
    }
    
    public final void setState(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    public final int getFailureCount() {
        return 0;
    }
    
    public final void setFailureCount(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLastFailureTime() {
        return null;
    }
    
    public final void setLastFailureTime(@org.jetbrains.annotations.Nullable()
    java.lang.Long p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLastSuccessTime() {
        return null;
    }
    
    public final void setLastSuccessTime(@org.jetbrains.annotations.Nullable()
    java.lang.Long p0) {
    }
    
    public CircuitBreakerState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.miclaw.notification.model.CircuitBreakerState copy(@org.jetbrains.annotations.NotNull()
    java.lang.String state, int failureCount, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastFailureTime, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastSuccessTime) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/miclaw/notification/model/CircuitBreakerState$Companion;", "", "()V", "FAILURE_THRESHOLD", "", "STATE_CLOSED", "", "STATE_HALF_OPEN", "STATE_OPEN", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}