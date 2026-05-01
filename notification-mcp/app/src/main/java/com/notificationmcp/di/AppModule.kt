package com.notificationmcp.di

import android.content.Context
import androidx.room.Room
import com.notificationmcp.data.db.AppDatabase
import com.notificationmcp.data.db.dao.AuditLogDao
import com.notificationmcp.data.db.dao.AutomationRuleDao
import com.notificationmcp.data.db.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "notification_mcp.db"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()

    @Provides
    fun provideAutomationRuleDao(db: AppDatabase): AutomationRuleDao = db.automationRuleDao()

    @Provides
    fun provideAuditLogDao(db: AppDatabase): AuditLogDao = db.auditLogDao()
}
