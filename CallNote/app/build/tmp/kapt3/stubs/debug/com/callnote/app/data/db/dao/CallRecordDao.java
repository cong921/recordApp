package com.callnote.app.data.db.dao;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\r\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\"\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\bH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\r\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0014\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\b0\u0012H\'J\u001c\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\b0\u00122\u0006\u0010\u0015\u001a\u00020\u0003H\'J\u001c\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\b0\u00122\u0006\u0010\u0017\u001a\u00020\u000fH\'J\u0018\u0010\u0018\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u00122\u0006\u0010\u0019\u001a\u00020\u0003H\'J\u000e\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0012H\'J\u001c\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\b0\u00122\u0006\u0010\u001d\u001a\u00020\u001eH\'J(\u0010\u001f\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\u00032\b\u0010\u0015\u001a\u0004\u0018\u00010\u00032\u0006\u0010 \u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010!J>\u0010\"\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\u00032\u0006\u0010#\u001a\u00020\u001e2\u0006\u0010$\u001a\u00020\u001e2\u0006\u0010%\u001a\u00020\u001e2\u0006\u0010&\u001a\u00020\u001e2\u0006\u0010 \u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\'J&\u0010(\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\u00032\u0006\u0010)\u001a\u00020\u001e2\u0006\u0010 \u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010*\u00a8\u0006+"}, d2 = {"Lcom/callnote/app/data/db/dao/CallRecordDao;", "", "insert", "", "record", "Lcom/callnote/app/data/db/entity/CallRecordEntity;", "(Lcom/callnote/app/data/db/entity/CallRecordEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertAll", "", "records", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "delete", "count", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeAllWithCategory", "Lkotlinx/coroutines/flow/Flow;", "Lcom/callnote/app/data/db/entity/RecordWithCategory;", "observeByCategoryWithCategory", "categoryId", "observeRecentWithCategory", "limit", "observeById", "id", "observeStats", "Lcom/callnote/app/data/db/pojo/RecordStats;", "search", "query", "", "updateCategory", "updatedAt", "(JLjava/lang/Long;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateTranscription", "text", "summary", "keywords", "status", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markAudioDeleted", "placeholderUri", "(JLjava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface CallRecordDao {
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.entity.CallRecordEntity record, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.callnote.app.data.db.entity.CallRecordEntity> records, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.Long>> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.entity.CallRecordEntity record, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.entity.CallRecordEntity record, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM call_record")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object count(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Transaction()
    @androidx.room.Query(value = "SELECT * FROM call_record ORDER BY callTime DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.entity.RecordWithCategory>> observeAllWithCategory();
    
    @androidx.room.Transaction()
    @androidx.room.Query(value = "SELECT * FROM call_record WHERE categoryId = :categoryId ORDER BY callTime DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.entity.RecordWithCategory>> observeByCategoryWithCategory(long categoryId);
    
    @androidx.room.Transaction()
    @androidx.room.Query(value = "SELECT * FROM call_record ORDER BY callTime DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.entity.RecordWithCategory>> observeRecentWithCategory(int limit);
    
    @androidx.room.Query(value = "SELECT * FROM call_record WHERE id = :id")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.callnote.app.data.db.entity.CallRecordEntity> observeById(long id);
    
    @androidx.room.Query(value = "\n        SELECT COUNT(*) AS totalCount,\n               COALESCE(SUM(CASE WHEN transcriptionStatus = \'COMPLETED\' THEN 1 ELSE 0 END), 0) AS completedCount,\n               COALESCE(SUM(CASE WHEN transcriptionStatus IN (\'PENDING\', \'PROCESSING\') THEN 1 ELSE 0 END), 0) AS pendingCount,\n               COALESCE(SUM(CASE WHEN transcriptionStatus = \'FAILED\' THEN 1 ELSE 0 END), 0) AS failedCount,\n               COALESCE(SUM(duration), 0) AS totalDuration,\n               COALESCE(SUM(fileSize), 0) AS totalSize\n        FROM call_record\n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.callnote.app.data.db.pojo.RecordStats> observeStats();
    
    @androidx.room.Query(value = "\n        SELECT * FROM call_record\n        WHERE fileName LIKE \'%\' || :query || \'%\'\n           OR transcriptionText LIKE \'%\' || :query || \'%\'\n           OR summary LIKE \'%\' || :query || \'%\'\n           OR keywords LIKE \'%\' || :query || \'%\'\n        ORDER BY callTime DESC\n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.entity.CallRecordEntity>> search(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @androidx.room.Query(value = "UPDATE call_record SET categoryId = :categoryId, updatedAt = :updatedAt WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateCategory(long id, @org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, long updatedAt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "\n        UPDATE call_record\n        SET transcriptionText = :text, summary = :summary, keywords = :keywords,\n            transcriptionStatus = :status, updatedAt = :updatedAt\n        WHERE id = :id\n        ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateTranscription(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.lang.String summary, @org.jetbrains.annotations.NotNull()
    java.lang.String keywords, @org.jetbrains.annotations.NotNull()
    java.lang.String status, long updatedAt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE call_record SET localPath = NULL, fileUri = :placeholderUri, fileSize = 0, updatedAt = :updatedAt WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object markAudioDeleted(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String placeholderUri, long updatedAt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}