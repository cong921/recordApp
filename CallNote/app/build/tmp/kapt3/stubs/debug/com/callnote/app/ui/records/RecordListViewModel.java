package com.callnote.app.ui.records;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\fJ\u000e\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006\u0018"}, d2 = {"Lcom/callnote/app/ui/records/RecordListViewModel;", "Landroidx/lifecycle/ViewModel;", "recordRepository", "Lcom/callnote/app/data/repository/CallRecordRepository;", "savedStateHandle", "Landroidx/lifecycle/SavedStateHandle;", "<init>", "(Lcom/callnote/app/data/repository/CallRecordRepository;Landroidx/lifecycle/SavedStateHandle;)V", "categoryId", "", "query", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "filter", "Lcom/callnote/app/ui/records/RecordFilter;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/callnote/app/ui/records/RecordListUiState;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "onQueryChange", "", "value", "onFilterChange", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class RecordListViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.callnote.app.data.repository.CallRecordRepository recordRepository = null;
    private final long categoryId = 0L;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> query = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.callnote.app.ui.records.RecordFilter> filter = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.callnote.app.ui.records.RecordListUiState> uiState = null;
    
    @javax.inject.Inject()
    public RecordListViewModel(@org.jetbrains.annotations.NotNull()
    com.callnote.app.data.repository.CallRecordRepository recordRepository, @org.jetbrains.annotations.NotNull()
    androidx.lifecycle.SavedStateHandle savedStateHandle) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.callnote.app.ui.records.RecordListUiState> getUiState() {
        return null;
    }
    
    public final void onQueryChange(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void onFilterChange(@org.jetbrains.annotations.NotNull()
    com.callnote.app.ui.records.RecordFilter value) {
    }
}