package com.golozhopikistudio.schoolprojectapplication.features.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CatalogEffect>()
    val effect: SharedFlow<CatalogEffect> = _effect.asSharedFlow()

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun search() = viewModelScope.launch {
        val q = state.value.query.trim()
        if (q.isEmpty()) return@launch

        _state.update { it.copy(isLoading = true, error = null) }

        runCatching { repository.search(q) }
            .onSuccess { books ->
                _state.update { it.copy(books = books, isLoading = false) }
            }
            .onFailure {
                _state.update { it.copy(isLoading = false, error = "Ошибка загрузки") }
                _effect.emit(CatalogEffect.ShowToast("Не удалось выполнить поиск"))
            }
    }

    fun onBookClick(book: Book) = viewModelScope.launch {
        _effect.emit(CatalogEffect.OpenDetails(book.id))
    }
}

data class CatalogUiState(
    val query: String = "",
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface CatalogEffect {
    data class OpenDetails(val bookId: String) : CatalogEffect
    data class ShowToast(val message: String) : CatalogEffect
}


