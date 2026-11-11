package com.callnote.app.data.db.entity;

/**
 * 录音及其分类（未分类时为 null）
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u001f\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0016"}, d2 = {"Lcom/callnote/app/data/db/entity/RecordWithCategory;", "", "record", "Lcom/callnote/app/data/db/entity/CallRecordEntity;", "category", "Lcom/callnote/app/data/db/entity/CallCategoryEntity;", "<init>", "(Lcom/callnote/app/data/db/entity/CallRecordEntity;Lcom/callnote/app/data/db/entity/CallCategoryEntity;)V", "getRecord", "()Lcom/callnote/app/data/db/entity/CallRecordEntity;", "getCategory", "()Lcom/callnote/app/data/db/entity/CallCategoryEntity;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
public final class RecordWithCategory {
    @androidx.room.Embedded()
    @org.jetbrains.annotations.NotNull()
    private final com.callnote.app.data.db.entity.CallRecordEntity record = null;
    @androidx.room.Relation(parentColumn = "categoryId", entityColumn = "id")
    @org.jetbrains.annotations.Nullable()
    private final com.callnote.app.data.db.entity.CallCategoryEntity category = null;
    
    public RecordWithCategory(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.entity.CallRecordEntity record, @org.jetbrains.annotations.Nullable()
    com.callnote.app.data.db.entity.CallCategoryEntity category) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.callnote.app.data.db.entity.CallRecordEntity getRecord() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.callnote.app.data.db.entity.CallCategoryEntity getCategory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.callnote.app.data.db.entity.CallRecordEntity component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.callnote.app.data.db.entity.CallCategoryEntity component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.callnote.app.data.db.entity.RecordWithCategory copy(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.entity.CallRecordEntity record, @org.jetbrains.annotations.Nullable()
    com.callnote.app.data.db.entity.CallCategoryEntity category) {
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