package com.golozhopikistudio.schoolprojectapplication.features.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.HistoryItem
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class JournalViewModel(private val repository: LibraryRepository) : ViewModel() {

    val state: StateFlow<JournalUiState> = repository.state.map {
        val isLibrarian = it.users
            .find { user -> user.id == it.activeUserId }
            ?.role == Role.LIBRARIAN

        JournalUiState(
            history = it.history,
            isLibrarian = isLibrarian
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = JournalUiState()
    )

    fun clearHistory() {
        repository.clearHistory()
    }
}

data class JournalUiState(
    val history: List<HistoryItem> = emptyList(),
    val isLibrarian: Boolean = false
)