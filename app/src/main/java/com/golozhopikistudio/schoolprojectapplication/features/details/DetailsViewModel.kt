package com.golozhopikistudio.schoolprojectapplication.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val selectedBookId = MutableStateFlow<String?>(null)
    private val errorMessage = MutableStateFlow<String?>(null)

    val state: StateFlow<DetailsUiState> =
        combine(repository.state, selectedBookId, errorMessage) { appState, bookId, error ->
            val book = appState.books.find { it.id == bookId }
            val emptyMessage = when {
                bookId == null -> null
                book == null -> "Книга не найдена или уже удалена"
                else -> null
            }

            DetailsUiState(
                book = book,
                isLoading = bookId == null,
                errorMessage = error,
                emptyMessage = emptyMessage
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailsUiState()
        )

    private val _effect = MutableSharedFlow<DetailsEffect>()
    val effect = _effect.asSharedFlow()

    fun load(bookId: String) {
        errorMessage.update { null }
        selectedBookId.update { bookId }
    }

    fun onAction(action: DetailsAction) {
        when (action) {
            DetailsAction.DeleteClicked -> deleteBook()
        }
    }

    private fun deleteBook() = viewModelScope.launch {
        val bookId = selectedBookId.value ?: return@launch

        runCatching {
            repository.deleteBook(bookId)
        }.onSuccess {
            _effect.emit(DetailsEffect.ShowToast("Книга удалена"))
            _effect.emit(DetailsEffect.NavigateBack)
        }.onFailure {
            val message = it.message ?: "Не удалось удалить книгу"
            errorMessage.update { message }
        }
    }
}


data class DetailsUiState(
    val book: Book? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val emptyMessage: String? = null
)

sealed interface DetailsEffect {
    data class ShowToast(val message: String) : DetailsEffect
    data object NavigateBack : DetailsEffect
}

sealed interface DetailsAction {
    data object DeleteClicked : DetailsAction
}