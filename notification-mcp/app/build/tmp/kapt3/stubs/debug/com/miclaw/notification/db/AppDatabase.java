package com.miclaw.notification.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \t2\u00020\u0001:\u0001\tB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&\u00a8\u0006\n"}, d2 = {"Lcom/miclaw/notification/db/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "filterRuleDao", "Lcom/miclaw/notification/db/FilterRuleDao;", "maskingRuleDao", "Lcom/miclaw/notification/db/MaskingRuleDao;", "notificationDao", "Lcom/miclaw/notification/db/NotificationDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.miclaw.notification.model.NotificationData.class, com.miclaw.notification.model.FilterRule.class, com.miclaw.notification.model.MaskingRule.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.miclaw.notification.db.AppDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.miclaw.notification.db.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.miclaw.notification.db.NotificationDao notificationDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.miclaw.notification.db.FilterRuleDao filterRuleDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.miclaw.notification.db.MaskingRuleDao maskingRuleDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/miclaw/notification/db/AppDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/miclaw/notification/db/AppDatabase;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.miclaw.notification.db.AppDatabase getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}