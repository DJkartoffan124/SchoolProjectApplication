package io.github.arseniy.libraryapp.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role
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
                emptyMessage = emptyMessage,
                activeUserId = appState.activeUserId,
                isLibrarian = appState.users
                    .find { it.id == appState.activeUserId }
                    ?.role == Role.LIBRARIAN
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
            DetailsAction.BorrowClicked -> borrow()
            DetailsAction.DeleteClicked -> deleteBook()
            DetailsAction.ReturnClicked -> returnBook()
        }
    }

    private fun borrow() = viewModelScope.launch {
        val bookId = selectedBookId.value ?: return@launch
        when (repository.borrow(bookId)) {
            BorrowResult.Success -> _effect.emit(DetailsEffect.ShowToast("Книга выдана"))
            BorrowResult.NotFound -> _effect.emit(DetailsEffect.ShowToast("Книга не найдена"))
            BorrowResult.AlreadyBorrowed -> _effect.emit(DetailsEffect.ShowToast("Книга уже выдана"))
            BorrowResult.NotAllowed -> _effect.emit(DetailsEffect.ShowToast("Нет активного пользователя"))
        }
    }

    private fun returnBook() = viewModelScope.launch {
        val bookId = selectedBookId.value ?: return@launch
        when (repository.returnBook(bookId)) {
            ReturnResult.Success -> _effect.emit(DetailsEffect.ShowToast("Книга возвращена"))
            ReturnResult.NotFound -> _effect.emit(DetailsEffect.ShowToast("Книга не найдена"))
            ReturnResult.NotBorrowed -> _effect.emit(DetailsEffect.ShowToast("Книга уже в библиотеке"))
            ReturnResult.NotAllowed -> _effect.emit(DetailsEffect.ShowToast("Книгу выдал другой пользователь"))
        }
    }

    private fun deleteBook() = viewModelScope.launch {
        if (!state.value.isLibrarian) {
            _effect.emit(DetailsEffect.ShowToast("Недостаточно прав"))
            return@launch
        }

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
    val emptyMessage: String? = null,
    val activeUserId: String? = null,
    val isLibrarian: Boolean = false
)

sealed interface DetailsEffect {
    data class ShowToast(val message: String) : DetailsEffect
    data object NavigateBack : DetailsEffect
}

sealed interface DetailsAction {
    data object BorrowClicked : DetailsAction
    data object DeleteClicked : DetailsAction
    data object ReturnClicked : DetailsAction
}