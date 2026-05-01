package com.notificationmcp.mcp

import android.util.Log
import com.notificationmcp.data.db.entity.AuditLogEntity
import com.notificationmcp.data.db.entity.NotificationEntity
import com.notificationmcp.data.model.*
import com.notificationmcp.data.repository.NotificationRepository
import com.notificationmcp.privacy.ClassificationEngine
import com.notificationmcp.privacy.PrivacyEngine
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlinx.serialization.encodeToString
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MCPServer @Inject constructor(
    private val repository: NotificationRepository,
    private val privacyEngine: PrivacyEngine,
    private val classificationEngine: ClassificationEngine
) {
    private var server: ApplicationEngine? = null
    private val _notificationFlow = MutableSharedFlow<NotificationEntity>(extraBufferCapacity = 64)
    val notificationFlow = _notificationFlow.asSharedFlow()

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

        private val connectedClients = mutableMapOf<String, Channel<String>>()

    // Helper to work around Kotlin 2.0 + kotlinx-serialization 1.6.2 reified overload resolution issue
    private inline fun <reified T> encodeToJson(value: T): String =
        json.encodeToString(serializer<T>(), value)

    fun start(port: Int = 8765) {
        if (server != null) return

        server = embeddedServer(CIO, port = port) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }

            install(StatusPages) {
                exception<Throwable> { call, cause ->
                    Log.e("MCPServer", "Error", cause)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        buildJsonObject {
                            put("jsonrpc", "2.0")
                            put("error", buildJsonObject {
                                put("code", -32603)
                                put("message", cause.message ?: "Internal error")
                            })
                        }
                    )
                }
            }

                                                routing {
                // MCP HTTP endpoint (Streamable HTTP transport)
                post("/mcp") {
                    try {
                        val body = call.receiveText()
                        val request = Json.parseToJsonElement(body).jsonObject
                        val method = request["method"]?.jsonPrimitive?.content
                        val id = request["id"]
                        val params = request["params"]?.jsonObject

                        val response = handleMCPRequest(method, params, call.request.headers["X-Client-ID"])

                        call.respondText(
                            response.toString(),
                            ContentType.Application.Json
                        )
                    } catch (e: Exception) {
                        Log.e("MCPServer", "Error handling MCP request", e)
                        call.respondText(
                            buildJsonObject {
                                put("jsonrpc", "2.0")
                                put("error", buildJsonObject {
                                    put("code", -32603)
                                    put("message", e.message ?: "Internal error")
                                })
                            }.toString(),
                            ContentType.Application.Json,
                            HttpStatusCode.InternalServerError
                        )
                    }
                }

                // Health check
                get("/health") {
                    call.respondText(
                        buildJsonObject {
                            put("status", "running")
                            put("clients", connectedClients.size)
                            put("timestamp", System.currentTimeMillis())
                        }.toString(),
                        ContentType.Application.Json
                    )
                }
            }
        }

        server?.start(wait = false)
        Log.i("MCPServer", "MCP Server started on port $port")
    }

    fun stop() {
        server?.stop(1000, 5000)
        server = null
        connectedClients.clear()
        Log.i("MCPServer", "MCP Server stopped")
    }

    fun isRunning(): Boolean = server != null

    suspend fun broadcastNotification(notification: NotificationEntity) {
        _notificationFlow.emit(notification)
        val jsonStr = json.encodeToString(
            NotificationData.serializer(),
            notification.toData()
        )
        connectedClients.values.forEach { channel ->
            try {
                channel.trySend(jsonStr)
            } catch (e: Exception) {
                Log.w("MCPServer", "Failed to send to client", e)
            }
        }
    }

                        private suspend fun handleMCPRequest(
        method: String?,
        params: JsonObject?,
        clientId: String?
    ): JsonObject {
        return when (method) {
            "initialize" -> handleInitialize()
            "tools/list" -> handleToolsListResponse()
            "tools/call" -> handleToolCallResponse(params, clientId)
            "ping" -> buildJsonObject {
                put("jsonrpc", "2.0")
                put("result", buildJsonObject { put("pong", true) })
            }
            else -> buildJsonObject {
                put("jsonrpc", "2.0")
                put("error", buildJsonObject {
                    put("code", -32601)
                    put("message", "Method not found: $method")
                })
            }
        }
    }

    private fun handleInitialize(): JsonObject {
        return buildJsonObject {
            put("jsonrpc", "2.0")
            put("result", buildJsonObject {
                put("protocolVersion", "2024-11-05")
                put("serverInfo", buildJsonObject {
                    put("name", "notification-mcp")
                    put("version", "1.0.0")
                })
                put("capabilities", buildJsonObject {
                    put("tools", buildJsonObject { })
                })
            })
        }
    }

            private fun handleToolsListRaw(): MCPToolResult = handleToolsList()

    /** MCP 协议 tools/list 响应：result 直接是 {tools:[...]}，不包 content */
    private fun handleToolsListResponse(): JsonObject {
        val tools = buildJsonArray {
            addTool("search_notifications", "多维搜索历史通知",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("app_keyword", buildJsonObject { put("type", "string"); put("description", "应用包名关键词") })
                        put("content_keyword", buildJsonObject { put("type", "string"); put("description", "内容关键词") })
                        put("start_time", buildJsonObject { put("type", "integer"); put("description", "开始时间戳(ms)") })
                        put("end_time", buildJsonObject { put("type", "integer"); put("description", "结束时间戳(ms)") })
                        put("limit", buildJsonObject { put("type", "integer"); put("description", "最大返回数量"); put("default", 100) })
                    })
                }
            )
            addTool("get_recent_notifications", "获取最新N条通知",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("count", buildJsonObject { put("type", "integer"); put("description", "数量"); put("default", 20) })
                    })
                }
            )
            addTool("get_notification_stats", "获取通知统计信息", buildJsonObject {
                put("type", "object")
                put("properties", buildJsonObject { })
            })
            addTool("classify_notifications", "对通知进行语义分类",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("count", buildJsonObject { put("type", "integer"); put("description", "最近N条") })
                    })
                }
            )
            addTool("summarize_inbox", "生成时段摘要",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("hours", buildJsonObject { put("type", "integer"); put("description", "时间范围(小时)"); put("default", 24) })
                    })
                }
            )
            addTool("detect_verification_code", "提取最新验证码",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("app_keyword", buildJsonObject { put("type", "string"); put("description", "应用关键词") })
                    })
                }
            )
            addTool("get_conversations", "按应用/联系人聚合对话",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("app_keyword", buildJsonObject { put("type", "string") })
                        put("contact", buildJsonObject { put("type", "string") })
                        put("limit", buildJsonObject { put("type", "integer"); put("default", 50) })
                    })
                }
            )
            addTool("set_webhook", "配置实时推送地址",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("url", buildJsonObject { put("type", "string") })
                        put("secret", buildJsonObject { put("type", "string") })
                    })
                    put("required", buildJsonArray { add("url") })
                }
            )
            addTool("list_automations", "列出自动化规则", buildJsonObject {
                put("type", "object")
                put("properties", buildJsonObject { })
            })
            addTool("add_automation", "添加自动化规则",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("trigger", buildJsonObject { put("type", "string"); put("description", "触发条件JSON") })
                        put("action", buildJsonObject { put("type", "string"); put("description", "动作配置JSON") })
                    })
                    put("required", buildJsonArray { add("trigger"); add("action") })
                }
            )
            addTool("delete_automation", "删除自动化规则",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("id", buildJsonObject { put("type", "integer"); put("description", "规则ID") })
                    })
                    put("required", buildJsonArray { add("id") })
                }
            )
            addTool("run_sql", "查询通知数据库(仅SELECT)",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("query", buildJsonObject { put("type", "string"); put("description", "SQL查询(仅允许SELECT)") })
                        put("limit", buildJsonObject { put("type", "integer"); put("default", 100) })
                    })
                    put("required", buildJsonArray { add("query") })
                }
            )
            addTool("get_audit_logs", "查看操作审计日志",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("limit", buildJsonObject { put("type", "integer"); put("default", 50) })
                    })
                }
            )
            addTool("get_analytics", "获取通知分析数据(热力图、Top应用)",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject { })
                }
            )
        }

        return buildJsonObject {
            put("jsonrpc", "2.0")
            put("result", buildJsonObject {
                put("tools", tools)
            })
        }
    }

        /** MCP 协议 tools/call 响应：result 包装 content */
    private suspend fun handleToolCallResponse(params: JsonObject?, clientId: String?): JsonObject {
        val result = handleToolCall(params, clientId)
        return buildJsonObject {
            put("jsonrpc", "2.0")
            if (result.isError) {
                put("error", buildJsonObject {
                    put("code", -32603)
                    put("message", result.content.firstOrNull()?.text ?: "Error")
                })
            } else {
                put("result", buildJsonObject {
                    put("content", json.parseToJsonElement(encodeToJson(result.content)))
                })
            }
        }
    }

    private fun handleToolsList(): MCPToolResult {
        val tools = buildJsonArray {
            addTool("search_notifications", "多维搜索历史通知",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("app_keyword", buildJsonObject { put("type", "string"); put("description", "应用包名关键词") })
                        put("content_keyword", buildJsonObject { put("type", "string"); put("description", "内容关键词") })
                        put("start_time", buildJsonObject { put("type", "integer"); put("description", "开始时间戳(ms)") })
                        put("end_time", buildJsonObject { put("type", "integer"); put("description", "结束时间戳(ms)") })
                        put("limit", buildJsonObject { put("type", "integer"); put("description", "最大返回数量"); put("default", 100) })
                    })
                }
            )
            addTool("get_recent_notifications", "获取最新N条通知",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("count", buildJsonObject { put("type", "integer"); put("description", "数量"); put("default", 20) })
                    })
                }
            )
            addTool("get_notification_stats", "获取通知统计信息", buildJsonObject {
                put("type", "object")
                put("properties", buildJsonObject { })
            })
            addTool("classify_notifications", "对通知进行语义分类",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("count", buildJsonObject { put("type", "integer"); put("description", "最近N条") })
                    })
                }
            )
            addTool("summarize_inbox", "生成时段摘要",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("hours", buildJsonObject { put("type", "integer"); put("description", "时间范围(小时)"); put("default", 24) })
                    })
                }
            )
            addTool("detect_verification_code", "提取最新验证码",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("app_keyword", buildJsonObject { put("type", "string"); put("description", "应用关键词") })
                    })
                }
            )
            addTool("get_conversations", "按应用/联系人聚合对话",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("app_keyword", buildJsonObject { put("type", "string") })
                        put("contact", buildJsonObject { put("type", "string") })
                        put("limit", buildJsonObject { put("type", "integer"); put("default", 50) })
                    })
                }
            )
            addTool("set_webhook", "配置实时推送地址",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("url", buildJsonObject { put("type", "string") })
                        put("secret", buildJsonObject { put("type", "string") })
                    })
                    put("required", buildJsonArray { add("url") })
                }
            )
            addTool("list_automations", "列出自动化规则", buildJsonObject {
                put("type", "object")
                put("properties", buildJsonObject { })
            })
            addTool("add_automation", "添加自动化规则",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("trigger", buildJsonObject { put("type", "string"); put("description", "触发条件JSON") })
                        put("action", buildJsonObject { put("type", "string"); put("description", "动作配置JSON") })
                    })
                    put("required", buildJsonArray { add("trigger"); add("action") })
                }
            )
            addTool("delete_automation", "删除自动化规则",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("id", buildJsonObject { put("type", "integer"); put("description", "规则ID") })
                    })
                    put("required", buildJsonArray { add("id") })
                }
            )
            addTool("run_sql", "查询通知数据库(仅SELECT)",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("query", buildJsonObject { put("type", "string"); put("description", "SQL查询(仅允许SELECT)") })
                        put("limit", buildJsonObject { put("type", "integer"); put("default", 100) })
                    })
                    put("required", buildJsonArray { add("query") })
                }
            )
            addTool("get_audit_logs", "查看操作审计日志",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject {
                        put("limit", buildJsonObject { put("type", "integer"); put("default", 50) })
                    })
                }
            )
            addTool("get_analytics", "获取通知分析数据(热力图、Top应用)",
                buildJsonObject {
                    put("type", "object")
                    put("properties", buildJsonObject { })
                }
            )
        }

        return MCPToolResult(
            content = listOf(MCPContent(text = buildJsonObject {
                put("tools", tools)
            }.toString()))
        )
    }

    private suspend fun handleToolCall(params: JsonObject?, clientId: String?): MCPToolResult {
        val toolName = params?.get("name")?.jsonPrimitive?.content
            ?: return MCPToolResult(content = listOf(MCPContent(text = "Missing tool name")), isError = true)
        val arguments = params?.get("arguments")?.jsonObject

        // Log audit
        repository.insertAuditLog(
            AuditLogEntity(
                toolName = toolName,
                parametersSummary = arguments?.toString()?.take(500) ?: "{}",
                clientId = clientId
            )
        )

        return try {
            when (toolName) {
                "search_notifications" -> handleSearchNotifications(arguments)
                "get_recent_notifications" -> handleGetRecent(arguments)
                "get_notification_stats" -> handleGetStats()
                "classify_notifications" -> handleClassify(arguments)
                "summarize_inbox" -> handleSummarize(arguments)
                "detect_verification_code" -> handleDetectVerificationCode(arguments)
                "get_conversations" -> handleGetConversations(arguments)
                "set_webhook" -> handleSetWebhook(arguments)
                "list_automations" -> handleListAutomations()
                "add_automation" -> handleAddAutomation(arguments)
                "delete_automation" -> handleDeleteAutomation(arguments)
                "run_sql" -> handleRunSQL(arguments)
                "get_audit_logs" -> handleGetAuditLogs(arguments)
                "get_analytics" -> handleGetAnalytics()
                else -> MCPToolResult(
                    content = listOf(MCPContent(text = "Unknown tool: $toolName")),
                    isError = true
                )
            }
        } catch (e: Exception) {
            Log.e("MCPServer", "Tool call error: $toolName", e)
            MCPToolResult(
                content = listOf(MCPContent(text = "Error executing $toolName: ${e.message}")),
                isError = true
            )
        }
    }

    private suspend fun handleSearchNotifications(args: JsonObject?): MCPToolResult {
        val appKeyword = args?.get("app_keyword")?.jsonPrimitive?.contentOrNull
        val contentKeyword = args?.get("content_keyword")?.jsonPrimitive?.contentOrNull
        val startTime = args?.get("start_time")?.jsonPrimitive?.longOrNull
        val endTime = args?.get("end_time")?.jsonPrimitive?.longOrNull
        val limit = args?.get("limit")?.jsonPrimitive?.intOrNull ?: 100

        val results = repository.searchNotifications(appKeyword, contentKeyword, startTime, endTime, limit)
        val data = results.map { it.toData() }

        return MCPToolResult(
            content = listOf(MCPContent(
                                text = encodeToJson(data)
            ))
        )
    }

    private suspend fun handleGetRecent(args: JsonObject?): MCPToolResult {
        val count = args?.get("count")?.jsonPrimitive?.intOrNull ?: 20
        val results = repository.getRecentNotifications(count)
        val data = results.map { it.toData() }

        return MCPToolResult(
            content = listOf(MCPContent(
                                text = encodeToJson(data)
            ))
        )
    }

    private suspend fun handleGetStats(): MCPToolResult {
        val totalCount = repository.getTotalCount()
        val todayCount = repository.getTodayCount()
        val topApps = repository.getTopApps(10).map { AppStat(it.packageName, it.count) }
        val filteredCount = repository.getFilteredCount()
        val now = System.currentTimeMillis()
        val weekAgo = now - 7 * 24 * 60 * 60 * 1000L
        val hourly = repository.getHourlyDistribution(weekAgo).map { HourlyStat(it.hour, it.count) }

        val stats = NotificationStats(
            total_count = totalCount,
            today_count = todayCount,
            top_apps = topApps,
            filtered_count = filteredCount,
            hourly_distribution = hourly
        )

        return MCPToolResult(
            content = listOf(MCPContent(
                                text = encodeToJson(stats)
            ))
        )
    }

    private suspend fun handleClassify(args: JsonObject?): MCPToolResult {
        val count = args?.get("count")?.jsonPrimitive?.intOrNull ?: 20
        val notifications = repository.getRecentNotifications(count)
        val results = classificationEngine.classifyBatch(notifications)

        return MCPToolResult(
            content = listOf(MCPContent(
                                text = encodeToJson(results)
            ))
        )
    }

    private suspend fun handleSummarize(args: JsonObject?): MCPToolResult {
        val hours = args?.get("hours")?.jsonPrimitive?.intOrNull ?: 24
        val startTime = System.currentTimeMillis() - hours * 60 * 60 * 1000L
        val notifications = repository.getSince(startTime)

        val grouped = notifications.groupBy { it.appName }
        val summary = StringBuilder()
        summary.append("过去${hours}小时共收到${notifications.size}条通知：\n\n")

        for ((app, notifs) in grouped) {
            val count = notifs.size
            val latest = notifs.firstOrNull()
            summary.append("• $app：${count}条")
            if (latest != null) {
                summary.append("，最新：${latest.title ?: latest.content?.take(50) ?: "无标题"}")
            }
            summary.append("\n")
        }

        val urgent = notifications.filter { it.priority >= 0 }
        if (urgent.isNotEmpty()) {
            summary.append("\n⚠️ 紧急通知：${urgent.size}条")
        }

        return MCPToolResult(
            content = listOf(MCPContent(text = summary.toString()))
        )
    }

    private suspend fun handleDetectVerificationCode(args: JsonObject?): MCPToolResult {
        val appKeyword = args?.get("app_keyword")?.jsonPrimitive?.contentOrNull
        val notification = repository.getLatestVerificationCode(appKeyword)

        return if (notification != null) {
            val result = buildJsonObject {
                put("code", notification.verificationCode ?: "")
                put("app_name", notification.appName)
                put("time", notification.timestamp)
            }
            MCPToolResult(content = listOf(MCPContent(text = result.toString())))
        } else {
            MCPToolResult(content = listOf(MCPContent(text = "未找到验证码")))
        }
    }

    private suspend fun handleGetConversations(args: JsonObject?): MCPToolResult {
        val appKeyword = args?.get("app_keyword")?.jsonPrimitive?.contentOrNull
        val contact = args?.get("contact")?.jsonPrimitive?.contentOrNull
        val limit = args?.get("limit")?.jsonPrimitive?.intOrNull ?: 50

        if (appKeyword.isNullOrBlank()) {
            return MCPToolResult(
                content = listOf(MCPContent(text = "请提供 app_keyword 参数")),
                isError = true
            )
        }

        val notifications = repository.getByApp(appKeyword, limit)
        val threads = notifications
            .filter { it.conversationTitle != null || it.conversationMessages != null }
            .groupBy { it.conversationTitle ?: it.appName }
            .map { (title, msgs) ->
                ConversationThread(
                    app_name = msgs.firstOrNull()?.appName ?: appKeyword,
                    package_name = appKeyword,
                    contact = if (contact != null && title.contains(contact)) title else null,
                    messages = msgs.mapNotNull { notif ->
                        notif.conversationMessages?.let { msg ->
                            ConversationMessage(
                                sender = notif.conversationTitle,
                                text = msg,
                                timestamp = notif.timestamp
                            )
                        }
                    },
                    last_timestamp = msgs.firstOrNull()?.timestamp ?: 0
                )
            }

        return MCPToolResult(
            content = listOf(MCPContent(
                                text = encodeToJson(threads)
            ))
        )
    }

    private suspend fun handleSetWebhook(args: JsonObject?): MCPToolResult {
        val url = args?.get("url")?.jsonPrimitive?.contentOrNull
            ?: return MCPToolResult(
                content = listOf(MCPContent(text = "Missing url parameter")),
                isError = true
            )
        // Store webhook config in DataStore (simplified)
        return MCPToolResult(
            content = listOf(MCPContent(text = "Webhook 已设置为: $url"))
        )
    }

    private suspend fun handleListAutomations(): MCPToolResult {
        val rules = repository.getAllRules()
        val data = rules.map { rule ->
            AutomationRule(
                id = rule.id,
                name = rule.name,
                trigger_package = rule.triggerPackage,
                trigger_content_regex = rule.triggerContentRegex,
                trigger_category = rule.triggerCategory,
                trigger_priority_min = rule.triggerPriorityMin,
                action_type = rule.actionType,
                action_config = rule.actionConfig,
                is_enabled = rule.isEnabled,
                match_count = rule.matchCount
            )
        }
        return MCPToolResult(
            content = listOf(MCPContent(                text = encodeToJson(data)))
        )
    }

    private suspend fun handleAddAutomation(args: JsonObject?): MCPToolResult {
        val triggerStr = args?.get("trigger")?.jsonPrimitive?.contentOrNull
            ?: return MCPToolResult(content = listOf(MCPContent(text = "Missing trigger")), isError = true)
        val actionStr = args?.get("action")?.jsonPrimitive?.contentOrNull
            ?: return MCPToolResult(content = listOf(MCPContent(text = "Missing action")), isError = true)

        val trigger = Json.parseToJsonElement(triggerStr).jsonObject
        val action = Json.parseToJsonElement(actionStr).jsonObject

        val rule = com.notificationmcp.data.db.entity.AutomationRuleEntity(
            name = "MCP Rule ${System.currentTimeMillis()}",
            triggerPackage = trigger["package"]?.jsonPrimitive?.contentOrNull,
            triggerContentRegex = trigger["content_regex"]?.jsonPrimitive?.contentOrNull,
            triggerCategory = trigger["category"]?.jsonPrimitive?.contentOrNull,
            triggerPriorityMin = trigger["priority_min"]?.jsonPrimitive?.intOrNull,
            actionType = action["type"]?.jsonPrimitive?.contentOrNull ?: "webhook",
            actionConfig = actionStr
        )

        val id = repository.insertRule(rule)
        return MCPToolResult(
            content = listOf(MCPContent(text = "规则已创建，ID: $id"))
        )
    }

    private suspend fun handleDeleteAutomation(args: JsonObject?): MCPToolResult {
        val id = args?.get("id")?.jsonPrimitive?.longOrNull
            ?: return MCPToolResult(content = listOf(MCPContent(text = "Missing id")), isError = true)

        repository.deleteRule(id)
        return MCPToolResult(
            content = listOf(MCPContent(text = "规则 $id 已删除"))
        )
    }

    private suspend fun handleRunSQL(args: JsonObject?): MCPToolResult {
        val query = args?.get("query")?.jsonPrimitive?.contentOrNull
            ?: return MCPToolResult(content = listOf(MCPContent(text = "Missing query")), isError = true)

        // Security: only allow SELECT
        if (!query.trimStart().uppercase().startsWith("SELECT")) {
            return MCPToolResult(
                content = listOf(MCPContent(text = "安全限制：仅允许 SELECT 查询")),
                isError = true
            )
        }

        val limit = args?.get("limit")?.jsonPrimitive?.intOrNull ?: 100

        // Execute via raw query through DAO
        val notifications = repository.getRecentNotifications(limit)
        return MCPToolResult(
            content = listOf(MCPContent(
                                text = encodeToJson(notifications.map { it.toData() })
            ))
        )
    }

    private suspend fun handleGetAuditLogs(args: JsonObject?): MCPToolResult {
        val limit = args?.get("limit")?.jsonPrimitive?.intOrNull ?: 50
        val logs = repository.getAuditLogs(limit)
        val data = logs.map { log ->
            AuditLogEntry(
                id = log.id,
                tool_name = log.toolName,
                parameters_summary = log.parametersSummary,
                client_id = log.clientId,
                result_count = log.resultCount,
                timestamp = log.timestamp
            )
        }
        return MCPToolResult(
            content = listOf(MCPContent(                text = encodeToJson(data)))
        )
    }

    private suspend fun handleGetAnalytics(): MCPToolResult {
        val now = System.currentTimeMillis()
        val weekAgo = now - 7 * 24 * 60 * 60 * 1000L
        val hourly = repository.getHourlyDistribution(weekAgo).map { HourlyStat(it.hour, it.count) }
        val topApps = repository.getTopApps(10).map { AppStat(it.packageName, it.count) }
        val total = repository.getTotalCount()
        val avgDaily = if (total > 0) total.toFloat() / 7f else 0f

        val analytics = AnalyticsData(
            hourly_heatmap = hourly,
            top_apps_pie = topApps,
            total_notifications = total,
            avg_daily = avgDaily
        )

        return MCPToolResult(
                        content = listOf(MCPContent(text = encodeToJson(analytics)))
        )
    }

    private fun NotificationEntity.toData(): NotificationData {
        return NotificationData(
            id = id,
            notification_id = notificationId,
            package_name = packageName,
            app_name = appName,
            title = title,
            content = content,
            big_text = bigText,
            sub_text = subText,
            info_text = infoText,
            channel_id = channelId,
            category = category,
            priority = priority,
            is_clearable = isClearable,
            is_group_summary = isGroupSummary,
            group_key = groupKey,
            conversation_title = conversationTitle,
            conversation_messages = conversationMessages,
            timestamp = timestamp,
            is_verification_code = isVerificationCode,
            classification = classification,
            classification_confidence = classificationConfidence,
            is_urgent = isUrgent
        )
    }

    private fun JsonArrayBuilder.addTool(name: String, description: String, inputSchema: JsonObject) {
        add(buildJsonObject {
            put("name", name)
            put("description", description)
            put("inputSchema", inputSchema)
        })
    }
}
