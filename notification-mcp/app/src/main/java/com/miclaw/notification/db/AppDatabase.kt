package com.miclaw.notification.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.miclaw.notification.model.FilterRule
import com.miclaw.notification.model.MaskingRule
import com.miclaw.notification.model.NotificationData

@Database(
    entities = [
        NotificationData::class,
        FilterRule::class,
        MaskingRule::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
    abstract fun filterRuleDao(): FilterRuleDao
    abstract fun maskingRuleDao(): MaskingRuleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notification_mcp.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
