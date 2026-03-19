package io.github.arseniy.libraryapp.features.mybooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.arseniy.libraryapp.data.repository.LibraryRepository
import io.github.arseniy.libraryapp.domain.model.Book
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MyBooksViewModel(repository: LibraryRepository) : ViewModel() {

    val state: StateFlow<MyBooksUiState> = repository.state.map { appState ->
        val userId = appState.activeUserId
        val myBooks = appState.books.filter { it.borrowerId == userId }
        MyBooksUiState(books = myBooks)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MyBooksUiState()
    )
}


data class MyBooksUiState(
    val books: List<Book> = emptyList()
)
