package com.golozhopikistudio.schoolprojectapplication.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import com.golozhopikistudio.schoolprojectapplication.domain.result.BorrowResult
import com.golozhopikistudio.schoolprojectapplication.domain.result.ReturnResult
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
    val state: StateFlow<DetailsUiState> =
        combine(repository.state, selectedBookId) { appState, bookId ->
            val book = appState.books.find { it.id == bookId }
            DetailsUiState(book = book, activeUserId = appState.activeUserId)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailsUiState()
        )

    private val _effect = MutableSharedFlow<DetailsEffect>()
    val effect = _effect.asSharedFlow()

    fun load(bookId: String) {
        selectedBookId.update { bookId }
    }

    fun borrow() = viewModelScope.launch {
        val bookId = selectedBookId.value ?: return@launch
        when (repository.borrow(bookId)) {
            BorrowResult.Success -> _effect.emit(DetailsEffect.ShowToast("Книга выдана"))
            BorrowResult.NotFound -> _effect.emit(DetailsEffect.ShowToast("Книга не найдена"))
            BorrowResult.AlreadyBorrowed -> _effect.emit(DetailsEffect.ShowToast("Книга уже выдана"))
            BorrowResult.NotAllowed -> _effect.emit(DetailsEffect.ShowToast("Нет активного пользователя"))
        }
    }

    fun returnBook() = viewModelScope.launch {
        val bookId = selectedBookId.value ?: return@launch
        when (repository.returnBook(bookId)) {
            ReturnResult.Success -> _effect.emit(DetailsEffect.ShowToast("Книга возвращена"))
            ReturnResult.NotFound -> _effect.emit(DetailsEffect.ShowToast("Книга не найдена"))
            ReturnResult.NotBorrowed -> _effect.emit(DetailsEffect.ShowToast("Книга уже в библиотеке"))
            ReturnResult.NotAllowed -> _effect.emit(DetailsEffect.ShowToast("Книгу выдал другой пользователь"))
        }
    }
}


data class DetailsUiState(
    val book: Book? = null,
    val activeUserId: String? = null
)

sealed interface DetailsEffect {
    data class ShowToast(val message: String) : DetailsEffect

}