package com.notificationmcp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["package_name"]),
        Index(value = ["category"]),
        Index(value = ["is_verification_code"])
    ]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "notification_id")
    val notificationId: Int,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "app_name")
    val appName: String,

    @ColumnInfo(name = "title")
    val title: String? = null,

    @ColumnInfo(name = "content")
    val content: String? = null,

    @ColumnInfo(name = "big_text")
    val bigText: String? = null,

    @ColumnInfo(name = "sub_text")
    val subText: String? = null,

    @ColumnInfo(name = "info_text")
    val infoText: String? = null,

    @ColumnInfo(name = "channel_id")
    val channelId: String? = null,

    @ColumnInfo(name = "category")
    val category: String? = null,

    @ColumnInfo(name = "priority")
    val priority: Int = 0,

    @ColumnInfo(name = "is_clearable")
    val isClearable: Boolean = true,

    @ColumnInfo(name = "is_group_summary")
    val isGroupSummary: Boolean = false,

    @ColumnInfo(name = "group_key")
    val groupKey: String? = null,

    @ColumnInfo(name = "conversation_title")
    val conversationTitle: String? = null,

    @ColumnInfo(name = "conversation_messages")
    val conversationMessages: String? = null,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "is_verification_code")
    val isVerificationCode: Boolean = false,

    @ColumnInfo(name = "verification_code")
    val verificationCode: String? = null,

    @ColumnInfo(name = "classification")
    val classification: String? = null,

    @ColumnInfo(name = "classification_confidence")
    val classificationConfidence: Float = 0f,

    @ColumnInfo(name = "is_urgent")
    val isUrgent: Boolean = false,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
