package com.callnote.app.ui.components;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u001a\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u001a:\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u001e\b\u0002\u0010\t\u001a\u0018\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n\u00a2\u0006\u0002\b\f\u00a2\u0006\u0002\b\rH\u0007\u001a*\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u00a8\u0006\u0013"}, d2 = {"StatusBadge", "", "statusName", "", "modifier", "Landroidx/compose/ui/Modifier;", "RecordListItem", "item", "Lcom/callnote/app/data/db/entity/RecordWithCategory;", "trailing", "Lkotlin/Function1;", "Landroidx/compose/foundation/layout/RowScope;", "Landroidx/compose/runtime/Composable;", "Lkotlin/ExtensionFunctionType;", "EmptyState", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "title", "subtitle", "app_debug"})
public final class CommonComponentsKt {
    
    /**
     * 转写状态徽章
     */
    @androidx.compose.runtime.Composable()
    public static final void StatusBadge(@org.jetbrains.annotations.NotNull()
    java.lang.String statusName, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    /**
     * 录音列表项：分类图标 + 文件名 + 摘要 + 可选尾部内容（状态徽章等）
     */
    @androidx.compose.runtime.Composable()
    public static final void RecordListItem(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.db.entity.RecordWithCategory item, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    androidx.compose.runtime.internal.ComposableFunction1<? super androidx.compose.foundation.layout.RowScope, kotlin.Unit> trailing) {
    }
    
    /**
     * 空状态占位
     */
    @androidx.compose.runtime.Composable()
    public static final void EmptyState(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.graphics.vector.ImageVector icon, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String subtitle, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}