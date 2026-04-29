package com.miclaw.notification.server

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.miclaw.notification.NotificationMcpApp
import com.miclaw.notification.service.NotificationListenerServiceImpl
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID
import java.util.concurrent.Executors

/**
 * 本地 MCP 服务器 — 符合 MCP Streamable HTTP 协议规范
 *
 * 协议要点：
 * - POST /mcp：JSON-RPC 2.0 请求
 * - 服务器必须返回 Mcp-Session-Id 头
 * - initialize 响应后客户端发送 notifications/initialized
 */
class LocalMcpServer(private val port: Int = 8765) {

    companion object {
        private const val TAG = "LocalMcpServer"
        private const val PROTOCOL_VERSION = "2024-11-05"
        private var instance: LocalMcpServer? = null

        fun getInstance(): LocalMcpServer? = instance

        fun start(port: Int = 8765): LocalMcpServer {
            return instance ?: synchronized(this) {
                instance ?: LocalMcpServer(port).also {
                    it.startServer()
                    instance = it
                }
            }
        }

        fun stop() {
            instance?.stopServer()
            instance = null
        }
    }

    private var serverSocket: ServerSocket? = null
    private val gson = Gson()
    private val executor = Executors.newCachedThreadPool()
    @Volatile private var running = false
    private val sessions = mutableMapOf<String, Boolean>()

    // ─── 工具定义（共享） ───
    private val toolsList = listOf(
        mapOf(
            "name" to "get_notifications",
            "description" to "获取手机通知列表，支持按包名筛选和限制数量",
            "inputSchema" to mapOf(
                "type" to "object",
                "properties" to mapOf(
                    "limit" to mapOf("type" to "integer", "description" to "最大返回数量，默认50"),
                    "package_name" to mapOf("type" to "string", "description" to "按应用包名筛选，如 com.tencent.mm")
                )
            )
        ),
                mapOf(
            "name" to "search_notifications",
            "description" to "按关键词搜索手机通知的标题和内容，支持时间范围筛选",
            "inputSchema" to mapOf(
                "type" to "object",
                "properties" to mapOf(
                    "keyword" to mapOf("type" to "string", "description" to "搜索关键词"),
                    "hours" to mapOf("type" to "integer", "description" to "只搜索最近N小时内的通知，默认24小时"),
                    "limit" to mapOf("type" to "integer", "description" to "最大返回数量，默认20")
                ),
                "required" to listOf("keyword")
            )
        ),
        mapOf(
            "name" to "get_stats",
            "description" to "获取通知接收统计信息（总数、已转发、已过滤、错误数）",
            "inputSchema" to mapOf(
                "type" to "object",
                "properties" to emptyMap<String, Any>()
            )
        )
    )

    // ─── 服务器生命周期 ───

