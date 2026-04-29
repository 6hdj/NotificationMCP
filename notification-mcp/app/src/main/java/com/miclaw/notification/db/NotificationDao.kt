package com.miclaw.notification.db

import androidx.room.*
import com.miclaw.notification.model.NotificationData

/**
 * 通知数据 DAO — 高频读写优化
 */
@Dao
interface NotificationDao {

    // ═══════════════════════════════════════
    //  写入
    // ═══════════════════════════════════════

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: NotificationData): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(notifications: List<NotificationData>): List<Long>

    // ═══════════════════════════════════════
    //  查询
    // ═══════════════════════════════════════

    /** 按指纹查重 */
    @Query("SELECT * FROM notifications WHERE fingerprint = :fingerprint LIMIT 1")
    suspend fun findByFingerprint(fingerprint: String): NotificationData?

    /** 获取未转发的通知（按时间排序） */
    @Query("SELECT * FROM notifications WHERE forwarded = 0 ORDER BY postTime ASC LIMIT :limit")
    suspend fun getUnforwarded(limit: Int = 50): List<NotificationData>

    /** 按包名查询 */
    @Query("SELECT * FROM notifications WHERE packageName = :pkg ORDER BY postTime DESC LIMIT :limit")
    suspend fun getByPackage(pkg: String, limit: Int = 50): List<NotificationData>

        /** 按关键词搜索标题和内容 */
    @Query("""
        SELECT * FROM notifications 
        WHERE title LIKE '%' || :keyword || '%' 
           OR content LIKE '%' || :keyword || '%'
        ORDER BY postTime DESC 
        LIMIT :limit
    """)
    suspend fun searchByKeyword(keyword: String, limit: Int = 50): List<NotificationData>

    /** 按关键词搜索标题和内容（限定时间范围，避免全表扫描） */
    @Query("""
        SELECT * FROM notifications 
        WHERE postTime >= :startTime
          AND (title LIKE '%' || :keyword || '%' 
               OR content LIKE '%' || :keyword || '%')
        ORDER BY postTime DESC 
        LIMIT :limit
    """)
    suspend fun searchByKeywordInTimeRange(keyword: String, startTime: Long, limit: Int = 20): List<NotificationData>

    /** 获取最近 N 条通知 */
    @Query("SELECT * FROM notifications ORDER BY postTime DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 100): List<NotificationData>

    /** 获取指定时间范围内的通知 */
    @Query("SELECT * FROM notifications WHERE postTime BETWEEN :startTime AND :endTime ORDER BY postTime DESC")
    suspend fun getByTimeRange(startTime: Long, endTime: Long): List<NotificationData>

    /** 获取今日通知统计 */
    @Query("SELECT COUNT(*) FROM notifications WHERE postTime >= :dayStart")
    suspend fun getTodayCount(dayStart: Long): Int

    /** 获取今日已转发数 */
    @Query("SELECT COUNT(*) FROM notifications WHERE postTime >= :dayStart AND forwarded = 1")
    suspend fun getTodayForwardedCount(dayStart: Long): Int

    /** 获取今日过滤数 */
    @Query("SELECT COUNT(*) FROM notifications WHERE postTime >= :dayStart AND forwarded = 0 AND isRead = 1")
    suspend fun getTodayFilteredCount(dayStart: Long): Int

    /** 获取未转发通知数量 */
    @Query("SELECT COUNT(*) FROM notifications WHERE forwarded = 0")
    suspend fun getUnforwardedCount(): Int

    /** 获取数据库总条数 */
    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun getTotalCount(): Long

    // ═══════════════════════════════════════
    //  更新
    // ═══════════════════════════════════════

    /** 标记已转发 */
    @Query("UPDATE notifications SET forwarded = 1, forwardTime = :forwardTime WHERE dbId = :dbId")
    suspend fun markForwarded(dbId: Long, forwardTime: Long)

    /** 批量标记已转发 */
    @Query("UPDATE notifications SET forwarded = 1, forwardTime = :forwardTime WHERE dbId IN (:dbIds)")
    suspend fun markBatchForwarded(dbIds: List<Long>, forwardTime: Long)

    // ═══════════════════════════════════════
    //  清理
    // ═══════════════════════════════════════

    /** 删除已转发且超过 N 天的通知 */
    @Query("DELETE FROM notifications WHERE forwarded = 1 AND postTime < :cutoffTime")
    suspend fun cleanupOldForwarded(cutoffTime: Long): Int

    /** 删除所有已转发通知 */
    @Query("DELETE FROM notifications WHERE forwarded = 1")
    suspend fun cleanupAllForwarded(): Int

    /** 清空全部数据 */
    @Query("DELETE FROM notifications")
    suspend fun deleteAll()
}
