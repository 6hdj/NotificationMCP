package com.notificationmcp.data.db.dao

import androidx.room.*
import com.notificationmcp.data.db.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Update
    suspend fun update(notification: NotificationEntity)

    @Query("UPDATE notifications SET is_deleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("UPDATE notifications SET is_deleted = 1")
    suspend fun softDeleteAll()

    @Query("DELETE FROM notifications WHERE is_verification_code = 1 AND timestamp < :deadline")
    suspend fun deleteExpiredVerificationCodes(deadline: Long)

    @Query("DELETE FROM notifications WHERE timestamp < :cutoff OR id IN (SELECT id FROM notifications WHERE is_deleted = 1 ORDER BY timestamp ASC LIMIT :limit)")
    suspend fun cleanupOld(cutoff: Long, limit: Int = 0)

    @Query("DELETE FROM notifications WHERE id IN (SELECT id FROM notifications ORDER BY timestamp ASC LIMIT :count)")
    suspend fun deleteOldest(count: Int)

    @Query("SELECT * FROM notifications WHERE is_deleted = 0 ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 20): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE is_deleted = 0 ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentFlow(limit: Int = 100): Flow<List<NotificationEntity>>

        @Query("""
        SELECT * FROM notifications 
        WHERE is_deleted = 0
        AND (:packageName IS NULL OR package_name LIKE '%' || :packageName || '%')
        AND (:contentKeyword IS NULL OR content LIKE '%' || :contentKeyword || '%' OR title LIKE '%' || :contentKeyword || '%' OR app_name LIKE '%' || :contentKeyword || '%')
        AND (:startTime IS NULL OR timestamp >= :startTime)
        AND (:endTime IS NULL OR timestamp <= :endTime)
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun search(
        packageName: String? = null,
        contentKeyword: String? = null,
        startTime: Long? = null,
        endTime: Long? = null,
        limit: Int = 100
    ): List<NotificationEntity>

    @Query("SELECT COUNT(*) FROM notifications WHERE is_deleted = 0")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM notifications WHERE is_deleted = 0 AND timestamp >= :dayStart AND timestamp <= :dayEnd")
    suspend fun getTodayCount(dayStart: Long, dayEnd: Long): Int

    @Query("SELECT package_name, COUNT(*) as cnt FROM notifications WHERE is_deleted = 0 GROUP BY package_name ORDER BY cnt DESC LIMIT :limit")
    suspend fun getTopApps(limit: Int = 10): List<AppCount>

    @Query("SELECT COUNT(*) FROM notifications WHERE is_deleted = 0 AND classification IS NOT NULL AND classification != 'unknown'")
    suspend fun getFilteredCount(): Int

    @Query("SELECT * FROM notifications WHERE is_verification_code = 1 AND is_deleted = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestVerificationCode(): NotificationEntity?

    @Query("SELECT * FROM notifications WHERE is_verification_code = 1 AND is_deleted = 0 AND (:appKeyword IS NULL OR package_name LIKE '%' || :appKeyword || '%') ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestVerificationCodeByApp(appKeyword: String? = null): NotificationEntity?

    @Query("""
        SELECT * FROM notifications 
        WHERE is_deleted = 0 
        AND package_name LIKE '%' || :appKeyword || '%'
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getByApp(appKeyword: String, limit: Int = 50): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE is_deleted = 0 AND timestamp >= :startTime ORDER BY timestamp DESC")
    suspend fun getSince(startTime: Long): List<NotificationEntity>

    @Query("""
        SELECT strftime('%H', timestamp / 1000, 'unixepoch', 'localtime') as hour, COUNT(*) as count 
        FROM notifications WHERE is_deleted = 0 AND timestamp >= :startTime
        GROUP BY hour ORDER BY hour
    """)
    suspend fun getHourlyDistribution(startTime: Long): List<HourCount>

    @Query("SELECT * FROM notifications WHERE is_deleted = 0 ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getAllForExport(limit: Int = 5000): List<NotificationEntity>

        @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getById(id: Long): NotificationEntity?

    @Query("SELECT id FROM notifications WHERE notification_id = :notifId AND package_name = :packageName AND is_deleted = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun findExistingByNotifId(packageName: String, notifId: Int): Long?
}

data class AppCount(
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "cnt") val count: Int
)

data class HourCount(
    @ColumnInfo(name = "hour") val hour: String,
    @ColumnInfo(name = "count") val count: Int
)
