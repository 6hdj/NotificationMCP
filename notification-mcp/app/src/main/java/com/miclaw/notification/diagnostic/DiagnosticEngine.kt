package com.miclaw.notification.diagnostic

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.miclaw.notification.db.AppDatabase
import com.miclaw.notification.service.McpWebSocketManager
import com.miclaw.notification.service.NotificationListenerServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 一键故障排查引擎
 *
 * 六大诊断链路，按依赖顺序执行：
 *   1. 权限校验 — 通知使用权、电池优化、自启动、后台弹出界面
 *   2. 服务状态 — 监听服务、前台服务、守护进程、崩溃记录
 *   3. 系统拦截 — Doze、省电模式、MIUI电池策略、神隐模式、内存压力
 *   4. 连接状态 — 网络、WebSocket、心跳
 *   5. 资源状态 — 通知队列、数据库、存储空间
 *   6. 日志分析 — 错误日志、系统杀进程事件
 */
class DiagnosticEngine(private val context: Context) {

    private val checks = mutableListOf<CheckItem>()

    /**
     * 执行全链路诊断
     */
    suspend fun runFullDiagnostic(): DiagnosticReport = withContext(Dispatchers.IO) {
        checks.clear()

        // 六大诊断链路
        checkPermissions()
        checkServiceStatus()
        checkSystemBlocking()
        checkConnection()
        checkResources()
        analyzeLogs()

        DiagnosticReport(
            timestamp = System.currentTimeMillis(),
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
            systemVersion = getSystemVersion(),
            checks = checks.toList()
        )
    }

    /**
     * 执行指定类别的诊断
     */
    suspend fun runCategoryDiagnostic(category: CheckCategory): DiagnosticReport = withContext(Dispatchers.IO) {
        checks.clear()

        when (category) {
            CheckCategory.PERMISSION -> checkPermissions()
            CheckCategory.SERVICE -> checkServiceStatus()
            CheckCategory.SYSTEM_BLOCK -> checkSystemBlocking()
            CheckCategory.CONNECTION -> checkConnection()
            CheckCategory.RESOURCE -> checkResources()
            CheckCategory.LOG -> analyzeLogs()
        }

        DiagnosticReport(
            timestamp = System.currentTimeMillis(),
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
            systemVersion = getSystemVersion(),
            checks = checks.toList()
        )
    }

    // ═══════════════════════════════════════════════════
    //  第一链路：权限校验
    // ═══════════════════════════════════════════════════

    private fun checkPermissions() {
        checkNotificationListenerPermission()
        checkPostNotificationPermission()
        checkBatteryOptimization()
        checkAutoStartPermission()
        checkBackgroundPopupPermission()
    }

    private fun checkNotificationListenerPermission() {
        val cn = ComponentName(context, "com.miclaw.notification.service.NotificationListenerServiceImpl")
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners") ?: ""
        val enabled = flat.contains(cn.flattenToString())

        checks.add(CheckItem(
            name = "通知使用权",
            category = CheckCategory.PERMISSION,
            status = if (enabled) CheckStatus.PASS else CheckStatus.FAIL,
            detail = if (enabled) "已授权，可正常监听通知" else "未授权！这是核心权限，缺失将导致完全无法读取通知",
            fixAction = if (!enabled) "跳转到通知使用权设置" else null,
            fixIntent = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
        ))
    }

    private fun checkPostNotificationPermission() {
        if (Build.VERSION.SDK_INT < 33) {
            checks.add(CheckItem(
                name = "通知发送权限",
                category = CheckCategory.PERMISSION,
                status = CheckStatus.PASS,
                detail = "Android ${Build.VERSION.SDK_INT}，无需运行时通知权限"
            ))
            return
        }

        val granted = context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED

        checks.add(CheckItem(
            name = "通知发送权限",
            category = CheckCategory.PERMISSION,
            status = if (granted) CheckStatus.PASS else CheckStatus.WARN,
            detail = if (granted) "已授权，可发送状态通知和告警" else "未授权，服务状态通知和告警通知将无法显示",
            fixAction = if (!granted) "跳转到通知设置" else null,
            fixIntent = "android.settings.APP_NOTIFICATION_SETTINGS"
        ))
    }

