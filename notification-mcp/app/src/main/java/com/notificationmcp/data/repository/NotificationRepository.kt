package com.notificationmcp.data.repository

import com.notificationmcp.data.db.dao.AuditLogDao
import com.notificationmcp.data.db.dao.AutomationRuleDao
import com.notificationmcp.data.db.dao.HourCount
import com.notificationmcp.data.db.dao.NotificationDao
import com.notificationmcp.data.db.entity.AuditLogEntity
import com.notificationmcp.data.db.entity.AutomationRuleEntity
import com.notificationmcp.data.db.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
    private val automationRuleDao: AutomationRuleDao,
    private val auditLogDao: AuditLogDao
) {
    // Notification operations
        suspend fun insertNotification(notification: NotificationEntity): Long =
        notificationDao.insert(notification)

    suspend fun updateNotification(notification: NotificationEntity) =
        notificationDao.update(notification)

    suspend fun findExistingByNotifId(packageName: String, notificationId: Int): Long? =
        notificationDao.findExistingByNotifId(packageName, notificationId)

    suspend fun getRecentNotifications(limit: Int = 20): List<NotificationEntity> =
        notificationDao.getRecent(limit)

    fun getRecentNotificationsFlow(limit: Int = 100): Flow<List<NotificationEntity>> =
        notificationDao.getRecentFlow(limit)

    suspend fun searchNotifications(
        packageName: String? = null,
        contentKeyword: String? = null,
        startTime: Long? = null,
        endTime: Long? = null,
        limit: Int = 100
    ): List<NotificationEntity> =
        notificationDao.search(packageName, contentKeyword, startTime, endTime, limit)

    suspend fun getTotalCount(): Int = notificationDao.getTotalCount()

    suspend fun getTodayCount(): Int {
        val now = System.currentTimeMillis()
        val startOfDay = now - (now % (24 * 60 * 60 * 1000))
        return notificationDao.getTodayCount(startOfDay, now)
    }

    suspend fun getTopApps(limit: Int = 10) = notificationDao.getTopApps(limit)

    suspend fun getFilteredCount(): Int = notificationDao.getFilteredCount()

    suspend fun getLatestVerificationCode(appKeyword: String? = null): NotificationEntity? =
        notificationDao.getLatestVerificationCodeByApp(appKeyword)

    suspend fun getByApp(appKeyword: String, limit: Int = 50): List<NotificationEntity> =
        notificationDao.getByApp(appKeyword, limit)

    suspend fun getSince(startTime: Long): List<NotificationEntity> =
        notificationDao.getSince(startTime)

    suspend fun getHourlyDistribution(startTime: Long): List<HourCount> =
        notificationDao.getHourlyDistribution(startTime)

    suspend fun getAllForExport(limit: Int = 5000): List<NotificationEntity> =
        notificationDao.getAllForExport(limit)

    suspend fun getById(id: Long): NotificationEntity? = notificationDao.getById(id)

    suspend fun softDelete(id: Long) = notificationDao.softDelete(id)

    suspend fun softDeleteAll() = notificationDao.softDeleteAll()

    suspend fun deleteExpiredVerificationCodes(deadlineMs: Long) =
        notificationDao.deleteExpiredVerificationCodes(deadlineMs)

    suspend fun cleanupOld(cutoffMs: Long) = notificationDao.cleanupOld(cutoffMs)

    suspend fun deleteOldest(count: Int) = notificationDao.deleteOldest(count)

    // Automation rules
    suspend fun insertRule(rule: AutomationRuleEntity): Long = automationRuleDao.insert(rule)

    suspend fun updateRule(rule: AutomationRuleEntity) = automationRuleDao.update(rule)

    suspend fun deleteRule(id: Long) = automationRuleDao.deleteById(id)

    fun getAllRulesFlow(): Flow<List<AutomationRuleEntity>> = automationRuleDao.getAllFlow()

    suspend fun getEnabledRules(): List<AutomationRuleEntity> = automationRuleDao.getEnabledRules()

    suspend fun getAllRules(): List<AutomationRuleEntity> = automationRuleDao.getAll()

    suspend fun getRuleById(id: Long): AutomationRuleEntity? = automationRuleDao.getById(id)

    suspend fun incrementRuleMatchCount(id: Long) = automationRuleDao.incrementMatchCount(id)

    // Audit logs
    suspend fun insertAuditLog(log: AuditLogEntity): Long = auditLogDao.insert(log)

    suspend fun getAuditLogs(limit: Int = 100): List<AuditLogEntity> = auditLogDao.getRecent(limit)

    suspend fun cleanupAuditLogs(olderThanMs: Long) = auditLogDao.deleteOlderThan(olderThanMs)
}
