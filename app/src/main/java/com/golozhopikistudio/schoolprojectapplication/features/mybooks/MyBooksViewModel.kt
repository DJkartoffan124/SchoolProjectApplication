package com.golozhopikistudio.schoolprojectapplication.features.mybooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyBooksViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyBooksUiState())
    val state: StateFlow<MyBooksUiState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        val books = repository.getMyBooks()
        _state.update { it.copy(books = books, isLoading = false) }
    }

    fun returnBook(book: Book) = viewModelScope.launch {
        repository.returnBook(book)
        load()
    }
}


data class MyBooksUiState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false
)