    private fun checkBatteryOptimization() {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val ignoring = pm.isIgnoringBatteryOptimizations(context.packageName)

        checks.add(CheckItem(
            name = "电池优化白名单",
            category = CheckCategory.PERMISSION,
            status = if (ignoring) CheckStatus.PASS else CheckStatus.FAIL,
            detail = if (ignoring) "已设为无限制，系统不会冻结服务进程"
                     else "未加入白名单！系统会在 Doze 模式下冻结或杀死服务",
            fixAction = if (!ignoring) "跳转到电池优化设置" else null,
            fixIntent = "android.os.action.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
        ))
    }

    private fun checkAutoStartPermission() {
        if (!isMiuiOrHyperOs()) {
            checks.add(CheckItem(
                name = "自启动权限",
                category = CheckCategory.PERMISSION,
                status = CheckStatus.PASS,
                detail = "非 MIUI/HyperOS 系统，无需检查"
            ))
            return
        }

        val allowed = try {
            val appOps = context.getSystemService("appops") as android.app.AppOpsManager
            val mode = appOps.unsafeCheckOpNoThrow(
                "android:self_manage_auto_start",
                android.os.Process.myUid(),
                context.packageName
            )
            mode == android.app.AppOpsManager.MODE_DEFAULT ||
            mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) { true }

        checks.add(CheckItem(
            name = "自启动权限",
            category = CheckCategory.PERMISSION,
            status = if (allowed) CheckStatus.PASS else CheckStatus.WARN,
            detail = if (allowed) "已允许自启动" else "未允许自启动，服务可能在重启后无法自动运行",
            fixAction = if (!allowed) "跳转到自启动管理" else null
        ))
    }

    private fun checkBackgroundPopupPermission() {
        if (!isMiuiOrHyperOs()) {
            checks.add(CheckItem(
                name = "后台弹出界面权限",
                category = CheckCategory.PERMISSION,
                status = CheckStatus.PASS,
                detail = "非 MIUI/HyperOS 系统，无需检查"
            ))
            return
        }

        val allowed = try {
            val appOps = context.getSystemService("appops") as android.app.AppOpsManager
            val mode = appOps.unsafeCheckOpNoThrow(
                "android:background_start",
                android.os.Process.myUid(),
                context.packageName
            )
            mode == android.app.AppOpsManager.MODE_DEFAULT ||
            mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) { true }

        checks.add(CheckItem(
            name = "后台弹出界面权限",
            category = CheckCategory.PERMISSION,
            status = if (allowed) CheckStatus.PASS else CheckStatus.WARN,
            detail = if (allowed) "已允许" else "未允许，后台告警通知可能无法正常显示",
            fixAction = if (!allowed) "跳转到权限管理" else null
        ))
    }

    // ═══════════════════════════════════════════════════
    //  第二链路：服务状态检测
    // ═══════════════════════════════════════════════════

    private fun checkServiceStatus() {
        checkListenerServiceRunning()
        checkKeepAliveServiceRunning()
        checkWatchdogServiceRunning()
        checkServiceCrashCount()
    }

    private fun checkListenerServiceRunning() {
        val running = isServiceRunning("com.miclaw.notification.service.NotificationListenerServiceImpl")
        checks.add(CheckItem(
            name = "通知监听服务",
            category = CheckCategory.SERVICE,
            status = if (running) CheckStatus.PASS else CheckStatus.FAIL,
            detail = if (running) "服务正在运行，正在监听通知" else "服务未运行！无法接收任何通知",
            fixAction = if (!running) "重启通知监听服务" else null
        ))
    }

    private fun checkKeepAliveServiceRunning() {
        val running = isServiceRunning("com.miclaw.notification.service.KeepAliveService")
        checks.add(CheckItem(
            name = "前台保活服务",
            category = CheckCategory.SERVICE,
            status = if (running) CheckStatus.PASS else CheckStatus.WARN,
            detail = if (running) "前台服务正在运行" else "前台服务未运行，保活能力降低",
            fixAction = if (!running) "重启前台保活服务" else null
        ))
    }

    private fun checkWatchdogServiceRunning() {
        val running = isServiceRunning("com.miclaw.notification.service.WatchdogService")
        checks.add(CheckItem(
            name = "守护进程",
            category = CheckCategory.SERVICE,
            status = if (running) CheckStatus.PASS else CheckStatus.WARN,
            detail = if (running) "守护进程正在运行" else "守护进程未运行，主服务被杀后无法自动重启",
            fixAction = if (!running) "重启守护进程" else null
        ))
    }

