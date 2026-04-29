package com.miclaw.notification.config;

/**
 * 服务器配置管理
 * 存储 WebSocket 连接地址和 Token
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u000b\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\rJ\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\rH\u0002J\u000e\u0010\u0010\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\rJ\u000e\u0010\u0011\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\rJ\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u0006J\u0016\u0010\u0015\u001a\u00020\u00132\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\u0004J\u0016\u0010\u0017\u001a\u00020\u00132\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0018\u001a\u00020\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/miclaw/notification/config/ServerConfig;", "", "()V", "DEFAULT_AUTO_CONNECT", "", "DEFAULT_SERVER_URL", "", "KEY_AUTH_TOKEN", "KEY_AUTO_CONNECT", "KEY_SERVER_URL", "PREFS_NAME", "getAuthToken", "context", "Landroid/content/Context;", "getPrefs", "Landroid/content/SharedPreferences;", "getServerUrl", "isAutoConnect", "setAuthToken", "", "token", "setAutoConnect", "enabled", "setServerUrl", "url", "app_debug"})
public final class ServerConfig {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "server_config";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_SERVER_URL = "server_url";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_AUTH_TOKEN = "auth_token";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_AUTO_CONNECT = "auto_connect";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DEFAULT_SERVER_URL = "ws://192.168.1.100:8765/mcp";
    private static final boolean DEFAULT_AUTO_CONNECT = true;
    @org.jetbrains.annotations.NotNull()
    public static final com.miclaw.notification.config.ServerConfig INSTANCE = null;
    
    private ServerConfig() {
        super();
    }
    
    private final android.content.SharedPreferences getPrefs(android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getServerUrl(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void setServerUrl(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String url) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAuthToken(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void setAuthToken(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String token) {
    }
    
    public final boolean isAutoConnect(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void setAutoConnect(@org.jetbrains.annotations.NotNull()
    android.content.Context context, boolean enabled) {
    }
}