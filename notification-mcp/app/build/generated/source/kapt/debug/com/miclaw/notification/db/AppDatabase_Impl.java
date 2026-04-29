package com.miclaw.notification.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile NotificationDao _notificationDao;

  private volatile FilterRuleDao _filterRuleDao;

  private volatile MaskingRuleDao _maskingRuleDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`dbId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `notificationId` INTEGER NOT NULL, `channelId` TEXT, `fingerprint` TEXT NOT NULL, `postTime` INTEGER NOT NULL, `receiveTime` INTEGER NOT NULL, `packageName` TEXT NOT NULL, `appName` TEXT NOT NULL, `appVersion` TEXT NOT NULL, `uid` INTEGER NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `subText` TEXT, `bigText` TEXT, `largeIcon` TEXT, `picture` TEXT, `priority` INTEGER NOT NULL, `isOngoing` INTEGER NOT NULL, `isClearable` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, `category` TEXT, `isGroupSummary` INTEGER NOT NULL, `actions` TEXT NOT NULL, `extras` TEXT NOT NULL, `forwarded` INTEGER NOT NULL, `forwardTime` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `filter_rules` (`id` TEXT NOT NULL, `packageName` TEXT, `appName` TEXT, `keywordPattern` TEXT, `priority` TEXT, `category` TEXT, `enabled` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `masking_rules` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `pattern` TEXT NOT NULL, `replacement` TEXT NOT NULL, `enabled` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e155dca9967c84c6ce6b888b16f1997a')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `notifications`");
        db.execSQL("DROP TABLE IF EXISTS `filter_rules`");
        db.execSQL("DROP TABLE IF EXISTS `masking_rules`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsNotifications = new HashMap<String, TableInfo.Column>(26);
        _columnsNotifications.put("dbId", new TableInfo.Column("dbId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("notificationId", new TableInfo.Column("notificationId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("channelId", new TableInfo.Column("channelId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("fingerprint", new TableInfo.Column("fingerprint", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("postTime", new TableInfo.Column("postTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("receiveTime", new TableInfo.Column("receiveTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("appName", new TableInfo.Column("appName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("appVersion", new TableInfo.Column("appVersion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("uid", new TableInfo.Column("uid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("subText", new TableInfo.Column("subText", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("bigText", new TableInfo.Column("bigText", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("largeIcon", new TableInfo.Column("largeIcon", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("picture", new TableInfo.Column("picture", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("isOngoing", new TableInfo.Column("isOngoing", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("isClearable", new TableInfo.Column("isClearable", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("isRead", new TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("isGroupSummary", new TableInfo.Column("isGroupSummary", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("actions", new TableInfo.Column("actions", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("extras", new TableInfo.Column("extras", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("forwarded", new TableInfo.Column("forwarded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("forwardTime", new TableInfo.Column("forwardTime", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotifications = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotifications = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotifications = new TableInfo("notifications", _columnsNotifications, _foreignKeysNotifications, _indicesNotifications);
        final TableInfo _existingNotifications = TableInfo.read(db, "notifications");
        if (!_infoNotifications.equals(_existingNotifications)) {
          return new RoomOpenHelper.ValidationResult(false, "notifications(com.miclaw.notification.model.NotificationData).\n"
                  + " Expected:\n" + _infoNotifications + "\n"
                  + " Found:\n" + _existingNotifications);
        }
        final HashMap<String, TableInfo.Column> _columnsFilterRules = new HashMap<String, TableInfo.Column>(8);
        _columnsFilterRules.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilterRules.put("packageName", new TableInfo.Column("packageName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilterRules.put("appName", new TableInfo.Column("appName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilterRules.put("keywordPattern", new TableInfo.Column("keywordPattern", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilterRules.put("priority", new TableInfo.Column("priority", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilterRules.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilterRules.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilterRules.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFilterRules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFilterRules = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFilterRules = new TableInfo("filter_rules", _columnsFilterRules, _foreignKeysFilterRules, _indicesFilterRules);
        final TableInfo _existingFilterRules = TableInfo.read(db, "filter_rules");
        if (!_infoFilterRules.equals(_existingFilterRules)) {
          return new RoomOpenHelper.ValidationResult(false, "filter_rules(com.miclaw.notification.model.FilterRule).\n"
                  + " Expected:\n" + _infoFilterRules + "\n"
                  + " Found:\n" + _existingFilterRules);
        }
        final HashMap<String, TableInfo.Column> _columnsMaskingRules = new HashMap<String, TableInfo.Column>(5);
        _columnsMaskingRules.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaskingRules.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaskingRules.put("pattern", new TableInfo.Column("pattern", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaskingRules.put("replacement", new TableInfo.Column("replacement", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaskingRules.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMaskingRules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMaskingRules = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMaskingRules = new TableInfo("masking_rules", _columnsMaskingRules, _foreignKeysMaskingRules, _indicesMaskingRules);
        final TableInfo _existingMaskingRules = TableInfo.read(db, "masking_rules");
        if (!_infoMaskingRules.equals(_existingMaskingRules)) {
          return new RoomOpenHelper.ValidationResult(false, "masking_rules(com.miclaw.notification.model.MaskingRule).\n"
                  + " Expected:\n" + _infoMaskingRules + "\n"
                  + " Found:\n" + _existingMaskingRules);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e155dca9967c84c6ce6b888b16f1997a", "0229efcb2f0f96fdee5f79cfdd703732");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "notifications","filter_rules","masking_rules");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `notifications`");
      _db.execSQL("DELETE FROM `filter_rules`");
      _db.execSQL("DELETE FROM `masking_rules`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(NotificationDao.class, NotificationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FilterRuleDao.class, FilterRuleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MaskingRuleDao.class, MaskingRuleDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public NotificationDao notificationDao() {
    if (_notificationDao != null) {
      return _notificationDao;
    } else {
      synchronized(this) {
        if(_notificationDao == null) {
          _notificationDao = new NotificationDao_Impl(this);
        }
        return _notificationDao;
      }
    }
  }

  @Override
  public FilterRuleDao filterRuleDao() {
    if (_filterRuleDao != null) {
      return _filterRuleDao;
    } else {
      synchronized(this) {
        if(_filterRuleDao == null) {
          _filterRuleDao = new FilterRuleDao_Impl(this);
        }
        return _filterRuleDao;
      }
    }
  }

  @Override
  public MaskingRuleDao maskingRuleDao() {
    if (_maskingRuleDao != null) {
      return _maskingRuleDao;
    } else {
      synchronized(this) {
        if(_maskingRuleDao == null) {
          _maskingRuleDao = new MaskingRuleDao_Impl(this);
        }
        return _maskingRuleDao;
      }
    }
  }
}
