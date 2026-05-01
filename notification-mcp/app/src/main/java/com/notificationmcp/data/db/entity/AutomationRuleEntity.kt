package com.notificationmcp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automation_rules")
data class AutomationRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    // ── Trigger: app source ──
    @ColumnInfo(name = "trigger_package")
    val triggerPackage: String? = null,

    @ColumnInfo(name = "selected_apps")
    val selectedApps: String? = null, // JSON array of package names

    // ── Trigger: content matching ──
    @ColumnInfo(name = "trigger_content_regex")
    val triggerContentRegex: String? = null,

    @ColumnInfo(name = "trigger_category")
    val triggerCategory: String? = null,

    @ColumnInfo(name = "trigger_priority_min")
    val triggerPriorityMin: Int? = null,

    @ColumnInfo(name = "trigger_template_type")
    val triggerTemplateType: String? = null,
    // verification_code | amount | messaging | logistics | keyword

    // ── Trigger: time & DND ──
    @ColumnInfo(name = "trigger_time_mode")
    val triggerTimeMode: String = "always",
    // always | daytime | nighttime | custom

    @ColumnInfo(name = "trigger_time_range")
    val triggerTimeRange: String? = null,
    // JSON {"start":"08:00","end":"22:00"} for custom mode

    @ColumnInfo(name = "trigger_dnd_override")
    val triggerDndOverride: Boolean = false,

    // ── Action ──
    @ColumnInfo(name = "action_type")
    val actionType: String, // clipboard, webhook, sound, notification, silent_remove

    @ColumnInfo(name = "action_config")
    val actionConfig: String, // JSON config for the action

    @ColumnInfo(name = "action_auto_copy")
    val actionAutoCopy: Boolean = false,

    @ColumnInfo(name = "action_force_sound")
    val actionForceSound: Boolean = false,

    @ColumnInfo(name = "action_pin_in_summary")
    val actionPinInSummary: Boolean = false,

    @ColumnInfo(name = "action_silent_remove")
    val actionSilentRemove: Boolean = false,

    // ── Meta ──
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "match_count")
    val matchCount: Int = 0
)
