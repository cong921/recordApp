package com.callnote.app.data.repository;

/**
 * 录音数据仓库：UI 层唯一的数据入口
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0007\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB\u0011\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007J\u001c\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\u00072\b\b\u0002\u0010\f\u001a\u00020\rJ\u001c\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\u00072\b\b\u0002\u0010\u000f\u001a\u00020\u0010J\u0016\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00120\u00072\u0006\u0010\f\u001a\u00020\rJ\u001a\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\n0\u00072\u0006\u0010\u0015\u001a\u00020\u0012J \u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0086@\u00a2\u0006\u0002\u0010\u0019R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/callnote/app/data/repository/CallRecordRepository;", "", "dao", "Lcom/callnote/app/data/db/dao/CallRecordDao;", "<init>", "(Lcom/callnote/app/data/db/dao/CallRecordDao;)V", "observeStats", "Lkotlinx/coroutines/flow/Flow;", "Lcom/callnote/app/data/db/pojo/RecordStats;", "observeRecords", "", "Lcom/callnote/app/data/db/entity/RecordWithCategory;", "categoryId", "", "observeRecent", "limit", "", "observeCategoryName", "", "search", "Lcom/callnote/app/data/db/entity/CallRecordEntity;", "query", "updateCategory", "", "recordId", "(JLjava/lang/Long;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class CallRecordRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.callnote.app.data.db.dao.CallRecordDao dao = null;
    public static final long ALL = -1L;
    @org.jetbrains.annotations.NotNull()
    public static final com.callnote.app.data.repository.CallRecordRepository.Companion Companion = null;
    
    @javax.inject.Inject()
    public CallRecordRepository(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.dao.CallRecordDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.callnote.app.data.db.pojo.RecordStats> observeStats() {
        return null;
    }
    
    /**
     * categoryId <= 0 表示查询全部
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.entity.RecordWithCategory>> observeRecords(long categoryId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.entity.RecordWithCategory>> observeRecent(int limit) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> observeCategoryName(long categoryId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.entity.CallRecordEntity>> search(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateCategory(long recordId, @org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/callnote/app/data/repository/CallRecordRepository$Companion;", "", "<init>", "()V", "ALL", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}