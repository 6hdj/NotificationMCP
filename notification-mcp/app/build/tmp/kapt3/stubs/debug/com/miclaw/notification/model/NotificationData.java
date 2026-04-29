package com.miclaw.notification.model;

/**
 * 通知数据模型 —— 全字段无损解析
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\bG\b\u0087\b\u0018\u0000 ]2\u00020\u0001:\u0001]B\u0089\u0002\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0007\u0012\u0006\u0010\f\u001a\u00020\u0007\u0012\b\b\u0002\u0010\r\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0007\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0017\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0017\u0012\b\b\u0002\u0010\u0019\u001a\u00020\u0017\u0012\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\u001b\u001a\u00020\u0017\u0012\b\b\u0002\u0010\u001c\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u001d\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u001e\u001a\u00020\u0017\u0012\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010 J\t\u0010=\u001a\u00020\u0003H\u00c6\u0003J\t\u0010>\u001a\u00020\u0005H\u00c6\u0003J\t\u0010?\u001a\u00020\u0007H\u00c6\u0003J\t\u0010@\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010A\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u0010B\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u0010C\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u0010D\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010E\u001a\u00020\u0005H\u00c6\u0003J\t\u0010F\u001a\u00020\u0017H\u00c6\u0003J\t\u0010G\u001a\u00020\u0017H\u00c6\u0003J\t\u0010H\u001a\u00020\u0005H\u00c6\u0003J\t\u0010I\u001a\u00020\u0017H\u00c6\u0003J\u000b\u0010J\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010K\u001a\u00020\u0017H\u00c6\u0003J\t\u0010L\u001a\u00020\u0007H\u00c6\u0003J\t\u0010M\u001a\u00020\u0007H\u00c6\u0003J\t\u0010N\u001a\u00020\u0017H\u00c6\u0003J\u0010\u0010O\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010.J\u000b\u0010P\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010Q\u001a\u00020\u0007H\u00c6\u0003J\t\u0010R\u001a\u00020\u0003H\u00c6\u0003J\t\u0010S\u001a\u00020\u0003H\u00c6\u0003J\t\u0010T\u001a\u00020\u0007H\u00c6\u0003J\t\u0010U\u001a\u00020\u0007H\u00c6\u0003J\t\u0010V\u001a\u00020\u0007H\u00c6\u0003J\u00a0\u0002\u0010W\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\u00072\b\b\u0002\u0010\r\u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u00052\b\b\u0002\u0010\u000f\u001a\u00020\u00072\b\b\u0002\u0010\u0010\u001a\u00020\u00072\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\u0015\u001a\u00020\u00052\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u00172\b\b\u0002\u0010\u0019\u001a\u00020\u00172\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\u001b\u001a\u00020\u00172\b\b\u0002\u0010\u001c\u001a\u00020\u00072\b\b\u0002\u0010\u001d\u001a\u00020\u00072\b\b\u0002\u0010\u001e\u001a\u00020\u00172\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010XJ\u0013\u0010Y\u001a\u00020\u00172\b\u0010Z\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010[\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\\\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\u001c\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\"R\u0011\u0010\r\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\"R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\"R\u0013\u0010\u001a\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\"R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\"R\u0011\u0010\u0010\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\"R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0011\u0010\u001d\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\"R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\"R\u0015\u0010\u001f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010/\u001a\u0004\b-\u0010.R\u0011\u0010\u001e\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u00101R\u0011\u0010\u0018\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u00101R\u0011\u0010\u001b\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u00101R\u0011\u0010\u0016\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u00101R\u0011\u0010\u0019\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u00101R\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\"R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u0011\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010\"R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u0010\"R\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u0010*R\u0011\u0010\u0015\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00104R\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u0010*R\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010\"R\u0011\u0010\u000f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b;\u0010\"R\u0011\u0010\u000e\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u00104\u00a8\u0006^"}, d2 = {"Lcom/miclaw/notification/model/NotificationData;", "", "dbId", "", "notificationId", "", "channelId", "", "fingerprint", "postTime", "receiveTime", "packageName", "appName", "appVersion", "uid", "title", "content", "subText", "bigText", "largeIcon", "picture", "priority", "isOngoing", "", "isClearable", "isRead", "category", "isGroupSummary", "actions", "extras", "forwarded", "forwardTime", "(JILjava/lang/String;Ljava/lang/String;JJLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZZZLjava/lang/String;ZLjava/lang/String;Ljava/lang/String;ZLjava/lang/Long;)V", "getActions", "()Ljava/lang/String;", "getAppName", "getAppVersion", "getBigText", "getCategory", "getChannelId", "getContent", "getDbId", "()J", "getExtras", "getFingerprint", "getForwardTime", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getForwarded", "()Z", "getLargeIcon", "getNotificationId", "()I", "getPackageName", "getPicture", "getPostTime", "getPriority", "getReceiveTime", "getSubText", "getTitle", "getUid", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(JILjava/lang/String;Ljava/lang/String;JJLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZZZLjava/lang/String;ZLjava/lang/String;Ljava/lang/String;ZLjava/lang/Long;)Lcom/miclaw/notification/model/NotificationData;", "equals", "other", "hashCode", "toString", "Companion", "app_debug"})
@androidx.room.Entity(tableName = "notifications")
public final class NotificationData {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long dbId = 0L;
    private final int notificationId = 0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String channelId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String fingerprint = null;
    private final long postTime = 0L;
    private final long receiveTime = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String packageName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String appName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String appVersion = null;
    private final int uid = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String content = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String subText = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String bigText = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String largeIcon = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String picture = null;
    private final int priority = 0;
    private final boolean isOngoing = false;
    private final boolean isClearable = false;
    private final boolean isRead = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String category = null;
    private final boolean isGroupSummary = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String actions = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String extras = null;
    private final boolean forwarded = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long forwardTime = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.miclaw.notification.model.NotificationData.Companion Companion = null;
    
    public NotificationData(long dbId, int notificationId, @org.jetbrains.annotations.Nullable()
    java.lang.String channelId, @org.jetbrains.annotations.NotNull()
    java.lang.String fingerprint, long postTime, long receiveTime, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    java.lang.String appName, @org.jetbrains.annotations.NotNull()
    java.lang.String appVersion, int uid, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.Nullable()
    java.lang.String subText, @org.jetbrains.annotations.Nullable()
    java.lang.String bigText, @org.jetbrains.annotations.Nullable()
    java.lang.String largeIcon, @org.jetbrains.annotations.Nullable()
    java.lang.String picture, int priority, boolean isOngoing, boolean isClearable, boolean isRead, @org.jetbrains.annotations.Nullable()
    java.lang.String category, boolean isGroupSummary, @org.jetbrains.annotations.NotNull()
    java.lang.String actions, @org.jetbrains.annotations.NotNull()
    java.lang.String extras, boolean forwarded, @org.jetbrains.annotations.Nullable()
    java.lang.Long forwardTime) {
        super();
    }
    
    public final long getDbId() {
        return 0L;
    }
    
    public final int getNotificationId() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getChannelId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFingerprint() {
        return null;
    }
    
    public final long getPostTime() {
        return 0L;
    }
    
    public final long getReceiveTime() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPackageName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAppName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAppVersion() {
        return null;
    }
    
    public final int getUid() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getContent() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSubText() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getBigText() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLargeIcon() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPicture() {
        return null;
    }
    
    public final int getPriority() {
        return 0;
    }
    
    public final boolean isOngoing() {
        return false;
    }
    
    public final boolean isClearable() {
        return false;
    }
    
    public final boolean isRead() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCategory() {
        return null;
    }
    
    public final boolean isGroupSummary() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getActions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getExtras() {
        return null;
    }
    
    public final boolean getForwarded() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getForwardTime() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final int component10() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component15() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component16() {
        return null;
    }
    
    public final int component17() {
        return 0;
    }
    
    public final boolean component18() {
        return false;
    }
    
    public final boolean component19() {
        return false;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final boolean component20() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component21() {
        return null;
    }
    
    public final boolean component22() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component23() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component24() {
        return null;
    }
    
    public final boolean component25() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component26() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    public final long component5() {
        return 0L;
    }
    
    public final long component6() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.miclaw.notification.model.NotificationData copy(long dbId, int notificationId, @org.jetbrains.annotations.Nullable()
    java.lang.String channelId, @org.jetbrains.annotations.NotNull()
    java.lang.String fingerprint, long postTime, long receiveTime, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    java.lang.String appName, @org.jetbrains.annotations.NotNull()
    java.lang.String appVersion, int uid, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.Nullable()
    java.lang.String subText, @org.jetbrains.annotations.Nullable()
    java.lang.String bigText, @org.jetbrains.annotations.Nullable()
    java.lang.String largeIcon, @org.jetbrains.annotations.Nullable()
    java.lang.String picture, int priority, boolean isOngoing, boolean isClearable, boolean isRead, @org.jetbrains.annotations.Nullable()
    java.lang.String category, boolean isGroupSummary, @org.jetbrains.annotations.NotNull()
    java.lang.String actions, @org.jetbrains.annotations.NotNull()
    java.lang.String extras, boolean forwarded, @org.jetbrains.annotations.Nullable()
    java.lang.Long forwardTime) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t\u00a8\u0006\n"}, d2 = {"Lcom/miclaw/notification/model/NotificationData$Companion;", "", "()V", "generateFingerprint", "", "notificationId", "", "packageName", "postTime", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * 生成去重指纹: notificationId + packageName + postTime
         */
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String generateFingerprint(int notificationId, @org.jetbrains.annotations.NotNull()
        java.lang.String packageName, long postTime) {
            return null;
        }
    }
}