package com.golozhopikistudio.schoolprojectapplication.features.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CatalogViewModel(private val repository: LibraryRepository) : ViewModel() {

    private val query = MutableStateFlow("")
    val state: StateFlow<CatalogUiState> =
        combine(repository.state, query) { appState, searchQuery ->
            val filteredBooks = appState.books.filter {
                val q = searchQuery.trim()
                q.isBlank() || it.title.contains(q, ignoreCase = true) || it.author.contains(
                    q,
                    ignoreCase = true
                )
            }
            CatalogUiState(
                query = searchQuery,
                books = filteredBooks,
                canAddBook = appState.users
                    .find { it.id == appState.activeUserId }
                    ?.role == Role.LIBRARIAN
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CatalogUiState()
        )

    fun onQueryChange(value: String) {
        query.update { value }
    }

    fun addManualBook(title: String, author: String) {
        val normalizedTitle = title.trim()
        val normalizedAuthor = author.trim()
        if (normalizedTitle.isBlank() || normalizedAuthor.isBlank()) return
        repository.addManualBook(title = normalizedTitle, author = normalizedAuthor)
    }

}

data class CatalogUiState(
    val query: String = "",
    val books: List<Book> = emptyList(),
    val canAddBook: Boolean = false
)