    private fun checkServiceCrashCount() {
        val prefs = context.getSharedPreferences("diagnostic", Context.MODE_PRIVATE)
        val crashCount = prefs.getInt("crash_count_today", 0)
        val lastCrash = prefs.getLong("last_crash_time", 0)

        checks.add(CheckItem(
            name = "崩溃记录",
            category = CheckCategory.SERVICE,
            status = when {
                crashCount == 0 -> CheckStatus.PASS
                crashCount < 3 -> CheckStatus.WARN
                else -> CheckStatus.FAIL
            },
            detail = when {
                crashCount == 0 -> "今日无崩溃记录"
                crashCount < 3 -> "今日崩溃 ${crashCount} 次，上次: ${formatTime(lastCrash)}"
                else -> "今日崩溃 ${crashCount} 次！服务严重不稳定，可能触发熔断"
            },
            fixAction = if (crashCount >= 3) "查看崩溃日志" else null
        ))
    }

    // ═══════════════════════════════════════════════════
    //  第三链路：系统拦截检测
    // ═══════════════════════════════════════════════════

    private fun checkSystemBlocking() {
        checkDozeMode()
        checkLowPowerMode()
        checkMiuiBatterySaver()
        checkMiuiHiddenMode()
        checkMemoryPressure()
    }

    private fun checkDozeMode() {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val inDoze = if (Build.VERSION.SDK_INT >= 23) pm.isDeviceIdleMode else false
        checks.add(CheckItem(
            name = "Doze 模式",
            category = CheckCategory.SYSTEM_BLOCK,
            status = if (!inDoze) CheckStatus.PASS else CheckStatus.WARN,
            detail = if (!inDoze) "设备未处于 Doze 模式" else "设备处于 Doze 模式！后台网络和任务可能被限制",
            fixAction = if (inDoze) "移动设备或点亮屏幕以退出 Doze" else null
        ))
    }

    private fun checkLowPowerMode() {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val lowPower = pm.isPowerSaveMode
        checks.add(CheckItem(
            name = "省电模式",
            category = CheckCategory.SYSTEM_BLOCK,
            status = if (!lowPower) CheckStatus.PASS else CheckStatus.WARN,
            detail = if (!lowPower) "省电模式未开启" else "省电模式已开启！系统会限制后台活动和网络访问",
            fixAction = if (lowPower) "关闭省电模式" else null,
            fixIntent = "android.settings.BATTERY_SAVER_SETTINGS"
        ))
    }

    private fun checkMiuiBatterySaver() {
        if (!isMiuiOrHyperOs()) {
            checks.add(CheckItem(
                name = "MIUI 电池策略",
                category = CheckCategory.SYSTEM_BLOCK,
                status = CheckStatus.PASS,
                detail = "非 MIUI 系统，跳过"
            ))
            return
        }

        val restricted = try {
            val appOps = context.getSystemService("appops") as android.app.AppOpsManager
            val mode = appOps.unsafeCheckOpNoThrow(
                "android:run_in_background",
                android.os.Process.myUid(),
                context.packageName
            )
            mode == android.app.AppOpsManager.MODE_DEFAULT
        } catch (e: Exception) { false }

        checks.add(CheckItem(
            name = "MIUI 电池策略",
            category = CheckCategory.SYSTEM_BLOCK,
            status = if (!restricted) CheckStatus.PASS else CheckStatus.WARN,
            detail = if (!restricted) "MIUI 电池策略未限制后台运行"
                     else "MIUI 电池策略可能限制了后台运行",
            fixAction = if (restricted) "设置 → 电池 → 电池优化 → 通知读取 → 无限制" else null
        ))
    }