    private fun startServer() {
        Thread({
            try {
                serverSocket = ServerSocket(port)
                serverSocket?.reuseAddress = true
                running = true
                Log.i(TAG, "✅ MCP 服务器已启动，端口: $port")
                while (running) {
                    try {
                        val client = serverSocket?.accept() ?: break
                        executor.submit { handleClient(client) }
                    } catch (e: Exception) {
                        if (running) Log.e(TAG, "接受连接异常: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "启动失败: ${e.message}", e)
            }
        }, "McpServer").start()
    }

    private fun stopServer() {
        running = false
        try { serverSocket?.close() } catch (_: Exception) {}
        serverSocket = null
        executor.shutdownNow()
        sessions.clear()
        Log.i(TAG, "MCP 服务器已停止")
    }

    // ─── HTTP 请求处理 ───

    private fun handleClient(socket: Socket) {
        try {
            socket.soTimeout = 10000
            val reader = BufferedReader(InputStreamReader(socket.getInputStream(), "UTF-8"))

            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 2) return
            val method = parts[0]
            val path = parts[1]

            // 读取 headers
            val headers = mutableMapOf<String, String>()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrEmpty()) break
                val colonIdx = line!!.indexOf(':')
                if (colonIdx > 0) {
                    headers[line!!.substring(0, colonIdx).trim().lowercase()] = line!!.substring(colonIdx + 1).trim()
                }
            }

            // 读取 body
            val contentLength = headers["content-length"]?.toIntOrNull() ?: 0
            val body = if (contentLength > 0) {
                val chars = CharArray(contentLength)
                var totalRead = 0
                while (totalRead < contentLength) {
                    val n = reader.read(chars, totalRead, contentLength - totalRead)
                    if (n < 0) break
                    totalRead += n
                }
                String(chars, 0, totalRead)
            } else ""

            Log.d(TAG, "$method $path (body=${body.length})")

            val (responseBody, extraHeaders) = when {
                method == "OPTIONS" -> "" to emptyMap<String, String>()
                path == "/" || path == "/health" -> handleHealth()
                path == "/mcp" && method == "GET" -> handleToolsList()
                path == "/mcp" && method == "POST" -> handleMcpPost(body, headers)
                path == "/mcp" && method == "DELETE" -> "{}" to emptyMap<String, String>()
                                else -> gson.toJson(errorJson("Not found: $path")) to emptyMap<String, String>()
            }

            sendResponse(socket, responseBody, extraHeaders)
        } catch (e: Exception) {
            Log.e(TAG, "处理客户端异常: ${e.message}", e)
        } finally {
            try { socket.close() } catch (_: Exception) {}
        }
    }

    private fun sendResponse(socket: Socket, body: String, extraHeaders: Map<String, String> = emptyMap()) {
        val os: OutputStream = socket.getOutputStream()
        val responseBytes = body.toByteArray(Charsets.UTF_8)
        val header = buildString {
            append("HTTP/1.1 200 OK\r\n")
            append("Content-Type: application/json; charset=utf-8\r\n")
            append("Content-Length: ${responseBytes.size}\r\n")
            append("Access-Control-Allow-Origin: *\r\n")
            append("Access-Control-Allow-Methods: GET, POST, DELETE, OPTIONS\r\n")
            append("Access-Control-Allow-Headers: Content-Type, Mcp-Session-Id\r\n")
            extraHeaders.forEach { (k, v) -> append("$k: $v\r\n") }
            append("Connection: close\r\n")
            append("\r\n")
        }
        os.write(header.toByteArray())
        os.write(responseBytes)
        os.flush()
        os.close()
    }

    // ─── 路由处理 ───

    private fun handleHealth(): Pair<String, Map<String, String>> {
        val json = JsonObject().apply {
            addProperty("status", "running")
            addProperty("service", "notification-mcp")
            addProperty("version", "1.0.0")
            addProperty("protocol", PROTOCOL_VERSION)
            addProperty("port", port)
            addProperty("timestamp", System.currentTimeMillis())
        }
        return gson.toJson(json) to emptyMap()
    }

    private fun handleToolsList(): Pair<String, Map<String, String>> {
        val json = JsonObject().apply {
            add("tools", gson.toJsonTree(toolsList))
        }
        return gson.toJson(json) to emptyMap()
    }

    private fun handleMcpPost(body: String, requestHeaders: Map<String, String>): Pair<String, Map<String, String>> {
        return try {
                        val request = JsonParser.parseString(body).asJsonObject
            val method = request.get("method")?.asString
                ?: return gson.toJson(errorJson("Missing method")) to emptyMap()
            val params = request.getAsJsonObject("params") ?: JsonObject()
            val id = request.get("id")

            Log.d(TAG, "MCP: $method (id=$id)")

            // notifications/initialized 是通知，不返回响应
            if (method == "notifications/initialized") {
                return "" to emptyMap()
            }

            val result = when (method) {
                "initialize" -> handleInitialize(params)
                "ping" -> JsonObject()
                "tools/list" -> JsonObject().apply { add("tools", gson.toJsonTree(toolsList)) }
                "tools/call" -> handleToolCall(params)
                else -> errorJson("Unknown method: $method")
            }

            // 会话管理
            val sessionId = requestHeaders["mcp-session-id"] ?: UUID.randomUUID().toString()
            if (!sessions.containsKey(sessionId)) {
                sessions[sessionId] = true
                Log.i(TAG, "新会话: $sessionId")
            }

            val jsonRpcResponse = JsonObject().apply {
                addProperty("jsonrpc", "2.0")
                add("result", result)
                if (id != null) add("id", id)
            }

            gson.toJson(jsonRpcResponse) to mutableMapOf("Mcp-Session-Id" to sessionId)
        } catch (e: Exception) {
            Log.e(TAG, "MCP 请求异常: ${e.message}", e)
                        gson.toJson(errorJson("Parse error: ${e.message}")) to emptyMap()
        }
    }

