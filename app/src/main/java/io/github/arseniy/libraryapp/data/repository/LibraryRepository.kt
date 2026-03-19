package io.github.arseniy.libraryapp.data.repository


import io.github.arseniy.libraryapp.data.local.AppState
import io.github.arseniy.libraryapp.domain.model.Role
import io.github.arseniy.libraryapp.domain.result.BorrowResult
import io.github.arseniy.libraryapp.domain.result.ImportResult
import io.github.arseniy.libraryapp.domain.result.ReturnResult
import kotlinx.coroutines.flow.StateFlow

interface LibraryRepository {

    val state: StateFlow<AppState>

    fun setActiveUser(name: String, role: Role)

    fun addManualBook(title: String, author: String)

    fun borrow(bookId: String): BorrowResult

    fun returnBook(bookId: String): ReturnResult

    fun deleteUser(userId: String)

    suspend fun importFromOpenLibrary(query: String, limit: Int): ImportResult

    suspend fun deleteBook(bookId: String)

    fun clearHistory()

    fun clearCatalog()

}