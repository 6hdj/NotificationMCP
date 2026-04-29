package com.miclaw.notification.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.miclaw.notification.model.NotificationData;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class NotificationDao_Impl implements NotificationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NotificationData> __insertionAdapterOfNotificationData;

  private final SharedSQLiteStatement __preparedStmtOfMarkForwarded;

  private final SharedSQLiteStatement __preparedStmtOfCleanupOldForwarded;

  private final SharedSQLiteStatement __preparedStmtOfCleanupAllForwarded;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public NotificationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNotificationData = new EntityInsertionAdapter<NotificationData>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `notifications` (`dbId`,`notificationId`,`channelId`,`fingerprint`,`postTime`,`receiveTime`,`packageName`,`appName`,`appVersion`,`uid`,`title`,`content`,`subText`,`bigText`,`largeIcon`,`picture`,`priority`,`isOngoing`,`isClearable`,`isRead`,`category`,`isGroupSummary`,`actions`,`extras`,`forwarded`,`forwardTime`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NotificationData entity) {
        statement.bindLong(1, entity.getDbId());
        statement.bindLong(2, entity.getNotificationId());
        if (entity.getChannelId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getChannelId());
        }
        if (entity.getFingerprint() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFingerprint());
        }
        statement.bindLong(5, entity.getPostTime());
        statement.bindLong(6, entity.getReceiveTime());
        if (entity.getPackageName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPackageName());
        }
        if (entity.getAppName() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getAppName());
        }
        if (entity.getAppVersion() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAppVersion());
        }
        statement.bindLong(10, entity.getUid());
        if (entity.getTitle() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getTitle());
        }
        if (entity.getContent() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getContent());
        }
        if (entity.getSubText() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getSubText());
        }
        if (entity.getBigText() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getBigText());
        }
        if (entity.getLargeIcon() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getLargeIcon());
        }
        if (entity.getPicture() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getPicture());
        }
        statement.bindLong(17, entity.getPriority());
        final int _tmp = entity.isOngoing() ? 1 : 0;
        statement.bindLong(18, _tmp);
        final int _tmp_1 = entity.isClearable() ? 1 : 0;
        statement.bindLong(19, _tmp_1);
        final int _tmp_2 = entity.isRead() ? 1 : 0;
        statement.bindLong(20, _tmp_2);
        if (entity.getCategory() == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.getCategory());
        }
        final int _tmp_3 = entity.isGroupSummary() ? 1 : 0;
        statement.bindLong(22, _tmp_3);
        if (entity.getActions() == null) {
          statement.bindNull(23);
        } else {
          statement.bindString(23, entity.getActions());
        }
        if (entity.getExtras() == null) {
          statement.bindNull(24);
        } else {
          statement.bindString(24, entity.getExtras());
        }
        final int _tmp_4 = entity.getForwarded() ? 1 : 0;
        statement.bindLong(25, _tmp_4);
        if (entity.getForwardTime() == null) {
          statement.bindNull(26);
        } else {
          statement.bindLong(26, entity.getForwardTime());
        }
      }
    };
    this.__preparedStmtOfMarkForwarded = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notifications SET forwarded = 1, forwardTime = ? WHERE dbId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfCleanupOldForwarded = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM notifications WHERE forwarded = 1 AND postTime < ?";
        return _query;
      }
    };
    this.__preparedStmtOfCleanupAllForwarded = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM notifications WHERE forwarded = 1";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM notifications";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final NotificationData notification,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfNotificationData.insertAndReturnId(notification);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<NotificationData> notifications,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfNotificationData.insertAndReturnIdsList(notifications);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markForwarded(final long dbId, final long forwardTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkForwarded.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, forwardTime);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, dbId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkForwarded.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object cleanupOldForwarded(final long cutoffTime,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCleanupOldForwarded.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffTime);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfCleanupOldForwarded.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object cleanupAllForwarded(final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCleanupAllForwarded.acquire();
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfCleanupAllForwarded.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object findByFingerprint(final String fingerprint,
      final Continuation<? super NotificationData> $completion) {
    final String _sql = "SELECT * FROM notifications WHERE fingerprint = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (fingerprint == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, fingerprint);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NotificationData>() {
      @Override
      @Nullable
      public NotificationData call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDbId = CursorUtil.getColumnIndexOrThrow(_cursor, "dbId");
          final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "fingerprint");
          final int _cursorIndexOfPostTime = CursorUtil.getColumnIndexOrThrow(_cursor, "postTime");
          final int _cursorIndexOfReceiveTime = CursorUtil.getColumnIndexOrThrow(_cursor, "receiveTime");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfAppVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "appVersion");
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfSubText = CursorUtil.getColumnIndexOrThrow(_cursor, "subText");
          final int _cursorIndexOfBigText = CursorUtil.getColumnIndexOrThrow(_cursor, "bigText");
          final int _cursorIndexOfLargeIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "largeIcon");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsOngoing = CursorUtil.getColumnIndexOrThrow(_cursor, "isOngoing");
          final int _cursorIndexOfIsClearable = CursorUtil.getColumnIndexOrThrow(_cursor, "isClearable");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsGroupSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "isGroupSummary");
          final int _cursorIndexOfActions = CursorUtil.getColumnIndexOrThrow(_cursor, "actions");
          final int _cursorIndexOfExtras = CursorUtil.getColumnIndexOrThrow(_cursor, "extras");
          final int _cursorIndexOfForwarded = CursorUtil.getColumnIndexOrThrow(_cursor, "forwarded");
          final int _cursorIndexOfForwardTime = CursorUtil.getColumnIndexOrThrow(_cursor, "forwardTime");
          final NotificationData _result;
          if (_cursor.moveToFirst()) {
            final long _tmpDbId;
            _tmpDbId = _cursor.getLong(_cursorIndexOfDbId);
            final int _tmpNotificationId;
            _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
            final String _tmpChannelId;
            if (_cursor.isNull(_cursorIndexOfChannelId)) {
              _tmpChannelId = null;
            } else {
              _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            }
            final String _tmpFingerprint;
            if (_cursor.isNull(_cursorIndexOfFingerprint)) {
              _tmpFingerprint = null;
            } else {
              _tmpFingerprint = _cursor.getString(_cursorIndexOfFingerprint);
            }
            final long _tmpPostTime;
            _tmpPostTime = _cursor.getLong(_cursorIndexOfPostTime);
            final long _tmpReceiveTime;
            _tmpReceiveTime = _cursor.getLong(_cursorIndexOfReceiveTime);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final String _tmpAppVersion;
            if (_cursor.isNull(_cursorIndexOfAppVersion)) {
              _tmpAppVersion = null;
            } else {
              _tmpAppVersion = _cursor.getString(_cursorIndexOfAppVersion);
            }
            final int _tmpUid;
            _tmpUid = _cursor.getInt(_cursorIndexOfUid);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpSubText;
            if (_cursor.isNull(_cursorIndexOfSubText)) {
              _tmpSubText = null;
            } else {
              _tmpSubText = _cursor.getString(_cursorIndexOfSubText);
            }
            final String _tmpBigText;
            if (_cursor.isNull(_cursorIndexOfBigText)) {
              _tmpBigText = null;
            } else {
              _tmpBigText = _cursor.getString(_cursorIndexOfBigText);
            }
            final String _tmpLargeIcon;
            if (_cursor.isNull(_cursorIndexOfLargeIcon)) {
              _tmpLargeIcon = null;
            } else {
              _tmpLargeIcon = _cursor.getString(_cursorIndexOfLargeIcon);
            }
            final String _tmpPicture;
            if (_cursor.isNull(_cursorIndexOfPicture)) {
              _tmpPicture = null;
            } else {
              _tmpPicture = _cursor.getString(_cursorIndexOfPicture);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsOngoing;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOngoing);
            _tmpIsOngoing = _tmp != 0;
            final boolean _tmpIsClearable;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsClearable);
            _tmpIsClearable = _tmp_1 != 0;
            final boolean _tmpIsRead;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_2 != 0;
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final boolean _tmpIsGroupSummary;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsGroupSummary);
            _tmpIsGroupSummary = _tmp_3 != 0;
            final String _tmpActions;
            if (_cursor.isNull(_cursorIndexOfActions)) {
              _tmpActions = null;
            } else {
              _tmpActions = _cursor.getString(_cursorIndexOfActions);
            }
            final String _tmpExtras;
            if (_cursor.isNull(_cursorIndexOfExtras)) {
              _tmpExtras = null;
            } else {
              _tmpExtras = _cursor.getString(_cursorIndexOfExtras);
            }
            final boolean _tmpForwarded;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfForwarded);
            _tmpForwarded = _tmp_4 != 0;
            final Long _tmpForwardTime;
            if (_cursor.isNull(_cursorIndexOfForwardTime)) {
              _tmpForwardTime = null;
            } else {
              _tmpForwardTime = _cursor.getLong(_cursorIndexOfForwardTime);
            }
            _result = new NotificationData(_tmpDbId,_tmpNotificationId,_tmpChannelId,_tmpFingerprint,_tmpPostTime,_tmpReceiveTime,_tmpPackageName,_tmpAppName,_tmpAppVersion,_tmpUid,_tmpTitle,_tmpContent,_tmpSubText,_tmpBigText,_tmpLargeIcon,_tmpPicture,_tmpPriority,_tmpIsOngoing,_tmpIsClearable,_tmpIsRead,_tmpCategory,_tmpIsGroupSummary,_tmpActions,_tmpExtras,_tmpForwarded,_tmpForwardTime);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUnforwarded(final int limit,
      final Continuation<? super List<NotificationData>> $completion) {
    final String _sql = "SELECT * FROM notifications WHERE forwarded = 0 ORDER BY postTime ASC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NotificationData>>() {
      @Override
      @NonNull
      public List<NotificationData> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDbId = CursorUtil.getColumnIndexOrThrow(_cursor, "dbId");
          final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "fingerprint");
          final int _cursorIndexOfPostTime = CursorUtil.getColumnIndexOrThrow(_cursor, "postTime");
          final int _cursorIndexOfReceiveTime = CursorUtil.getColumnIndexOrThrow(_cursor, "receiveTime");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfAppVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "appVersion");
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfSubText = CursorUtil.getColumnIndexOrThrow(_cursor, "subText");
          final int _cursorIndexOfBigText = CursorUtil.getColumnIndexOrThrow(_cursor, "bigText");
          final int _cursorIndexOfLargeIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "largeIcon");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsOngoing = CursorUtil.getColumnIndexOrThrow(_cursor, "isOngoing");
          final int _cursorIndexOfIsClearable = CursorUtil.getColumnIndexOrThrow(_cursor, "isClearable");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsGroupSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "isGroupSummary");
          final int _cursorIndexOfActions = CursorUtil.getColumnIndexOrThrow(_cursor, "actions");
          final int _cursorIndexOfExtras = CursorUtil.getColumnIndexOrThrow(_cursor, "extras");
          final int _cursorIndexOfForwarded = CursorUtil.getColumnIndexOrThrow(_cursor, "forwarded");
          final int _cursorIndexOfForwardTime = CursorUtil.getColumnIndexOrThrow(_cursor, "forwardTime");
          final List<NotificationData> _result = new ArrayList<NotificationData>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NotificationData _item;
            final long _tmpDbId;
            _tmpDbId = _cursor.getLong(_cursorIndexOfDbId);
            final int _tmpNotificationId;
            _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
            final String _tmpChannelId;
            if (_cursor.isNull(_cursorIndexOfChannelId)) {
              _tmpChannelId = null;
            } else {
              _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            }
            final String _tmpFingerprint;
            if (_cursor.isNull(_cursorIndexOfFingerprint)) {
              _tmpFingerprint = null;
            } else {
              _tmpFingerprint = _cursor.getString(_cursorIndexOfFingerprint);
            }
            final long _tmpPostTime;
            _tmpPostTime = _cursor.getLong(_cursorIndexOfPostTime);
            final long _tmpReceiveTime;
            _tmpReceiveTime = _cursor.getLong(_cursorIndexOfReceiveTime);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final String _tmpAppVersion;
            if (_cursor.isNull(_cursorIndexOfAppVersion)) {
              _tmpAppVersion = null;
            } else {
              _tmpAppVersion = _cursor.getString(_cursorIndexOfAppVersion);
            }
            final int _tmpUid;
            _tmpUid = _cursor.getInt(_cursorIndexOfUid);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpSubText;
            if (_cursor.isNull(_cursorIndexOfSubText)) {
              _tmpSubText = null;
            } else {
              _tmpSubText = _cursor.getString(_cursorIndexOfSubText);
            }
            final String _tmpBigText;
            if (_cursor.isNull(_cursorIndexOfBigText)) {
              _tmpBigText = null;
            } else {
              _tmpBigText = _cursor.getString(_cursorIndexOfBigText);
            }
            final String _tmpLargeIcon;
            if (_cursor.isNull(_cursorIndexOfLargeIcon)) {
              _tmpLargeIcon = null;
            } else {
              _tmpLargeIcon = _cursor.getString(_cursorIndexOfLargeIcon);
            }
            final String _tmpPicture;
            if (_cursor.isNull(_cursorIndexOfPicture)) {
              _tmpPicture = null;
            } else {
              _tmpPicture = _cursor.getString(_cursorIndexOfPicture);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsOngoing;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOngoing);
            _tmpIsOngoing = _tmp != 0;
            final boolean _tmpIsClearable;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsClearable);
            _tmpIsClearable = _tmp_1 != 0;
            final boolean _tmpIsRead;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_2 != 0;
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final boolean _tmpIsGroupSummary;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsGroupSummary);
            _tmpIsGroupSummary = _tmp_3 != 0;
            final String _tmpActions;
            if (_cursor.isNull(_cursorIndexOfActions)) {
              _tmpActions = null;
            } else {
              _tmpActions = _cursor.getString(_cursorIndexOfActions);
            }
            final String _tmpExtras;
            if (_cursor.isNull(_cursorIndexOfExtras)) {
              _tmpExtras = null;
            } else {
              _tmpExtras = _cursor.getString(_cursorIndexOfExtras);
            }
            final boolean _tmpForwarded;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfForwarded);
            _tmpForwarded = _tmp_4 != 0;
            final Long _tmpForwardTime;
            if (_cursor.isNull(_cursorIndexOfForwardTime)) {
              _tmpForwardTime = null;
            } else {
              _tmpForwardTime = _cursor.getLong(_cursorIndexOfForwardTime);
            }
            _item = new NotificationData(_tmpDbId,_tmpNotificationId,_tmpChannelId,_tmpFingerprint,_tmpPostTime,_tmpReceiveTime,_tmpPackageName,_tmpAppName,_tmpAppVersion,_tmpUid,_tmpTitle,_tmpContent,_tmpSubText,_tmpBigText,_tmpLargeIcon,_tmpPicture,_tmpPriority,_tmpIsOngoing,_tmpIsClearable,_tmpIsRead,_tmpCategory,_tmpIsGroupSummary,_tmpActions,_tmpExtras,_tmpForwarded,_tmpForwardTime);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByPackage(final String pkg, final int limit,
      final Continuation<? super List<NotificationData>> $completion) {
    final String _sql = "SELECT * FROM notifications WHERE packageName = ? ORDER BY postTime DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (pkg == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, pkg);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NotificationData>>() {
      @Override
      @NonNull
      public List<NotificationData> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDbId = CursorUtil.getColumnIndexOrThrow(_cursor, "dbId");
          final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "fingerprint");
          final int _cursorIndexOfPostTime = CursorUtil.getColumnIndexOrThrow(_cursor, "postTime");
          final int _cursorIndexOfReceiveTime = CursorUtil.getColumnIndexOrThrow(_cursor, "receiveTime");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfAppVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "appVersion");
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfSubText = CursorUtil.getColumnIndexOrThrow(_cursor, "subText");
          final int _cursorIndexOfBigText = CursorUtil.getColumnIndexOrThrow(_cursor, "bigText");
          final int _cursorIndexOfLargeIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "largeIcon");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsOngoing = CursorUtil.getColumnIndexOrThrow(_cursor, "isOngoing");
          final int _cursorIndexOfIsClearable = CursorUtil.getColumnIndexOrThrow(_cursor, "isClearable");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsGroupSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "isGroupSummary");
          final int _cursorIndexOfActions = CursorUtil.getColumnIndexOrThrow(_cursor, "actions");
          final int _cursorIndexOfExtras = CursorUtil.getColumnIndexOrThrow(_cursor, "extras");
          final int _cursorIndexOfForwarded = CursorUtil.getColumnIndexOrThrow(_cursor, "forwarded");
          final int _cursorIndexOfForwardTime = CursorUtil.getColumnIndexOrThrow(_cursor, "forwardTime");
          final List<NotificationData> _result = new ArrayList<NotificationData>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NotificationData _item;
            final long _tmpDbId;
            _tmpDbId = _cursor.getLong(_cursorIndexOfDbId);
            final int _tmpNotificationId;
            _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
            final String _tmpChannelId;
            if (_cursor.isNull(_cursorIndexOfChannelId)) {
              _tmpChannelId = null;
            } else {
              _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            }
            final String _tmpFingerprint;
            if (_cursor.isNull(_cursorIndexOfFingerprint)) {
              _tmpFingerprint = null;
            } else {
              _tmpFingerprint = _cursor.getString(_cursorIndexOfFingerprint);
            }
            final long _tmpPostTime;
            _tmpPostTime = _cursor.getLong(_cursorIndexOfPostTime);
            final long _tmpReceiveTime;
            _tmpReceiveTime = _cursor.getLong(_cursorIndexOfReceiveTime);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final String _tmpAppVersion;
            if (_cursor.isNull(_cursorIndexOfAppVersion)) {
              _tmpAppVersion = null;
            } else {
              _tmpAppVersion = _cursor.getString(_cursorIndexOfAppVersion);
            }
            final int _tmpUid;
            _tmpUid = _cursor.getInt(_cursorIndexOfUid);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpSubText;
            if (_cursor.isNull(_cursorIndexOfSubText)) {
              _tmpSubText = null;
            } else {
              _tmpSubText = _cursor.getString(_cursorIndexOfSubText);
            }
            final String _tmpBigText;
            if (_cursor.isNull(_cursorIndexOfBigText)) {
              _tmpBigText = null;
            } else {
              _tmpBigText = _cursor.getString(_cursorIndexOfBigText);
            }
            final String _tmpLargeIcon;
            if (_cursor.isNull(_cursorIndexOfLargeIcon)) {
              _tmpLargeIcon = null;
            } else {
              _tmpLargeIcon = _cursor.getString(_cursorIndexOfLargeIcon);
            }
            final String _tmpPicture;
            if (_cursor.isNull(_cursorIndexOfPicture)) {
              _tmpPicture = null;
            } else {
              _tmpPicture = _cursor.getString(_cursorIndexOfPicture);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsOngoing;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOngoing);
            _tmpIsOngoing = _tmp != 0;
            final boolean _tmpIsClearable;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsClearable);
            _tmpIsClearable = _tmp_1 != 0;
            final boolean _tmpIsRead;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_2 != 0;
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final boolean _tmpIsGroupSummary;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsGroupSummary);
            _tmpIsGroupSummary = _tmp_3 != 0;
            final String _tmpActions;
            if (_cursor.isNull(_cursorIndexOfActions)) {
              _tmpActions = null;
            } else {
              _tmpActions = _cursor.getString(_cursorIndexOfActions);
            }
            final String _tmpExtras;
            if (_cursor.isNull(_cursorIndexOfExtras)) {
              _tmpExtras = null;
            } else {
              _tmpExtras = _cursor.getString(_cursorIndexOfExtras);
            }
            final boolean _tmpForwarded;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfForwarded);
            _tmpForwarded = _tmp_4 != 0;
            final Long _tmpForwardTime;
            if (_cursor.isNull(_cursorIndexOfForwardTime)) {
              _tmpForwardTime = null;
            } else {
              _tmpForwardTime = _cursor.getLong(_cursorIndexOfForwardTime);
            }
            _item = new NotificationData(_tmpDbId,_tmpNotificationId,_tmpChannelId,_tmpFingerprint,_tmpPostTime,_tmpReceiveTime,_tmpPackageName,_tmpAppName,_tmpAppVersion,_tmpUid,_tmpTitle,_tmpContent,_tmpSubText,_tmpBigText,_tmpLargeIcon,_tmpPicture,_tmpPriority,_tmpIsOngoing,_tmpIsClearable,_tmpIsRead,_tmpCategory,_tmpIsGroupSummary,_tmpActions,_tmpExtras,_tmpForwarded,_tmpForwardTime);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchByKeyword(final String keyword, final int limit,
      final Continuation<? super List<NotificationData>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM notifications \n"
            + "        WHERE title LIKE '%' || ? || '%' \n"
            + "           OR content LIKE '%' || ? || '%'\n"
            + "        ORDER BY postTime DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (keyword == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, keyword);
    }
    _argIndex = 2;
    if (keyword == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, keyword);
    }
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NotificationData>>() {
      @Override
      @NonNull
      public List<NotificationData> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDbId = CursorUtil.getColumnIndexOrThrow(_cursor, "dbId");
          final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "fingerprint");
          final int _cursorIndexOfPostTime = CursorUtil.getColumnIndexOrThrow(_cursor, "postTime");
          final int _cursorIndexOfReceiveTime = CursorUtil.getColumnIndexOrThrow(_cursor, "receiveTime");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfAppVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "appVersion");
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfSubText = CursorUtil.getColumnIndexOrThrow(_cursor, "subText");
          final int _cursorIndexOfBigText = CursorUtil.getColumnIndexOrThrow(_cursor, "bigText");
          final int _cursorIndexOfLargeIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "largeIcon");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsOngoing = CursorUtil.getColumnIndexOrThrow(_cursor, "isOngoing");
          final int _cursorIndexOfIsClearable = CursorUtil.getColumnIndexOrThrow(_cursor, "isClearable");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsGroupSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "isGroupSummary");
          final int _cursorIndexOfActions = CursorUtil.getColumnIndexOrThrow(_cursor, "actions");
          final int _cursorIndexOfExtras = CursorUtil.getColumnIndexOrThrow(_cursor, "extras");
          final int _cursorIndexOfForwarded = CursorUtil.getColumnIndexOrThrow(_cursor, "forwarded");
          final int _cursorIndexOfForwardTime = CursorUtil.getColumnIndexOrThrow(_cursor, "forwardTime");
          final List<NotificationData> _result = new ArrayList<NotificationData>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NotificationData _item;
            final long _tmpDbId;
            _tmpDbId = _cursor.getLong(_cursorIndexOfDbId);
            final int _tmpNotificationId;
            _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
            final String _tmpChannelId;
            if (_cursor.isNull(_cursorIndexOfChannelId)) {
              _tmpChannelId = null;
            } else {
              _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            }
            final String _tmpFingerprint;
            if (_cursor.isNull(_cursorIndexOfFingerprint)) {
              _tmpFingerprint = null;
            } else {
              _tmpFingerprint = _cursor.getString(_cursorIndexOfFingerprint);
            }
            final long _tmpPostTime;
            _tmpPostTime = _cursor.getLong(_cursorIndexOfPostTime);
            final long _tmpReceiveTime;
            _tmpReceiveTime = _cursor.getLong(_cursorIndexOfReceiveTime);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final String _tmpAppVersion;
            if (_cursor.isNull(_cursorIndexOfAppVersion)) {
              _tmpAppVersion = null;
            } else {
              _tmpAppVersion = _cursor.getString(_cursorIndexOfAppVersion);
            }
            final int _tmpUid;
            _tmpUid = _cursor.getInt(_cursorIndexOfUid);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpSubText;
            if (_cursor.isNull(_cursorIndexOfSubText)) {
              _tmpSubText = null;
            } else {
              _tmpSubText = _cursor.getString(_cursorIndexOfSubText);
            }
            final String _tmpBigText;
            if (_cursor.isNull(_cursorIndexOfBigText)) {
              _tmpBigText = null;
            } else {
              _tmpBigText = _cursor.getString(_cursorIndexOfBigText);
            }
            final String _tmpLargeIcon;
            if (_cursor.isNull(_cursorIndexOfLargeIcon)) {
              _tmpLargeIcon = null;
            } else {
              _tmpLargeIcon = _cursor.getString(_cursorIndexOfLargeIcon);
            }
            final String _tmpPicture;
            if (_cursor.isNull(_cursorIndexOfPicture)) {
              _tmpPicture = null;
            } else {
              _tmpPicture = _cursor.getString(_cursorIndexOfPicture);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsOngoing;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOngoing);
            _tmpIsOngoing = _tmp != 0;
            final boolean _tmpIsClearable;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsClearable);
            _tmpIsClearable = _tmp_1 != 0;
            final boolean _tmpIsRead;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_2 != 0;
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final boolean _tmpIsGroupSummary;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsGroupSummary);
            _tmpIsGroupSummary = _tmp_3 != 0;
            final String _tmpActions;
            if (_cursor.isNull(_cursorIndexOfActions)) {
              _tmpActions = null;
            } else {
              _tmpActions = _cursor.getString(_cursorIndexOfActions);
            }
            final String _tmpExtras;
            if (_cursor.isNull(_cursorIndexOfExtras)) {
              _tmpExtras = null;
            } else {
              _tmpExtras = _cursor.getString(_cursorIndexOfExtras);
            }
            final boolean _tmpForwarded;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfForwarded);
            _tmpForwarded = _tmp_4 != 0;
            final Long _tmpForwardTime;
            if (_cursor.isNull(_cursorIndexOfForwardTime)) {
              _tmpForwardTime = null;
            } else {
              _tmpForwardTime = _cursor.getLong(_cursorIndexOfForwardTime);
            }
            _item = new NotificationData(_tmpDbId,_tmpNotificationId,_tmpChannelId,_tmpFingerprint,_tmpPostTime,_tmpReceiveTime,_tmpPackageName,_tmpAppName,_tmpAppVersion,_tmpUid,_tmpTitle,_tmpContent,_tmpSubText,_tmpBigText,_tmpLargeIcon,_tmpPicture,_tmpPriority,_tmpIsOngoing,_tmpIsClearable,_tmpIsRead,_tmpCategory,_tmpIsGroupSummary,_tmpActions,_tmpExtras,_tmpForwarded,_tmpForwardTime);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchByKeywordInTimeRange(final String keyword, final long startTime,
      final int limit, final Continuation<? super List<NotificationData>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM notifications \n"
            + "        WHERE postTime >= ?\n"
            + "          AND (title LIKE '%' || ? || '%' \n"
            + "               OR content LIKE '%' || ? || '%')\n"
            + "        ORDER BY postTime DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    if (keyword == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, keyword);
    }
    _argIndex = 3;
    if (keyword == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, keyword);
    }
    _argIndex = 4;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NotificationData>>() {
      @Override
      @NonNull
      public List<NotificationData> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDbId = CursorUtil.getColumnIndexOrThrow(_cursor, "dbId");
          final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "fingerprint");
          final int _cursorIndexOfPostTime = CursorUtil.getColumnIndexOrThrow(_cursor, "postTime");
          final int _cursorIndexOfReceiveTime = CursorUtil.getColumnIndexOrThrow(_cursor, "receiveTime");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfAppVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "appVersion");
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfSubText = CursorUtil.getColumnIndexOrThrow(_cursor, "subText");
          final int _cursorIndexOfBigText = CursorUtil.getColumnIndexOrThrow(_cursor, "bigText");
          final int _cursorIndexOfLargeIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "largeIcon");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsOngoing = CursorUtil.getColumnIndexOrThrow(_cursor, "isOngoing");
          final int _cursorIndexOfIsClearable = CursorUtil.getColumnIndexOrThrow(_cursor, "isClearable");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsGroupSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "isGroupSummary");
          final int _cursorIndexOfActions = CursorUtil.getColumnIndexOrThrow(_cursor, "actions");
          final int _cursorIndexOfExtras = CursorUtil.getColumnIndexOrThrow(_cursor, "extras");
          final int _cursorIndexOfForwarded = CursorUtil.getColumnIndexOrThrow(_cursor, "forwarded");
          final int _cursorIndexOfForwardTime = CursorUtil.getColumnIndexOrThrow(_cursor, "forwardTime");
          final List<NotificationData> _result = new ArrayList<NotificationData>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NotificationData _item;
            final long _tmpDbId;
            _tmpDbId = _cursor.getLong(_cursorIndexOfDbId);
            final int _tmpNotificationId;
            _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
            final String _tmpChannelId;
            if (_cursor.isNull(_cursorIndexOfChannelId)) {
              _tmpChannelId = null;
            } else {
              _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            }
            final String _tmpFingerprint;
            if (_cursor.isNull(_cursorIndexOfFingerprint)) {
              _tmpFingerprint = null;
            } else {
              _tmpFingerprint = _cursor.getString(_cursorIndexOfFingerprint);
            }
            final long _tmpPostTime;
            _tmpPostTime = _cursor.getLong(_cursorIndexOfPostTime);
            final long _tmpReceiveTime;
            _tmpReceiveTime = _cursor.getLong(_cursorIndexOfReceiveTime);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final String _tmpAppVersion;
            if (_cursor.isNull(_cursorIndexOfAppVersion)) {
              _tmpAppVersion = null;
            } else {
              _tmpAppVersion = _cursor.getString(_cursorIndexOfAppVersion);
            }
            final int _tmpUid;
            _tmpUid = _cursor.getInt(_cursorIndexOfUid);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpSubText;
            if (_cursor.isNull(_cursorIndexOfSubText)) {
              _tmpSubText = null;
            } else {
              _tmpSubText = _cursor.getString(_cursorIndexOfSubText);
            }
            final String _tmpBigText;
            if (_cursor.isNull(_cursorIndexOfBigText)) {
              _tmpBigText = null;
            } else {
              _tmpBigText = _cursor.getString(_cursorIndexOfBigText);
            }
            final String _tmpLargeIcon;
            if (_cursor.isNull(_cursorIndexOfLargeIcon)) {
              _tmpLargeIcon = null;
            } else {
              _tmpLargeIcon = _cursor.getString(_cursorIndexOfLargeIcon);
            }
            final String _tmpPicture;
            if (_cursor.isNull(_cursorIndexOfPicture)) {
              _tmpPicture = null;
            } else {
              _tmpPicture = _cursor.getString(_cursorIndexOfPicture);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsOngoing;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOngoing);
            _tmpIsOngoing = _tmp != 0;
            final boolean _tmpIsClearable;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsClearable);
            _tmpIsClearable = _tmp_1 != 0;
            final boolean _tmpIsRead;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_2 != 0;
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final boolean _tmpIsGroupSummary;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsGroupSummary);
            _tmpIsGroupSummary = _tmp_3 != 0;
            final String _tmpActions;
            if (_cursor.isNull(_cursorIndexOfActions)) {
              _tmpActions = null;
            } else {
              _tmpActions = _cursor.getString(_cursorIndexOfActions);
            }
            final String _tmpExtras;
            if (_cursor.isNull(_cursorIndexOfExtras)) {
              _tmpExtras = null;
            } else {
              _tmpExtras = _cursor.getString(_cursorIndexOfExtras);
            }
            final boolean _tmpForwarded;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfForwarded);
            _tmpForwarded = _tmp_4 != 0;
            final Long _tmpForwardTime;
            if (_cursor.isNull(_cursorIndexOfForwardTime)) {
              _tmpForwardTime = null;
            } else {
              _tmpForwardTime = _cursor.getLong(_cursorIndexOfForwardTime);
            }
            _item = new NotificationData(_tmpDbId,_tmpNotificationId,_tmpChannelId,_tmpFingerprint,_tmpPostTime,_tmpReceiveTime,_tmpPackageName,_tmpAppName,_tmpAppVersion,_tmpUid,_tmpTitle,_tmpContent,_tmpSubText,_tmpBigText,_tmpLargeIcon,_tmpPicture,_tmpPriority,_tmpIsOngoing,_tmpIsClearable,_tmpIsRead,_tmpCategory,_tmpIsGroupSummary,_tmpActions,_tmpExtras,_tmpForwarded,_tmpForwardTime);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecent(final int limit,
      final Continuation<? super List<NotificationData>> $completion) {
    final String _sql = "SELECT * FROM notifications ORDER BY postTime DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NotificationData>>() {
      @Override
      @NonNull
      public List<NotificationData> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDbId = CursorUtil.getColumnIndexOrThrow(_cursor, "dbId");
          final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "fingerprint");
          final int _cursorIndexOfPostTime = CursorUtil.getColumnIndexOrThrow(_cursor, "postTime");
          final int _cursorIndexOfReceiveTime = CursorUtil.getColumnIndexOrThrow(_cursor, "receiveTime");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfAppVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "appVersion");
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfSubText = CursorUtil.getColumnIndexOrThrow(_cursor, "subText");
          final int _cursorIndexOfBigText = CursorUtil.getColumnIndexOrThrow(_cursor, "bigText");
          final int _cursorIndexOfLargeIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "largeIcon");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsOngoing = CursorUtil.getColumnIndexOrThrow(_cursor, "isOngoing");
          final int _cursorIndexOfIsClearable = CursorUtil.getColumnIndexOrThrow(_cursor, "isClearable");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsGroupSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "isGroupSummary");
          final int _cursorIndexOfActions = CursorUtil.getColumnIndexOrThrow(_cursor, "actions");
          final int _cursorIndexOfExtras = CursorUtil.getColumnIndexOrThrow(_cursor, "extras");
          final int _cursorIndexOfForwarded = CursorUtil.getColumnIndexOrThrow(_cursor, "forwarded");
          final int _cursorIndexOfForwardTime = CursorUtil.getColumnIndexOrThrow(_cursor, "forwardTime");
          final List<NotificationData> _result = new ArrayList<NotificationData>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NotificationData _item;
            final long _tmpDbId;
            _tmpDbId = _cursor.getLong(_cursorIndexOfDbId);
            final int _tmpNotificationId;
            _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
            final String _tmpChannelId;
            if (_cursor.isNull(_cursorIndexOfChannelId)) {
              _tmpChannelId = null;
            } else {
              _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            }
            final String _tmpFingerprint;
            if (_cursor.isNull(_cursorIndexOfFingerprint)) {
              _tmpFingerprint = null;
            } else {
              _tmpFingerprint = _cursor.getString(_cursorIndexOfFingerprint);
            }
            final long _tmpPostTime;
            _tmpPostTime = _cursor.getLong(_cursorIndexOfPostTime);
            final long _tmpReceiveTime;
            _tmpReceiveTime = _cursor.getLong(_cursorIndexOfReceiveTime);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final String _tmpAppVersion;
            if (_cursor.isNull(_cursorIndexOfAppVersion)) {
              _tmpAppVersion = null;
            } else {
              _tmpAppVersion = _cursor.getString(_cursorIndexOfAppVersion);
            }
            final int _tmpUid;
            _tmpUid = _cursor.getInt(_cursorIndexOfUid);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpSubText;
            if (_cursor.isNull(_cursorIndexOfSubText)) {
              _tmpSubText = null;
            } else {
              _tmpSubText = _cursor.getString(_cursorIndexOfSubText);
            }
            final String _tmpBigText;
            if (_cursor.isNull(_cursorIndexOfBigText)) {
              _tmpBigText = null;
            } else {
              _tmpBigText = _cursor.getString(_cursorIndexOfBigText);
            }
            final String _tmpLargeIcon;
            if (_cursor.isNull(_cursorIndexOfLargeIcon)) {
              _tmpLargeIcon = null;
            } else {
              _tmpLargeIcon = _cursor.getString(_cursorIndexOfLargeIcon);
            }
            final String _tmpPicture;
            if (_cursor.isNull(_cursorIndexOfPicture)) {
              _tmpPicture = null;
            } else {
              _tmpPicture = _cursor.getString(_cursorIndexOfPicture);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsOngoing;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOngoing);
            _tmpIsOngoing = _tmp != 0;
            final boolean _tmpIsClearable;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsClearable);
            _tmpIsClearable = _tmp_1 != 0;
            final boolean _tmpIsRead;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_2 != 0;
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final boolean _tmpIsGroupSummary;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsGroupSummary);
            _tmpIsGroupSummary = _tmp_3 != 0;
            final String _tmpActions;
            if (_cursor.isNull(_cursorIndexOfActions)) {
              _tmpActions = null;
            } else {
              _tmpActions = _cursor.getString(_cursorIndexOfActions);
            }
            final String _tmpExtras;
            if (_cursor.isNull(_cursorIndexOfExtras)) {
              _tmpExtras = null;
            } else {
              _tmpExtras = _cursor.getString(_cursorIndexOfExtras);
            }
            final boolean _tmpForwarded;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfForwarded);
            _tmpForwarded = _tmp_4 != 0;
            final Long _tmpForwardTime;
            if (_cursor.isNull(_cursorIndexOfForwardTime)) {
              _tmpForwardTime = null;
            } else {
              _tmpForwardTime = _cursor.getLong(_cursorIndexOfForwardTime);
            }
            _item = new NotificationData(_tmpDbId,_tmpNotificationId,_tmpChannelId,_tmpFingerprint,_tmpPostTime,_tmpReceiveTime,_tmpPackageName,_tmpAppName,_tmpAppVersion,_tmpUid,_tmpTitle,_tmpContent,_tmpSubText,_tmpBigText,_tmpLargeIcon,_tmpPicture,_tmpPriority,_tmpIsOngoing,_tmpIsClearable,_tmpIsRead,_tmpCategory,_tmpIsGroupSummary,_tmpActions,_tmpExtras,_tmpForwarded,_tmpForwardTime);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByTimeRange(final long startTime, final long endTime,
      final Continuation<? super List<NotificationData>> $completion) {
    final String _sql = "SELECT * FROM notifications WHERE postTime BETWEEN ? AND ? ORDER BY postTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NotificationData>>() {
      @Override
      @NonNull
      public List<NotificationData> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDbId = CursorUtil.getColumnIndexOrThrow(_cursor, "dbId");
          final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "fingerprint");
          final int _cursorIndexOfPostTime = CursorUtil.getColumnIndexOrThrow(_cursor, "postTime");
          final int _cursorIndexOfReceiveTime = CursorUtil.getColumnIndexOrThrow(_cursor, "receiveTime");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfAppVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "appVersion");
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfSubText = CursorUtil.getColumnIndexOrThrow(_cursor, "subText");
          final int _cursorIndexOfBigText = CursorUtil.getColumnIndexOrThrow(_cursor, "bigText");
          final int _cursorIndexOfLargeIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "largeIcon");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsOngoing = CursorUtil.getColumnIndexOrThrow(_cursor, "isOngoing");
          final int _cursorIndexOfIsClearable = CursorUtil.getColumnIndexOrThrow(_cursor, "isClearable");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsGroupSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "isGroupSummary");
          final int _cursorIndexOfActions = CursorUtil.getColumnIndexOrThrow(_cursor, "actions");
          final int _cursorIndexOfExtras = CursorUtil.getColumnIndexOrThrow(_cursor, "extras");
          final int _cursorIndexOfForwarded = CursorUtil.getColumnIndexOrThrow(_cursor, "forwarded");
          final int _cursorIndexOfForwardTime = CursorUtil.getColumnIndexOrThrow(_cursor, "forwardTime");
          final List<NotificationData> _result = new ArrayList<NotificationData>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NotificationData _item;
            final long _tmpDbId;
            _tmpDbId = _cursor.getLong(_cursorIndexOfDbId);
            final int _tmpNotificationId;
            _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
            final String _tmpChannelId;
            if (_cursor.isNull(_cursorIndexOfChannelId)) {
              _tmpChannelId = null;
            } else {
              _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            }
            final String _tmpFingerprint;
            if (_cursor.isNull(_cursorIndexOfFingerprint)) {
              _tmpFingerprint = null;
            } else {
              _tmpFingerprint = _cursor.getString(_cursorIndexOfFingerprint);
            }
            final long _tmpPostTime;
            _tmpPostTime = _cursor.getLong(_cursorIndexOfPostTime);
            final long _tmpReceiveTime;
            _tmpReceiveTime = _cursor.getLong(_cursorIndexOfReceiveTime);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final String _tmpAppVersion;
            if (_cursor.isNull(_cursorIndexOfAppVersion)) {
              _tmpAppVersion = null;
            } else {
              _tmpAppVersion = _cursor.getString(_cursorIndexOfAppVersion);
            }
            final int _tmpUid;
            _tmpUid = _cursor.getInt(_cursorIndexOfUid);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpSubText;
            if (_cursor.isNull(_cursorIndexOfSubText)) {
              _tmpSubText = null;
            } else {
              _tmpSubText = _cursor.getString(_cursorIndexOfSubText);
            }
            final String _tmpBigText;
            if (_cursor.isNull(_cursorIndexOfBigText)) {
              _tmpBigText = null;
            } else {
              _tmpBigText = _cursor.getString(_cursorIndexOfBigText);
            }
            final String _tmpLargeIcon;
            if (_cursor.isNull(_cursorIndexOfLargeIcon)) {
              _tmpLargeIcon = null;
            } else {
              _tmpLargeIcon = _cursor.getString(_cursorIndexOfLargeIcon);
            }
            final String _tmpPicture;
            if (_cursor.isNull(_cursorIndexOfPicture)) {
              _tmpPicture = null;
            } else {
              _tmpPicture = _cursor.getString(_cursorIndexOfPicture);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsOngoing;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOngoing);
            _tmpIsOngoing = _tmp != 0;
            final boolean _tmpIsClearable;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsClearable);
            _tmpIsClearable = _tmp_1 != 0;
            final boolean _tmpIsRead;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_2 != 0;
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final boolean _tmpIsGroupSummary;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsGroupSummary);
            _tmpIsGroupSummary = _tmp_3 != 0;
            final String _tmpActions;
            if (_cursor.isNull(_cursorIndexOfActions)) {
              _tmpActions = null;
            } else {
              _tmpActions = _cursor.getString(_cursorIndexOfActions);
            }
            final String _tmpExtras;
            if (_cursor.isNull(_cursorIndexOfExtras)) {
              _tmpExtras = null;
            } else {
              _tmpExtras = _cursor.getString(_cursorIndexOfExtras);
            }
            final boolean _tmpForwarded;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfForwarded);
            _tmpForwarded = _tmp_4 != 0;
            final Long _tmpForwardTime;
            if (_cursor.isNull(_cursorIndexOfForwardTime)) {
              _tmpForwardTime = null;
            } else {
              _tmpForwardTime = _cursor.getLong(_cursorIndexOfForwardTime);
            }
            _item = new NotificationData(_tmpDbId,_tmpNotificationId,_tmpChannelId,_tmpFingerprint,_tmpPostTime,_tmpReceiveTime,_tmpPackageName,_tmpAppName,_tmpAppVersion,_tmpUid,_tmpTitle,_tmpContent,_tmpSubText,_tmpBigText,_tmpLargeIcon,_tmpPicture,_tmpPriority,_tmpIsOngoing,_tmpIsClearable,_tmpIsRead,_tmpCategory,_tmpIsGroupSummary,_tmpActions,_tmpExtras,_tmpForwarded,_tmpForwardTime);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTodayCount(final long dayStart,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM notifications WHERE postTime >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dayStart);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTodayForwardedCount(final long dayStart,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM notifications WHERE postTime >= ? AND forwarded = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dayStart);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTodayFilteredCount(final long dayStart,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM notifications WHERE postTime >= ? AND forwarded = 0 AND isRead = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, dayStart);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUnforwardedCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM notifications WHERE forwarded = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalCount(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT COUNT(*) FROM notifications";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object markBatchForwarded(final List<Long> dbIds, final long forwardTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE notifications SET forwarded = 1, forwardTime = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE dbId IN (");
        final int _inputSize = dbIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, forwardTime);
        _argIndex = 2;
        for (Long _item : dbIds) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindLong(_argIndex, _item);
          }
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
