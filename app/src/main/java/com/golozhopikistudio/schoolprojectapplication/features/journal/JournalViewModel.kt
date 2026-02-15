package com.golozhopikistudio.schoolprojectapplication.features.journal

import androidx.lifecycle.ViewModel
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class JournalViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(JournalUiState())
    val state: StateFlow<JournalUiState> = _state.asStateFlow()

    fun load() {
        _state.update {
            it.copy(history = repository.getHistory())
        }
    }

    fun clear() {
        repository.clearHistory()
        load()
    }
}

data class JournalUiState(
    val history: List<HistoryItem> = emptyList()
)