    private fun handleInitialize(params: JsonObject): JsonObject {
        Log.i(TAG, "=== initialize ===")
        return JsonObject().apply {
            addProperty("protocolVersion", PROTOCOL_VERSION)
            add("capabilities", gson.toJsonTree(mapOf(
                "tools" to mapOf("listChanged" to false)
            )))
            add("serverInfo", gson.toJsonTree(mapOf(
                "name" to "notification-mcp",
                "version" to "1.0.0"
            )))
        }
    }

    private fun handleToolCall(params: JsonObject): JsonObject {
        val toolName = params.get("name")?.asString ?: return errorJson("Missing tool name")
        val arguments = params.getAsJsonObject("arguments") ?: JsonObject()
        Log.d(TAG, "工具: $toolName")

        return when (toolName) {
            "get_notifications" -> callGetNotifications(arguments)
            "search_notifications" -> callSearchNotifications(arguments)
            "get_stats" -> callGetStats()
            else -> errorJson("Unknown tool: $toolName")
        }
    }

    // ─── 工具实现 ───

    private fun callGetNotifications(args: JsonObject): JsonObject {
        val limit = args.get("limit")?.asInt ?: 50
        val packageName = args.get("package_name")?.asString
        return try {
            val db = NotificationMcpApp.instance.database
            val list = runBlocking {
                if (packageName != null) db.notificationDao().getByPackage(packageName, limit)
                else db.notificationDao().getRecent(limit)
            }
            Log.d(TAG, "查询到 ${list.size} 条通知")
            contentResult(gson.toJson(list))
        } catch (e: Exception) {
            Log.e(TAG, "查询失败: ${e.message}", e)
            errorJson("查询失败: ${e.message}")
        }
    }

        private fun callSearchNotifications(args: JsonObject): JsonObject {
        val keyword = args.get("keyword")?.asString ?: return errorJson("Missing keyword")
        val hours = args.get("hours")?.asInt ?: 24
        val limit = args.get("limit")?.asInt ?: 20
        return try {
            val db = NotificationMcpApp.instance.database
            val startTime = System.currentTimeMillis() - hours * 3600L * 1000L
            val list = runBlocking { db.notificationDao().searchByKeywordInTimeRange(keyword, startTime, limit) }
            Log.d(TAG, "搜索 '$keyword' (最近${hours}h) → ${list.size} 条")
            contentResult(gson.toJson(list))
        } catch (e: Exception) {
            Log.e(TAG, "搜索失败: ${e.message}", e)
            errorJson("搜索失败: ${e.message}")
        }
    }

    private fun callGetStats(): JsonObject {
        val stats = NotificationListenerServiceImpl.stats
        return contentResult(gson.toJson(stats.toMap()))
    }

    // ─── 工具方法 ───

    private fun contentResult(text: String): JsonObject {
        return JsonObject().apply {
            add("content", gson.toJsonTree(listOf(mapOf("type" to "text", "text" to text))))
        }
    }

    private fun errorJson(message: String): JsonObject {
        return JsonObject().apply {
            add("content", gson.toJsonTree(listOf(mapOf("type" to "text", "text" to "Error: $message"))))
            addProperty("isError", true)
        }
    }
}
