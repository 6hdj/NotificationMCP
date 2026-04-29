package com.miclaw.notification.service

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.miclaw.notification.model.NotificationData
import kotlinx.coroutines.*
import okhttp3.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

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
class McpWebSocketManager private constructor() {

    companion object {
        private const val TAG = "McpWebSocket"
        private const val RECONNECT_BASE_DELAY_MS = 1000L
        private const val RECONNECT_MAX_DELAY_MS = 30_000L
        private const val HEARTBEAT_INTERVAL_MS = 30_000L
        private const val CONNECTION_TIMEOUT_MS = 10L

        @Volatile
        private var instance: McpWebSocketManager? = null

        fun getInstance(): McpWebSocketManager {
            return instance ?: synchronized(this) {
                instance ?: McpWebSocketManager().also { instance = it }
            }
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECTION_TIMEOUT_MS, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS) // 无读取超时（长连接）
        .writeTimeout(10, TimeUnit.SECONDS)
        .pingInterval(HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private val isConnected = AtomicBoolean(false)
    private val isConnecting = AtomicBoolean(false)
    private val reconnectAttempt = AtomicLong(0)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var reconnectJob: Job? = null
    private var heartbeatJob: Job? = null

    // 连接配置
    private var serverUrl: String = ""
    private var authToken: String = ""

    // 回调
    var onConnected: (() -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null
    var onMessageReceived: ((JsonObject) -> Unit)? = null

    // ═══════════════════════════════════════════════════
    //  连接管理
    // ═══════════════════════════════════════════════════

    /**
     * 配置并连接到 Miclaw MCP Server
     */
    fun connect(url: String = serverUrl, token: String = authToken) {
        if (url.isNotEmpty()) serverUrl = url
        if (token.isNotEmpty()) authToken = token

        if (serverUrl.isEmpty()) {
            Log.w(TAG, "服务器地址为空，跳过连接")
            return
        }

        if (isConnecting.get()) {
            Log.d(TAG, "正在连接中，跳过")
            return
        }

        isConnecting.set(true)
        doConnect()
    }

    private fun doConnect() {
        try {
            val requestBuilder = Request.Builder()
                .url(serverUrl)

            if (authToken.isNotEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $authToken")
            }
            requestBuilder.addHeader("X-Client-Type", "android-notification-mcp")
            requestBuilder.addHeader("X-Client-Version", "1.0.0")

            webSocket = client.newWebSocket(requestBuilder.build(), createWebSocketListener())
            Log.i(TAG, "正在连接: $serverUrl")
        } catch (e: Exception) {
            Log.e(TAG, "连接失败: ${e.message}", e)
            isConnecting.set(false)
            scheduleReconnect()
        }
    }

    fun disconnect() {
        reconnectJob?.cancel()
        heartbeatJob?.cancel()
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
        isConnected.set(false)
        isConnecting.set(false)
        reconnectAttempt.set(0)
    }

    fun reconnect() {
        disconnect()
        doConnect()
    }

    fun isConnected(): Boolean = isConnected.get()

    // ═══════════════════════════════════════════════════
    //  数据发送
    // ═══════════════════════════════════════════════════

    /**
     * 转发通知到电脑端
     */
    fun sendNotification(data: NotificationData): Boolean {
        if (!isConnected.get()) return false

        return try {
            val payload = JsonObject().apply {
                addProperty("type", "notification")
                addProperty("timestamp", System.currentTimeMillis())
                add("data", Gson().toJsonTree(data))
            }
            val success = webSocket?.send(payload.toString()) ?: false
            if (!success) {
                Log.w(TAG, "发送通知失败: ${data.title}")
            }
            success
        } catch (e: Exception) {
            Log.e(TAG, "发送通知异常: ${e.message}", e)
            false
        }
    }

    /**
     * 发送心跳
     */
    fun sendHeartbeat(): Boolean {
        if (!isConnected.get()) return false

        return try {
            val payload = JsonObject().apply {
                addProperty("type", "heartbeat")
                addProperty("timestamp", System.currentTimeMillis())
                add("stats", Gson().toJsonTree(NotificationListenerServiceImpl.stats.toMap()))
            }
            webSocket?.send(payload.toString()) ?: false
        } catch (e: Exception) {
            Log.e(TAG, "发送心跳异常: ${e.message}", e)
            false
        }
    }

    /**
     * 发送自定义消息
     */
    fun sendMessage(message: JsonObject): Boolean {
        if (!isConnected.get()) return false
        return webSocket?.send(message.toString()) ?: false
    }

    // ═══════════════════════════════════════════════════
    //  WebSocket 回调
    // ═══════════════════════════════════════════════════

    private fun createWebSocketListener() = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.i(TAG, "✅ WebSocket 已连接")
            isConnected.set(true)
            isConnecting.set(false)
            reconnectAttempt.set(0)

            // 发送握手
            val handshake = JsonObject().apply {
                addProperty("type", "handshake")
                addProperty("clientType", "android-notification-mcp")
                addProperty("version", "1.0.0")
                addProperty("deviceId", getDeviceId())
            }
            webSocket.send(handshake.toString())

            onConnected?.invoke()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val json = JsonParser.parseString(text).asJsonObject
                val type = json.get("type")?.asString ?: return

                when (type) {
                    "command" -> handleCommand(json)
                    "query" -> handleQuery(json)
                    "config_update" -> handleConfigUpdate(json)
                    "pong" -> Log.d(TAG, "收到 pong")
                    else -> onMessageReceived?.invoke(json)
                }
            } catch (e: Exception) {
                Log.e(TAG, "处理消息异常: ${e.message}", e)
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "WebSocket 正在关闭: $code - $reason")
            webSocket.close(code, reason)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "WebSocket 已关闭: $code - $reason")
            handleDisconnect()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket 连接失败: ${t.message}")
            handleDisconnect()
        }
    }

    private fun handleDisconnect() {
        isConnected.set(false)
        isConnecting.set(false)
        onDisconnected?.invoke()
        scheduleReconnect()
    }

    // ═══════════════════════════════════════════════════
    //  指令处理
    // ═══════════════════════════════════════════════════

    private fun handleCommand(json: JsonObject) {
        val command = json.get("command")?.asString ?: return
        Log.i(TAG, "收到指令: $command")

        when (command) {
            "ping" -> {
                val pong = JsonObject().apply {
                    addProperty("type", "pong")
                    addProperty("timestamp", System.currentTimeMillis())
                }
                webSocket?.send(pong.toString())
            }
            "restart" -> {
                // 重启服务
                scope.launch {
                    disconnect()
                    delay(2000)
                    connect()
                }
            }
            "replay" -> {
                // 补传积压通知
                scope.launch {
                    NotificationListenerServiceImpl().replayUnforwardedNotifications()
                }
            }
        }
    }

    private fun handleQuery(json: JsonObject) {
        val queryId = json.get("queryId")?.asString ?: return
        val queryType = json.get("queryType")?.asString ?: return

        scope.launch {
            val result = when (queryType) {
                "stats" -> Gson().toJsonTree(NotificationListenerServiceImpl.stats.toMap())
                "recent" -> {
                    val db = com.miclaw.notification.NotificationMcpApp.instance.database
                    val limit = json.get("limit")?.asInt ?: 50
                    Gson().toJsonTree(db.notificationDao().getRecent(limit))
                }
                "search" -> {
                    val keyword = json.get("keyword")?.asString ?: ""
                    val db = com.miclaw.notification.NotificationMcpApp.instance.database
                    Gson().toJsonTree(db.notificationDao().searchByKeyword(keyword))
                }
                else -> JsonObject()
            }

            val response = JsonObject().apply {
                addProperty("type", "query_response")
                addProperty("queryId", queryId)
                add("data", result)
            }
            webSocket?.send(response.toString())
        }
    }

    private fun handleConfigUpdate(json: JsonObject) {
        Log.i(TAG, "收到配置更新: ${json.get("config")}")
        // TODO: 更新本地过滤规则、脱敏规则等
        onMessageReceived?.invoke(json)
    }

    // ═══════════════════════════════════════════════════
    //  重连机制（指数退避）
    // ═══════════════════════════════════════════════════

    private fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            val attempt = reconnectAttempt.incrementAndGet()
            val delayMs = minOf(
                RECONNECT_BASE_DELAY_MS * attempt,
                RECONNECT_MAX_DELAY_MS
            )
            Log.i(TAG, "将在 ${delayMs}ms 后重连 (第 $attempt 次)")
            delay(delayMs)
            doConnect()
        }
    }

    // ═══════════════════════════════════════════════════
    //  辅助
    // ═══════════════════════════════════════════════════

    private fun getDeviceId(): String {
        return "${android.os.Build.MANUFACTURER}_${android.os.Build.MODEL}_${android.os.Build.DEVICE}"
    }
}
