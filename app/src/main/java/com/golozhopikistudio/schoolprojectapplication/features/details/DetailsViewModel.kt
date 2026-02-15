package com.golozhopikistudio.schoolprojectapplication.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import com.golozhopikistudio.schoolprojectapplication.domain.result.BorrowResult
import com.golozhopikistudio.schoolprojectapplication.domain.result.ReturnResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsUiState())
    val state: StateFlow<DetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DetailsEffect>()
    val effect: SharedFlow<DetailsEffect> = _effect.asSharedFlow()

    fun load(bookId: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        runCatching { repository.getBook(bookId) }
            .onSuccess { book ->
                _state.update { it.copy(book = book, isLoading = false) }
            }
            .onFailure {
                _state.update { it.copy(isLoading = false, error = "Ошибка загрузки") }
            }
    }

    fun borrow() = viewModelScope.launch {
        val book = state.value.book ?: return@launch
        _state.update { it.copy(actionLoading = true) }

        val result = repository.borrow(book)

        _state.update { it.copy(actionLoading = false) }

        when (result) {
            is BorrowResult.Success ->
                _effect.emit(DetailsEffect.ShowToast("Книга выдана"))

            is BorrowResult.Error ->
                _effect.emit(DetailsEffect.ShowToast(result.message))
        }
    }

    fun returnBook() = viewModelScope.launch {
        val book = state.value.book ?: return@launch
        val result = repository.returnBook(book)

        when (result) {
            is ReturnResult.Success ->
                _effect.emit(DetailsEffect.ShowToast("Книга возвращена"))

            is ReturnResult.Error ->
                _effect.emit(DetailsEffect.ShowToast(result.message))
        }
    }
}


data class DetailsUiState(
    val book: Book? = null,
    val isLoading: Boolean = false,
    val actionLoading: Boolean = false,
    val error: String? = null
)

sealed interface DetailsEffect {
    data class ShowToast(val message: String) : DetailsEffect
    data object CloseScreen : DetailsEffect
}
