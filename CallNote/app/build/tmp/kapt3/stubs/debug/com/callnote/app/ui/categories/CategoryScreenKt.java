package com.callnote.app.ui.categories;

@kotlin.Metadata(mv = {2, 1, 0}, k = 2, xi = 48, d1 = {"\u0000J\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u001a&\u0010\u0000\u001a\u00020\u00012\u0012\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u0007\u001a\u0010\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\tH\u0003\u001a*\u0010\n\u001a\u00020\u00012\b\b\u0002\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\u0003\u001a\u001e\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u0016H\u0003\u001a\u0010\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u0019H\u0003\u00a8\u0006\u001a"}, d2 = {"CategoryScreen", "", "onCategoryClick", "Lkotlin/Function1;", "", "viewModel", "Lcom/callnote/app/ui/categories/CategoryViewModel;", "OverviewCard", "uiState", "Lcom/callnote/app/ui/categories/CategoryUiState;", "OverviewItem", "modifier", "Landroidx/compose/ui/Modifier;", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "label", "", "value", "CategoryCard", "category", "Lcom/callnote/app/data/db/pojo/CategoryStats;", "onClick", "Lkotlin/Function0;", "UncategorizedCard", "count", "", "app_debug"})
public final class CategoryScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void CategoryScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onCategoryClick, @org.jetbrains.annotations.NotNull()
    com.callnote.app.ui.categories.CategoryViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void OverviewCard(com.callnote.app.ui.categories.CategoryUiState uiState) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void OverviewItem(androidx.compose.ui.Modifier modifier, androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String label, java.lang.String value) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    private static final void CategoryCard(com.callnote.app.data.db.pojo.CategoryStats category, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void UncategorizedCard(int count) {
    }
}