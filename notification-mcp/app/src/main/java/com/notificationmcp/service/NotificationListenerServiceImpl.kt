package com.notificationmcp.service

import android.app.Notification
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import com.notificationmcp.data.db.entity.NotificationEntity
import com.notificationmcp.data.repository.NotificationRepository
import com.notificationmcp.mcp.MCPServer
import com.notificationmcp.privacy.ClassificationEngine
import com.notificationmcp.privacy.PrivacyEngine
import com.notificationmcp.ui.theme.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class NotificationListenerServiceImpl : NotificationListenerService() {

    @Inject lateinit var repository: NotificationRepository
    @Inject lateinit var mcpServer: MCPServer
    @Inject lateinit var privacyEngine: PrivacyEngine
    @Inject lateinit var classificationEngine: ClassificationEngine

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val TAG = "NotificationListener"

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        scope.launch {
            try {
                processNotification(sbn)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing notification", e)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Optional: track removals
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

        private suspend fun processNotification(sbn: StatusBarNotification) {
        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        // 通知过滤：如果启用了过滤，跳过低优先级通知
        if (PreferencesManager.isFilterEnabled()) {
            if (notification.priority < Notification.PRIORITY_DEFAULT) {
                Log.d(TAG, "Filtered low priority notification from ${sbn.packageName}")
                return
            }
        }

        var title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        var content = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
        val infoText = extras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString()

        // 隐私脱敏：如果启用了脱敏，对标题和内容进行脱敏处理
        if (PreferencesManager.isAutoRedactEnabled()) {
            val level = PreferencesManager.getRedactLevel()
            title = title?.let { privacyEngine.redact(it, level) }
            content = content?.let { privacyEngine.redact(it, level) }
        }

        val packageName = sbn.packageName
        val appName = try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }

                // Extract conversation messages (MessagingStyle)
        var conversationTitle: String? = null
        var conversationMessages: String? = null
        try {
            conversationTitle = extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
            val messagesArray = extras.getParcelableArray(Notification.EXTRA_MESSAGES)
            if (messagesArray != null && messagesArray.isNotEmpty()) {
                val messages = mutableListOf<String>()
                for (item in messagesArray) {
                    if (item is android.os.Bundle) {
                        val sender = item.getCharSequence("sender_person")?.toString()
                            ?: item.getString("sender_name") ?: "Unknown"
                        val text = item.getCharSequence("text")?.toString() ?: ""
                        if (text.isNotEmpty()) {
                            messages.add("$sender: $text")
                        }
                    }
                }
                if (messages.isNotEmpty()) {
                    conversationMessages = messages.joinToString("\n")
                }
            }
        } catch (_: Exception) {
            // Not a MessagingStyle notification
        }

        // Detect verification codes
        val isVerificationCode = privacyEngine.isVerificationCode(title, content)
        val verificationCode = if (isVerificationCode) {
            privacyEngine.extractVerificationCode(title) ?: privacyEngine.extractVerificationCode(content)
        } else null

        // Classify
        val tempEntity = NotificationEntity(
            notificationId = sbn.id,
            packageName = packageName,
            appName = appName,
            title = title,
            content = content,
            timestamp = sbn.postTime,
            isVerificationCode = isVerificationCode,
            verificationCode = verificationCode
        )
        val classification = classificationEngine.classify(tempEntity)

        val entity = NotificationEntity(
            notificationId = sbn.id,
            packageName = packageName,
            appName = appName,
            title = title,
            content = content,
            bigText = bigText,
            subText = subText,
            infoText = infoText,
            channelId = notification.channelId,
            category = classification.category,
            priority = notification.priority,
            isClearable = sbn.isClearable,
            isGroupSummary = sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0,
            groupKey = sbn.groupKey,
            conversationTitle = conversationTitle,
            conversationMessages = conversationMessages,
            timestamp = sbn.postTime,
            isVerificationCode = isVerificationCode,
            verificationCode = verificationCode,
            classification = classification.category,
            classificationConfidence = classification.confidence,
            isUrgent = classification.is_urgent
        )

                // For persistent (non-clearable) notifications, update existing record instead of inserting new one
        val id: Long
        if (!sbn.isClearable) {
            val existingId = repository.findExistingByNotifId(packageName, sbn.id)
            if (existingId != null) {
                // Update existing persistent notification
                val updatedEntity = entity.copy(id = existingId, timestamp = sbn.postTime)
                repository.updateNotification(updatedEntity)
                id = existingId
                Log.d(TAG, "Updated persistent notification: $packageName/${sbn.id} -> id=$existingId")
            } else {
                id = repository.insertNotification(entity)
            }
        } else {
            id = repository.insertNotification(entity)
        }
        val savedEntity = entity.copy(id = id)

        // Broadcast to MCP clients
        mcpServer.broadcastNotification(savedEntity)

        // Check automation rules
        checkAutomationRules(savedEntity)

        // Cleanup old notifications
        cleanupIfNeeded()
    }

    private suspend fun checkAutomationRules(notification: NotificationEntity) {
        val rules = repository.getEnabledRules()
        for (rule in rules) {
            var matches = true

            rule.triggerPackage?.let { pkg ->
                if (!notification.packageName.contains(pkg, ignoreCase = true)) {
                    matches = false
                }
            }

            rule.triggerContentRegex?.let { regex ->
                val text = "${notification.title.orEmpty()} ${notification.content.orEmpty()}"
                if (!Regex(regex, RegexOption.IGNORE_CASE).containsMatchIn(text)) {
                    matches = false
                }
            }

            rule.triggerCategory?.let { cat ->
                if (!notification.category.equals(cat, ignoreCase = true)) {
                    matches = false
                }
            }

            if (matches) {
                repository.incrementRuleMatchCount(rule.id)
                executeAutomationAction(rule.actionType, rule.actionConfig, notification)
            }
        }
    }

        private suspend fun executeAutomationAction(actionType: String, actionConfig: String, notification: NotificationEntity) {
        when (actionType) {
                        "clipboard" -> {
                // Only copy when a verification code was actually extracted.
                // Never fallback to copying the full notification content.
                val code = notification.verificationCode
                if (code.isNullOrBlank()) {
                    Log.d(TAG, "Automation: clipboard rule matched but no verification code found, skipping")
                    return
                }
                withContext(Dispatchers.Main) {
                    try {
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("验证码", code)
                        clipboard.setPrimaryClip(clip)
                        Log.i(TAG, "Automation: copied verification code to clipboard - $code")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to copy to clipboard", e)
                    }
                }
            }
                        "webhook" -> {
                if (actionConfig.isNotBlank() && actionConfig != "{}") {
                    try {
                        val url = try {
                            val json = kotlinx.serialization.json.Json.parseToJsonElement(actionConfig) as? kotlinx.serialization.json.JsonObject
                            json?.get("url")?.toString()?.removeSurrounding("\"")
                        } catch (_: Exception) { null }
                        if (!url.isNullOrBlank()) {
                            val payload = """{"title":"${notification.title ?: ""}","content":"${notification.content ?: ""}","app":"${notification.appName}","timestamp":${notification.timestamp}}"""
                            val conn = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                            conn.requestMethod = "POST"
                            conn.setRequestProperty("Content-Type", "application/json")
                            conn.doOutput = true
                            conn.connectTimeout = 10000
                            conn.readTimeout = 10000
                            conn.outputStream.write(payload.toByteArray())
                            conn.outputStream.flush()
                            val code = conn.responseCode
                            Log.i(TAG, "Webhook response: $code")
                            conn.disconnect()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Webhook error", e)
                    }
                }
            }
            "sound" -> {
                withContext(Dispatchers.Main) {
                    try {
                        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val ringtone = RingtoneManager.getRingtone(applicationContext, soundUri)
                        ringtone?.play()
                        Log.i(TAG, "Automation: played notification sound")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to play sound", e)
                    }
                }
            }
            "notification" -> {
                Log.i(TAG, "Automation: custom notification for ${notification.appName}")
            }
        }
    }

    private var lastCleanup = 0L
    private suspend fun cleanupIfNeeded() {
        val now = System.currentTimeMillis()
        if (now - lastCleanup < 60 * 60 * 1000) return // Cleanup at most once per hour
        lastCleanup = now

        val sevenDaysAgo = now - 7 * 24 * 60 * 60 * 1000L
        repository.cleanupOld(sevenDaysAgo)

        // Delete expired verification codes (10 minutes)
        val expiryDeadline = now - 10 * 60 * 1000L
        repository.deleteExpiredVerificationCodes(expiryDeadline)

        // Enforce max 5000 limit
        val total = repository.getTotalCount()
        if (total > 5000) {
            repository.deleteOldest(total - 5000)
        }
    }
}
