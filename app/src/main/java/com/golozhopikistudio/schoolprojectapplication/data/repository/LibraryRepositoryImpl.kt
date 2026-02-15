package com.golozhopikistudio.schoolprojectapplication.data.repository

import com.golozhopikistudio.schoolprojectapplication.data.local.AppState
import com.golozhopikistudio.schoolprojectapplication.data.local.JsonStore
import com.golozhopikistudio.schoolprojectapplication.data.remote.OpenLibraryApi
import com.golozhopikistudio.schoolprojectapplication.data.remote.mapper.toDomainBook

import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role
import com.golozhopikistudio.schoolprojectapplication.domain.model.User
import com.golozhopikistudio.schoolprojectapplication.domain.result.BorrowResult
import com.golozhopikistudio.schoolprojectapplication.domain.result.ReturnResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LibraryRepositoryImpl(
    private val jsonStore: JsonStore,
    private val api: OpenLibraryApi
) : LibraryRepository {

    private val _state = MutableStateFlow(jsonStore.load())
    override val state: StateFlow<AppState> = _state.asStateFlow()

    private fun updateState(newState: AppState) {
        _state.value = newState
        jsonStore.save(newState)
    }

    override fun setActiveUser(name: String, role: Role) {
        val newUser = User(name = name, role = role)

        updateState(
            state.value.copy(
                activeUser = newUser,
                users = state.value.users + newUser
            )
        )
    }

    override fun addManualBook(title: String, author: String) {
        val newBook = Book(
            id = System.currentTimeMillis().toString(),
            title = title,
            author = author,
            isBorrowed = false
        )

        updateState(
            state.value.copy(
                books = state.value.books + newBook
            )
        )
    }

    override fun borrow(bookId: String): BorrowResult {
        val current = state.value
        val book = current.books.find { it.id == bookId }
            ?: return BorrowResult.NotFound

        if (book.isBorrowed)
            return BorrowResult.AlreadyBorrowed

        val updatedBook = book.copy(isBorrowed = true)

        updateState(
            current.copy(
                books = current.books.map {
                    if (it.id == bookId) updatedBook else it
                }
            )
        )

        return BorrowResult.Success
    }

    override fun returnBook(bookId: String): ReturnResult {
        val current = state.value
        val book = current.books.find { it.id == bookId }
            ?: return ReturnResult.NotFound

        if (!book.isBorrowed)
            return ReturnResult.NotBorrowed

        val updatedBook = book.copy(isBorrowed = false)

        updateState(
            current.copy(
                books = current.books.map {
                    if (it.id == bookId) updatedBook else it
                }
            )
        )

        return ReturnResult.Success
    }

    override suspend fun importFromOpenLibrary(query: String) {
        val response = api.search(query)
        val books = response.docs.map { it.toDomainBook() }

        updateState(
            state.value.copy(
                books = state.value.books + books
            )
        )
    }
}