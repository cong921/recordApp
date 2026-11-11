package com.callnote.app.ui.categories;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\r"}, d2 = {"Lcom/callnote/app/ui/categories/CategoryViewModel;", "Landroidx/lifecycle/ViewModel;", "recordRepository", "Lcom/callnote/app/data/repository/CallRecordRepository;", "categoryRepository", "Lcom/callnote/app/data/repository/CallCategoryRepository;", "<init>", "(Lcom/callnote/app/data/repository/CallRecordRepository;Lcom/callnote/app/data/repository/CallCategoryRepository;)V", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/callnote/app/ui/categories/CategoryUiState;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class CategoryViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.callnote.app.ui.categories.CategoryUiState> uiState = null;
    
    @javax.inject.Inject()
    public CategoryViewModel(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.repository.CallRecordRepository recordRepository, @org.jetbrains.annotations.NotNull()
    com.callnote.app.data.repository.CallCategoryRepository categoryRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.callnote.app.ui.categories.CategoryUiState> getUiState() {
        return null;
    }
}