    private fun checkMiuiHiddenMode() {
        if (!isMiuiOrHyperOs()) {
            checks.add(CheckItem(
                name = "神隐模式",
                category = CheckCategory.SYSTEM_BLOCK,
                status = CheckStatus.PASS,
                detail = "非 MIUI 系统，跳过"
            ))
            return
        }

        val hidden = try {
            val appOps = context.getSystemService("appops") as android.app.AppOpsManager
            val mode = appOps.unsafeCheckOpNoThrow(
                "android:run_any_in_background",
                android.os.Process.myUid(),
                context.packageName
            )
            mode == android.app.AppOpsManager.MODE_ERRORED ||
            mode == android.app.AppOpsManager.MODE_IGNORED
        } catch (e: Exception) { false }

        checks.add(CheckItem(
            name = "神隐模式",
            category = CheckCategory.SYSTEM_BLOCK,
            status = if (!hidden) CheckStatus.PASS else CheckStatus.FAIL,
            detail = if (!hidden) "未被神隐模式限制"
                     else "应用被神隐模式限制！系统会严格限制后台活动",
            fixAction = if (hidden) "设置 → 电池 → 应用智能管控 → 通知读取 → 无限制" else null
        ))
    }

    private fun checkMemoryPressure() {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)

        val availMB = memInfo.availMem / (1024 * 1024)
        val totalMB = memInfo.totalMem / (1024 * 1024)
        val percent = (availMB * 100 / totalMB)

