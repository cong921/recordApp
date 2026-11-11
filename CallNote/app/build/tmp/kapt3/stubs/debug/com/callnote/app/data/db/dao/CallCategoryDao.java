package com.callnote.app.data.db.dao;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bg\u0018\u00002\u00020\u0001J\"\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u0016\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00030\u000eH\'J\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0010\u001a\u00020\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u0018\u0010\u0012\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00130\u000e2\u0006\u0010\u0010\u001a\u00020\u0004H\'J\u000e\u0010\u0014\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u0014\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u00030\u000eH\'\u00a8\u0006\u0019"}, d2 = {"Lcom/callnote/app/data/db/dao/CallCategoryDao;", "", "insertAll", "", "", "categories", "Lcom/callnote/app/data/db/entity/CallCategoryEntity;", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "category", "(Lcom/callnote/app/data/db/entity/CallCategoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "getById", "id", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeCategoryName", "", "count", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeCategoryStats", "Lcom/callnote/app/data/db/pojo/CategoryStats;", "app_debug"})
@androidx.room.Dao()
public abstract interface CallCategoryDao {
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.callnote.app.data.db.entity.CallCategoryEntity> categories, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.Long>> $completion);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.entity.CallCategoryEntity category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.entity.CallCategoryEntity category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM call_category ORDER BY sort ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.entity.CallCategoryEntity>> observeAll();
    
    @androidx.room.Query(value = "SELECT * FROM call_category WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.callnote.app.data.db.entity.CallCategoryEntity> $completion);
    
    @androidx.room.Query(value = "SELECT categoryName FROM call_category WHERE id = :id")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.String> observeCategoryName(long id);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM call_category")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object count(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "\n        SELECT c.id AS id,\n               c.categoryName AS categoryName,\n               c.categoryCode AS categoryCode,\n               COUNT(r.id) AS recordCount,\n               COALESCE(SUM(r.fileSize), 0) AS totalSize,\n               COALESCE(SUM(r.duration), 0) AS totalDuration,\n               MAX(r.callTime) AS lastCallTime\n        FROM call_category c\n        LEFT JOIN call_record r ON r.categoryId = c.id\n        GROUP BY c.id\n        ORDER BY c.sort ASC\n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.callnote.app.data.db.pojo.CategoryStats>> observeCategoryStats();
}