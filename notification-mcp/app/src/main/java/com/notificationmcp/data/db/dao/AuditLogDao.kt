package com.notificationmcp.data.db.dao

import androidx.room.*
import com.notificationmcp.data.db.entity.AuditLogEntity

@Dao
interface AuditLogDao {

    @Insert
    suspend fun insert(log: AuditLogEntity): Long

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 100): List<AuditLogEntity>

    @Query("DELETE FROM audit_logs WHERE timestamp < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)
}