        checks.add(CheckItem(
            name = "可用内存",
            category = CheckCategory.SYSTEM_BLOCK,
            status = when {
                percent > 20 -> CheckStatus.PASS
                percent > 10 -> CheckStatus.WARN
                else -> CheckStatus.FAIL
            },
            detail = "可用 ${availMB}MB / 总共 ${totalMB}MB (${percent}%)" +
                     when {
                         percent > 20 -> ""
                         percent > 10 -> " — 内存偏低，服务可能被 LMK 杀死"
                         else -> " — 内存严重不足！服务极可能被系统回收"
                     },
            fixAction = if (percent <= 20) "清理后台释放内存" else null
        ))
    }

    // ═══════════════════════════════════════════════════
    //  第四链路：连接状态检测
    // ═══════════════════════════════════════════════════

    private fun checkConnection() {
        checkNetworkAvailable()
        checkWebSocketConnection()
        checkHeartbeatStatus()
    }

    private fun checkNetworkAvailable() {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = network?.let { cm.getNetworkCapabilities(it) }
        val connected = caps != null
        val type = when {
            caps == null -> "无网络"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "移动数据"
            else -> "其他"
        }

        checks.add(CheckItem(
            name = "网络连接",
            category = CheckCategory.CONNECTION,
            status = if (connected) CheckStatus.PASS else CheckStatus.FAIL,
            detail = if (connected) "已连接 ($type)" else "无网络连接！WebSocket 无法建立通信",
            fixAction = if (!connected) "请检查网络连接" else null
        ))
    }

    private fun checkWebSocketConnection() {
        val connected = McpWebSocketManager.getInstance().isConnected()
        checks.add(CheckItem(
            name = "WebSocket 连接",
            category = CheckCategory.CONNECTION,
            status = if (connected) CheckStatus.PASS else CheckStatus.FAIL,
            detail = if (connected) "已连接到电脑端 Miclaw" else "未连接！通知无法转发到电脑端",
            fixAction = if (!connected) "重新建立 WebSocket 连接" else null
        ))
    }

    private fun checkHeartbeatStatus() {
        val lastHeartbeat = NotificationListenerServiceImpl.stats.lastHeartbeatTime ?: 0L
        val gap = if (lastHeartbeat > 0) (System.currentTimeMillis() - lastHeartbeat) / 1000 else -1L

        checks.add(CheckItem(
            name = "心跳状态",
            category = CheckCategory.CONNECTION,
            status = when {
                lastHeartbeat == 0L -> CheckStatus.INFO
                gap < 90 -> CheckStatus.PASS
                gap < 300 -> CheckStatus.WARN
                else -> CheckStatus.FAIL
            },
            detail = when {
                lastHeartbeat == 0L -> "尚未收到心跳响应"
                gap < 90 -> "正常，${gap}s 前收到心跳"
                gap < 300 -> "心跳延迟 ${gap}s，连接可能不稳定"
                else -> "已 ${gap}s 无心跳响应，连接可能已断开"
            },
            fixAction = if (lastHeartbeat > 0 && gap >= 90) "重新建立连接" else null
        ))
    }

    // ═══════════════════════════════════════════════════
    //  第五链路：资源状态
    // ═══════════════════════════════════════════════════

    private suspend fun checkResources() {
        checkNotificationQueueSize()
        checkDatabaseSize()
        checkStorageSpace()
    }

    private suspend fun checkNotificationQueueSize() {
        val db = AppDatabase.getInstance(context)
        val count = db.notificationDao().getUnforwardedCount()

        checks.add(CheckItem(
            name = "未转发通知队列",
            category = CheckCategory.RESOURCE,
            status = when {
                count < 100 -> CheckStatus.PASS
                count < 500 -> CheckStatus.WARN
                else -> CheckStatus.FAIL
            },
            detail = "队列中有 $count 条未转发通知" +
                     when {
                         count < 100 -> ""
                         count < 500 -> " — 队列偏大，可能影响性能"
                         else -> " — 队列严重积压！可能已断连较长时间"
                     },
            fixAction = if (count >= 100) "立即补传积压通知" else null
        ))
    }

    private fun checkDatabaseSize() {
        val dbFile = context.getDatabasePath("notification_mcp.db")
        val sizeMB = if (dbFile.exists()) dbFile.length() / (1024 * 1024) else 0L

        checks.add(CheckItem(
            name = "数据库大小",
            category = CheckCategory.RESOURCE,
            status = when {
                sizeMB < 50 -> CheckStatus.PASS
                sizeMB < 200 -> CheckStatus.WARN
                else -> CheckStatus.FAIL
            },
            detail = "数据库大小: ${sizeMB}MB" +
                     when {
                         sizeMB < 50 -> ""
                         sizeMB < 200 -> " — 建议定期清理旧数据"
                         else -> " — 数据库过大！严重影响性能"
                     },
            fixAction = if (sizeMB >= 50) "清理 72 小时前的旧通知数据" else null
        ))
    }

    private fun checkStorageSpace() {
        val stat = android.os.StatFs(context.filesDir.absolutePath)
        val availGB = stat.availableBlocksLong * stat.blockSizeLong / (1024 * 1024 * 1024)

        checks.add(CheckItem(
            name = "可用存储空间",
            category = CheckCategory.RESOURCE,
            status = when {
                availGB > 2 -> CheckStatus.PASS
                availGB > 0.5 -> CheckStatus.WARN
                else -> CheckStatus.FAIL
            },
            detail = "可用存储: ${availGB}GB" +
                     if (availGB <= 0.5) " — 存储空间严重不足！" else "",
            fixAction = if (availGB <= 2) "清理存储空间" else null
        ))
    }

    // ═══════════════════════════════════════════════════
    //  第六链路：日志分析
    // ═══════════════════════════════════════════════════

    private fun analyzeLogs() {
        analyzeRecentErrors()
        analyzeSystemKillEvents()
    }

    private fun analyzeRecentErrors() {
        val errorPatterns = listOf(
            "NotificationListenerService" to "通知监听服务异常",
            "SecurityException" to "安全权限异常",
            "RemoteException" to "远程调用异常",
            "OutOfMemoryError" to "内存溢出",
            "ANR" to "应用无响应",
            "killed" to "进程被系统杀死"
        )

        val foundErrors = mutableListOf<String>()
        try {
            val process = Runtime.getRuntime().exec(arrayOf(
                "logcat", "-d", "-t", "100", "-s",
                "NotificationListenerService:*", "ActivityManager:*"
            ))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val logs = reader.readText()
            reader.close()

            for ((pattern, desc) in errorPatterns) {
                if (logs.contains(pattern, ignoreCase = true)) {
                    foundErrors.add(desc)
                }
            }
        } catch (e: Exception) { /* logcat 不可用 */ }

        checks.add(CheckItem(
            name = "错误日志分析",
            category = CheckCategory.LOG,
            status = when {
                foundErrors.isEmpty() -> CheckStatus.PASS
                foundErrors.size <= 2 -> CheckStatus.WARN
                else -> CheckStatus.FAIL
            },
            detail = if (foundErrors.isEmpty()) "最近日志中未发现异常"
                     else "发现异常:\n${foundErrors.joinToString("\n") { "  • $it" }}",
            fixAction = if (foundErrors.isNotEmpty()) "查看详细日志并排查" else null
        ))
    }

    private fun analyzeSystemKillEvents() {
        var killCount = 0
        try {
            val process = Runtime.getRuntime().exec(arrayOf(
                "logcat", "-d", "-t", "200", "-s", "ActivityManager:*"
            ))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val lines = reader.readLines()
            reader.close()

            killCount = lines.count { line ->
                line.contains("Killing", ignoreCase = true) &&
                line.contains(context.packageName, ignoreCase = true)
            }
        } catch (e: Exception) { /* logcat 不可用 */ }

        checks.add(CheckItem(
            name = "系统杀进程事件",
            category = CheckCategory.LOG,
            status = when {
                killCount == 0 -> CheckStatus.PASS
                killCount < 5 -> CheckStatus.WARN
                else -> CheckStatus.FAIL
            },
            detail = when {
                killCount == 0 -> "最近日志中未发现系统杀进程事件"
                killCount < 5 -> "发现 $killCount 次系统杀进程记录"
                else -> "发现 $killCount 次系统杀进程记录！保活设置严重不足"
            },
            fixAction = if (killCount > 0) "检查 MIUI 保活优化设置" else null
        ))
    }

    // ═══════════════════════════════════════════════════
    //  一键修复
    // ═══════════════════════════════════════════════════

    /**
     * 执行一键自动修复
     */
    suspend fun executeAutoFix(report: DiagnosticReport): List<String> = withContext(Dispatchers.IO) {
        val results = mutableListOf<String>()

        for (check in report.checks) {
            if (check.status != CheckStatus.FAIL && check.status != CheckStatus.WARN) continue
            if (check.fixIntent == null) continue

            try {
                val intent = Intent(check.fixIntent).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    if (check.fixIntent == "android.os.action.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS") {
                        data = android.net.Uri.parse("package:${context.packageName}")
                    }
                }
                context.startActivity(intent)
                results.add("✅ ${check.name} — 已打开设置页面")
            } catch (e: Exception) {
                // 备用：跳转到应用详情
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    results.add("⚠️ ${check.name} — 已跳转到应用设置（备用路径）")
                } catch (e2: Exception) {
                    results.add("❌ ${check.name} — 无法自动跳转: ${e2.message}")
                }
            }
        }

        // 尝试重启服务
        try {
            val intent = Intent("com.miclaw.notification.ACTION_RESTART_SERVICE").apply {
                setPackage(context.packageName)
            }
            context.sendBroadcast(intent)
            results.add("✅ 已发送服务重启指令")
        } catch (e: Exception) {
            results.add("❌ 服务重启失败: ${e.message}")
        }

        results
    }

    // ═══════════════════════════════════════════════════
    //  工具方法
    // ═══════════════════════════════════════════════════

    private fun isServiceRunning(serviceName: String): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        return am.getRunningServices(Integer.MAX_VALUE).any {
            it.service.className == serviceName
        }
    }

    private fun isMiuiOrHyperOs(): Boolean {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val get = clazz.getMethod("get", String::class.java, String::class.java)
            val miui = get.invoke(null, "ro.miui.ui.version.name", "") as String
            val hyper = get.invoke(null, "ro.mi.os.version.name", "") as String
            miui.isNotEmpty() || hyper.isNotEmpty()
        } catch (e: Exception) {
            Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true) ||
            Build.MANUFACTURER.equals("Redmi", ignoreCase = true)
        }
    }

    private fun getSystemVersion(): String {
        val miuiVer = try {
            val clazz = Class.forName("android.os.SystemProperties")
            val get = clazz.getMethod("get", String::class.java, String::class.java)
            get.invoke(null, "ro.miui.ui.version.name", "") as String
        } catch (e: Exception) { "" }

        val hyperVer = try {
            val clazz = Class.forName("android.os.SystemProperties")
            val get = clazz.getMethod("get", String::class.java, String::class.java)
            get.invoke(null, "ro.mi.os.version.name", "") as String
        } catch (e: Exception) { "" }

        return buildString {
            append(Build.MANUFACTURER).append(" ").append(Build.MODEL)
            if (hyperVer.isNotEmpty()) append(" | HyperOS $hyperVer")
            else if (miuiVer.isNotEmpty()) append(" | MIUI $miuiVer")
            append(" | Android ${Build.VERSION.RELEASE}")
        }
    }

    private fun formatTime(ts: Long): String {
        if (ts <= 0) return "N/A"
        return java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date(ts))
    }
}
