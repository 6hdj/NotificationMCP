package com.miclaw.notification.config

import android.content.Context
import android.content.SharedPreferences

/**
 * 服务器配置管理
 * 存储 WebSocket 连接地址和 Token
 */
object ServerConfig {
    private const val PREFS_NAME = "server_config"
    private const val KEY_SERVER_URL = "server_url"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_AUTO_CONNECT = "auto_connect"

    // 默认值
    private const val DEFAULT_SERVER_URL = "ws://192.168.1.100:8765/mcp"
    private const val DEFAULT_AUTO_CONNECT = true

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getServerUrl(context: Context): String {
        return getPrefs(context).getString(KEY_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
    }

    fun setServerUrl(context: Context, url: String) {
        getPrefs(context).edit().putString(KEY_SERVER_URL, url).apply()
    }

    fun getAuthToken(context: Context): String {
        return getPrefs(context).getString(KEY_AUTH_TOKEN, "") ?: ""
    }

    fun setAuthToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun isAutoConnect(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_CONNECT, DEFAULT_AUTO_CONNECT)
    }

    fun setAutoConnect(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_CONNECT, enabled).apply()
    }
}
