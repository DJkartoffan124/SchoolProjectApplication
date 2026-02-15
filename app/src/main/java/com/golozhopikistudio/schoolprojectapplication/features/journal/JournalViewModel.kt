package com.golozhopikistudio.schoolprojectapplication.features.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.HistoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class JournalViewModel(repository: LibraryRepository) : ViewModel() {

    val state: StateFlow<JournalUiState> = repository.state.map {
        JournalUiState(history = it.history)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = JournalUiState()
    )
}

data class JournalUiState(
    val history: List<HistoryItem> = emptyList()
)
