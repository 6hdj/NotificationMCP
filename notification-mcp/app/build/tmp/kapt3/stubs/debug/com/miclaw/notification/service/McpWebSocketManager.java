package com.miclaw.notification.service;

/**
 * WebSocket 通信管理器 — 与电脑端 Miclaw 的双向通道
 *
 * 职责：
 * 1. 维持与 Miclaw MCP Server 的 WebSocket 长连接
 * 2. 实时转发通知数据（JSON 格式）
 * 3. 接收电脑端的控制指令（过滤规则、查询请求等）
 * 4. 断连自动重连（指数退避）
 * 5. 心跳保活
 * 6. 断连期间缓存通知，重连后自动补传
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 ;2\u00020\u0001:\u0001;B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010%\u001a\u00020\u000e2\b\b\u0002\u0010&\u001a\u00020\u00042\b\b\u0002\u0010\'\u001a\u00020\u0004J\b\u0010(\u001a\u00020)H\u0002J\u0006\u0010*\u001a\u00020\u000eJ\b\u0010+\u001a\u00020\u000eH\u0002J\b\u0010,\u001a\u00020\u0004H\u0002J\u0010\u0010-\u001a\u00020\u000e2\u0006\u0010.\u001a\u00020\u0018H\u0002J\u0010\u0010/\u001a\u00020\u000e2\u0006\u0010.\u001a\u00020\u0018H\u0002J\b\u00100\u001a\u00020\u000eH\u0002J\u0010\u00101\u001a\u00020\u000e2\u0006\u0010.\u001a\u00020\u0018H\u0002J\u0006\u0010\t\u001a\u000202J\u0006\u00103\u001a\u00020\u000eJ\b\u00104\u001a\u00020\u000eH\u0002J\u0006\u00105\u001a\u000202J\u000e\u00106\u001a\u0002022\u0006\u00107\u001a\u00020\u0018J\u000e\u00108\u001a\u0002022\u0006\u00109\u001a\u00020:R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\"\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\rX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\"\u0010\u0013\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\rX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0010\"\u0004\b\u0015\u0010\u0012R(\u0010\u0016\u001a\u0010\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u000e\u0018\u00010\u0017X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001f\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020!X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010$X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006<"}, d2 = {"Lcom/miclaw/notification/service/McpWebSocketManager;", "", "()V", "authToken", "", "client", "Lokhttp3/OkHttpClient;", "heartbeatJob", "Lkotlinx/coroutines/Job;", "isConnected", "Ljava/util/concurrent/atomic/AtomicBoolean;", "isConnecting", "onConnected", "Lkotlin/Function0;", "", "getOnConnected", "()Lkotlin/jvm/functions/Function0;", "setOnConnected", "(Lkotlin/jvm/functions/Function0;)V", "onDisconnected", "getOnDisconnected", "setOnDisconnected", "onMessageReceived", "Lkotlin/Function1;", "Lcom/google/gson/JsonObject;", "getOnMessageReceived", "()Lkotlin/jvm/functions/Function1;", "setOnMessageReceived", "(Lkotlin/jvm/functions/Function1;)V", "reconnectAttempt", "Ljava/util/concurrent/atomic/AtomicLong;", "reconnectJob", "scope", "Lkotlinx/coroutines/CoroutineScope;", "serverUrl", "webSocket", "Lokhttp3/WebSocket;", "connect", "url", "token", "createWebSocketListener", "Lokhttp3/WebSocketListener;", "disconnect", "doConnect", "getDeviceId", "handleCommand", "json", "handleConfigUpdate", "handleDisconnect", "handleQuery", "", "reconnect", "scheduleReconnect", "sendHeartbeat", "sendMessage", "message", "sendNotification", "data", "Lcom/miclaw/notification/model/NotificationData;", "Companion", "app_debug"})
public final class McpWebSocketManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "McpWebSocket";
    private static final long RECONNECT_BASE_DELAY_MS = 1000L;
    private static final long RECONNECT_MAX_DELAY_MS = 30000L;
    private static final long HEARTBEAT_INTERVAL_MS = 30000L;
    private static final long CONNECTION_TIMEOUT_MS = 10L;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.miclaw.notification.service.McpWebSocketManager instance;
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient client = null;
    @org.jetbrains.annotations.Nullable()
    private okhttp3.WebSocket webSocket;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.atomic.AtomicBoolean isConnected = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.atomic.AtomicBoolean isConnecting = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.atomic.AtomicLong reconnectAttempt = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job reconnectJob;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job heartbeatJob;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String serverUrl = "";
    @org.jetbrains.annotations.NotNull()
    private java.lang.String authToken = "";
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function0<kotlin.Unit> onConnected;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function0<kotlin.Unit> onDisconnected;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function1<? super com.google.gson.JsonObject, kotlin.Unit> onMessageReceived;
    @org.jetbrains.annotations.NotNull()
    public static final com.miclaw.notification.service.McpWebSocketManager.Companion Companion = null;
    
    private McpWebSocketManager() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnConnected() {
        return null;
    }
    
    public final void setOnConnected(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnDisconnected() {
        return null;
    }
    
    public final void setOnDisconnected(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function1<com.google.gson.JsonObject, kotlin.Unit> getOnMessageReceived() {
        return null;
    }
    
    public final void setOnMessageReceived(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super com.google.gson.JsonObject, kotlin.Unit> p0) {
    }
    
    /**
     * 配置并连接到 Miclaw MCP Server
     */
    public final void connect(@org.jetbrains.annotations.NotNull()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    java.lang.String token) {
    }
    
    private final void doConnect() {
    }
    
    public final void disconnect() {
    }
    
    public final void reconnect() {
    }
    
    public final boolean isConnected() {
        return false;
    }
    
    /**
     * 转发通知到电脑端
     */
    public final boolean sendNotification(@org.jetbrains.annotations.NotNull()
    com.miclaw.notification.model.NotificationData data) {
        return false;
    }
    
    /**
     * 发送心跳
     */
    public final boolean sendHeartbeat() {
        return false;
    }
    
    /**
     * 发送自定义消息
     */
    public final boolean sendMessage(@org.jetbrains.annotations.NotNull()
    com.google.gson.JsonObject message) {
        return false;
    }
    
    private final okhttp3.WebSocketListener createWebSocketListener() {
        return null;
    }
    
    private final void handleDisconnect() {
    }
    
    private final void handleCommand(com.google.gson.JsonObject json) {
    }
    
    private final void handleQuery(com.google.gson.JsonObject json) {
    }
    
    private final void handleConfigUpdate(com.google.gson.JsonObject json) {
    }
    
    private final void scheduleReconnect() {
    }
    
    private final java.lang.String getDeviceId() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\f\u001a\u00020\u000bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/miclaw/notification/service/McpWebSocketManager$Companion;", "", "()V", "CONNECTION_TIMEOUT_MS", "", "HEARTBEAT_INTERVAL_MS", "RECONNECT_BASE_DELAY_MS", "RECONNECT_MAX_DELAY_MS", "TAG", "", "instance", "Lcom/miclaw/notification/service/McpWebSocketManager;", "getInstance", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.miclaw.notification.service.McpWebSocketManager getInstance() {
            return null;
        }
    }
}