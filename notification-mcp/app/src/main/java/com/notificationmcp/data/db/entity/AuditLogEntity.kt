package com.notificationmcp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audit_logs",
    indices = [Index(value = ["timestamp"])]
)
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "tool_name")
    val toolName: String,

    @ColumnInfo(name = "parameters_summary")
    val parametersSummary: String,

    @ColumnInfo(name = "client_id")
    val clientId: String? = null,

    @ColumnInfo(name = "result_count")
    val resultCount: Int = 0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
