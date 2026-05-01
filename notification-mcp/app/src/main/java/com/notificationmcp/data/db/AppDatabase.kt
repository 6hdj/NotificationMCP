package com.notificationmcp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.notificationmcp.data.db.dao.AuditLogDao
import com.notificationmcp.data.db.dao.AutomationRuleDao
import com.notificationmcp.data.db.dao.NotificationDao
import com.notificationmcp.data.db.entity.AuditLogEntity
import com.notificationmcp.data.db.entity.AutomationRuleEntity
import com.notificationmcp.data.db.entity.NotificationEntity

@Database(
    entities = [
        NotificationEntity::class,
        AutomationRuleEntity::class,
        AuditLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun automationRuleDao(): AutomationRuleDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new columns to automation_rules for the new UX
                db.execSQL("ALTER TABLE automation_rules ADD COLUMN selected_apps TEXT")
                db.execSQL("ALTER TABLE automation_rules ADD COLUMN trigger_template_type TEXT")
                db.execSQL("ALTER TABLE automation_rules ADD COLUMN trigger_time_mode TEXT NOT NULL DEFAULT 'always'")
                db.execSQL("ALTER TABLE automation_rules ADD COLUMN trigger_time_range TEXT")
                db.execSQL("ALTER TABLE automation_rules ADD COLUMN trigger_dnd_override INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE automation_rules ADD COLUMN action_auto_copy INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE automation_rules ADD COLUMN action_force_sound INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE automation_rules ADD COLUMN action_pin_in_summary INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE automation_rules ADD COLUMN action_silent_remove INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
