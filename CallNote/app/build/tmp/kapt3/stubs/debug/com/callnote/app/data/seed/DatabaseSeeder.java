package com.callnote.app.data.seed;

/**
 * 第一阶段模拟数据：仅在空库时写入一次，用于展示首页统计、录音列表、
 * 分类管理与“我的”页的真实数据形态。第二阶段导入功能上线后仍然保留空库逻辑。
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001\u001bB+\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0001\u0010\b\u001a\u00020\t\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0006\u0010\f\u001a\u00020\rJ\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u000f2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\u000fH\u0002J\u0018\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\u001aH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/callnote/app/data/seed/DatabaseSeeder;", "", "categoryDao", "Lcom/callnote/app/data/db/dao/CallCategoryDao;", "recordDao", "Lcom/callnote/app/data/db/dao/CallRecordDao;", "taskDao", "Lcom/callnote/app/data/db/dao/TranscriptionTaskDao;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "<init>", "(Lcom/callnote/app/data/db/dao/CallCategoryDao;Lcom/callnote/app/data/db/dao/CallRecordDao;Lcom/callnote/app/data/db/dao/TranscriptionTaskDao;Lkotlinx/coroutines/CoroutineScope;)V", "seedIfEmpty", "", "defaultCategories", "", "Lcom/callnote/app/data/db/entity/CallCategoryEntity;", "seedData", "Lcom/callnote/app/data/seed/DatabaseSeeder$SeedRecord;", "seedRecords", "Lcom/callnote/app/data/db/entity/CallRecordEntity;", "categoryIds", "", "estimateSize", "durationSeconds", "format", "", "SeedRecord", "app_debug"})
public final class DatabaseSeeder {
    @org.jetbrains.annotations.NotNull()
    private final com.callnote.app.data.db.dao.CallCategoryDao categoryDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.callnote.app.data.db.dao.CallRecordDao recordDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.callnote.app.data.db.dao.TranscriptionTaskDao taskDao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.callnote.app.data.db.entity.CallCategoryEntity> defaultCategories = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.callnote.app.data.seed.DatabaseSeeder.SeedRecord> seedData = null;
    
    @javax.inject.Inject()
    public DatabaseSeeder(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.dao.CallCategoryDao categoryDao, @org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.dao.CallRecordDao recordDao, @org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.dao.TranscriptionTaskDao taskDao, @com.callnote.app.di.ApplicationScope()
    @org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.CoroutineScope scope) {
        super();
    }
    
    public final void seedIfEmpty() {
    }
    
    private final java.util.List<com.callnote.app.data.db.entity.CallRecordEntity> seedRecords(java.util.List<java.lang.Long> categoryIds) {
        return null;
    }
    
    private final long estimateSize(long durationSeconds, java.lang.String format) {
        return 0L;
    }
    
    /**
     * 分类在列表中的位置（1 起），对应插入后的 categoryId
     */
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b%\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0082\b\u0018\u00002\u00020\u0001Bq\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\u0004\b\u0013\u0010\u0014J\t\u0010)\u001a\u00020\u0003H\u00c6\u0003J\t\u0010*\u001a\u00020\u0005H\u00c6\u0003J\t\u0010+\u001a\u00020\u0005H\u00c6\u0003J\t\u0010,\u001a\u00020\u0005H\u00c6\u0003J\t\u0010-\u001a\u00020\tH\u00c6\u0003J\t\u0010.\u001a\u00020\u0003H\u00c6\u0003J\t\u0010/\u001a\u00020\fH\u00c6\u0003J\u0010\u00100\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010!J\u000b\u00101\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u00102\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u00103\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u00104\u001a\u0004\u0018\u00010\u0012H\u00c6\u0003\u00a2\u0006\u0002\u0010\'J\u0090\u0001\u00105\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u00c6\u0001\u00a2\u0006\u0002\u00106J\u0013\u00107\u001a\u0002082\b\u00109\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010:\u001a\u00020\u0005H\u00d6\u0001J\t\u0010;\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0018R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0018R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0015\u0010\r\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\"\u001a\u0004\b \u0010!R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0016R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0016R\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0016R\u0015\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\n\n\u0002\u0010(\u001a\u0004\b&\u0010\'\u00a8\u0006<"}, d2 = {"Lcom/callnote/app/data/seed/DatabaseSeeder$SeedRecord;", "", "phone", "", "daysAgo", "", "hour", "minute", "durationSeconds", "", "format", "status", "Lcom/callnote/app/data/model/TranscriptionStatus;", "categoryPos", "text", "summary", "keywords", "score", "", "<init>", "(Ljava/lang/String;IIIJLjava/lang/String;Lcom/callnote/app/data/model/TranscriptionStatus;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Float;)V", "getPhone", "()Ljava/lang/String;", "getDaysAgo", "()I", "getHour", "getMinute", "getDurationSeconds", "()J", "getFormat", "getStatus", "()Lcom/callnote/app/data/model/TranscriptionStatus;", "getCategoryPos", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getText", "getSummary", "getKeywords", "getScore", "()Ljava/lang/Float;", "Ljava/lang/Float;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "copy", "(Ljava/lang/String;IIIJLjava/lang/String;Lcom/callnote/app/data/model/TranscriptionStatus;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Float;)Lcom/callnote/app/data/seed/DatabaseSeeder$SeedRecord;", "equals", "", "other", "hashCode", "toString", "app_debug"})
    static final class SeedRecord {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String phone = null;
        private final int daysAgo = 0;
        private final int hour = 0;
        private final int minute = 0;
        private final long durationSeconds = 0L;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String format = null;
        @org.jetbrains.annotations.NotNull()
        private final com.callnote.app.data.model.TranscriptionStatus status = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer categoryPos = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String text = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String summary = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String keywords = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Float score = null;
        
        public SeedRecord(@org.jetbrains.annotations.NotNull()
        java.lang.String phone, int daysAgo, int hour, int minute, long durationSeconds, @org.jetbrains.annotations.NotNull()
        java.lang.String format, @org.jetbrains.annotations.NotNull()
        com.callnote.app.data.model.TranscriptionStatus status, @org.jetbrains.annotations.Nullable()
        java.lang.Integer categoryPos, @org.jetbrains.annotations.Nullable()
        java.lang.String text, @org.jetbrains.annotations.Nullable()
        java.lang.String summary, @org.jetbrains.annotations.Nullable()
        java.lang.String keywords, @org.jetbrains.annotations.Nullable()
        java.lang.Float score) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhone() {
            return null;
        }
        
        public final int getDaysAgo() {
            return 0;
        }
        
        public final int getHour() {
            return 0;
        }
        
        public final int getMinute() {
            return 0;
        }
        
        public final long getDurationSeconds() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getFormat() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.callnote.app.data.model.TranscriptionStatus getStatus() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getCategoryPos() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getText() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getSummary() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getKeywords() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Float getScore() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component10() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component11() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Float component12() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final long component5() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.callnote.app.data.model.TranscriptionStatus component7() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component8() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component9() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.callnote.app.data.seed.DatabaseSeeder.SeedRecord copy(@org.jetbrains.annotations.NotNull()
        java.lang.String phone, int daysAgo, int hour, int minute, long durationSeconds, @org.jetbrains.annotations.NotNull()
        java.lang.String format, @org.jetbrains.annotations.NotNull()
        com.callnote.app.data.model.TranscriptionStatus status, @org.jetbrains.annotations.Nullable()
        java.lang.Integer categoryPos, @org.jetbrains.annotations.Nullable()
        java.lang.String text, @org.jetbrains.annotations.Nullable()
        java.lang.String summary, @org.jetbrains.annotations.Nullable()
        java.lang.String keywords, @org.jetbrains.annotations.Nullable()
        java.lang.Float score) {
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
    }
}