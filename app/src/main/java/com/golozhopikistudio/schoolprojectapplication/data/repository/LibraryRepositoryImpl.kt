package com.golozhopikistudio.schoolprojectapplication.data.repository

import com.golozhopikistudio.schoolprojectapplication.data.local.AppState
import com.golozhopikistudio.schoolprojectapplication.data.local.JsonStore
import com.golozhopikistudio.schoolprojectapplication.data.remote.OpenLibraryApi
import com.golozhopikistudio.schoolprojectapplication.data.remote.mapper.toDomainBook

import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import com.golozhopikistudio.schoolprojectapplication.domain.model.HistoryItem
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role
import com.golozhopikistudio.schoolprojectapplication.domain.model.User
import com.golozhopikistudio.schoolprojectapplication.domain.result.BorrowResult
import com.golozhopikistudio.schoolprojectapplication.domain.result.ImportResult
import com.golozhopikistudio.schoolprojectapplication.domain.result.ReturnResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

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
        val current = state.value
        val existingUser = current.users.firstOrNull { it.name == name && it.role == role }
        val user = existingUser ?: User(id = UUID.randomUUID().toString(), name = name, role = role)


        updateState(
            current.copy(
                users = if (existingUser == null) current.users + user else current.users,
                activeUserId = user.id
            )
        )
    }

    override fun addManualBook(title: String, author: String) {

        val current = state.value
        val activeRole = current.users.find { it.id == current.activeUserId }?.role
        if (activeRole != Role.LIBRARIAN) return

        val newBook = Book(
            id = UUID.randomUUID().toString(),
            title = title,
            author = author,
            isBorrowed = false
        )

        updateState(
            current.copy(
                books = current.books + newBook
            )
        )
    }

    override fun borrow(bookId: String): BorrowResult {
        val current = state.value
        val activeUserId = current.activeUserId ?: return BorrowResult.NotAllowed
        val book = current.books.find { it.id == bookId } ?: return BorrowResult.NotFound

        if (book.isBorrowed) return BorrowResult.AlreadyBorrowed

        val updatedBook = book.copy(isBorrowed = true, borrowerId = activeUserId)
        val user = current.users.find { it.id == activeUserId }
        val historyItem = HistoryItem(
            time = System.currentTimeMillis(),
            message = "${user?.name ?: "Пользователь"} выдал(а) «${book.title}»"
        )

        updateState(
            current.copy(
                books = current.books.map { if (it.id == bookId) updatedBook else it },
                history = current.history + historyItem
            )
        )

        return BorrowResult.Success
    }

    override fun returnBook(bookId: String): ReturnResult {
        val current = state.value
        val activeUserId = current.activeUserId ?: return ReturnResult.NotAllowed
        val book = current.books.find { it.id == bookId } ?: return ReturnResult.NotFound

        if (!book.isBorrowed) return ReturnResult.NotBorrowed
        if (book.borrowerId != activeUserId) return ReturnResult.NotAllowed

        val updatedBook = book.copy(isBorrowed = false, borrowerId = null)
        val user = current.users.find { it.id == activeUserId }
        val historyItem = HistoryItem(
            time = System.currentTimeMillis(),
            message = "${user?.name ?: "Пользователь"} вернул(а) «${book.title}»"
        )

        updateState(
            current.copy(
                books = current.books.map { if (it.id == bookId) updatedBook else it },
                history = current.history + historyItem
            )
        )

        return ReturnResult.Success
    }

    override suspend fun importFromOpenLibrary(query: String, limit: Int): ImportResult {
        val current = state.value
        val activeRole = current.users.find { it.id == current.activeUserId }?.role
        if (activeRole != Role.LIBRARIAN) {
            return ImportResult.Error("Недостаточно прав")
        }
        val importedBooks = try {
            api.search(query = query, limit = limit).docs.map { it.toDomainBook() }
        } catch (_: Exception) {
            return ImportResult.Error("Ошибка сети. Проверьте интернет-соединение")
        }

        val existingKeys =
            current.books.map { "${it.title.lowercase()}::${it.author.lowercase()}" }.toSet()
        val uniqueBooks = importedBooks
            .distinctBy { "${it.title.lowercase()}::${it.author.lowercase()}" }
            .filterNot {
                val key = "${it.title.lowercase()}::${it.author.lowercase()}"
                key in existingKeys
            }
            .take(limit)

        val historyItem = HistoryItem(
            time = System.currentTimeMillis(),
            message = "Импортировано ${uniqueBooks.size} книг по запросу \"$query\""
        )

        updateState(
            current.copy(
                books = current.books + uniqueBooks,
                history = current.history + historyItem
            )
        )
        return ImportResult.Success(uniqueBooks.size)
    }

    override fun deleteUser(userId: String) {
        val current = state.value
        val updatedUsers = current.users.filterNot { it.id == userId }
        if (updatedUsers.size == current.users.size) return

        val updatedActiveUserId = if (current.activeUserId == userId) {
            updatedUsers.firstOrNull()?.id
        } else {
            current.activeUserId
        }

        updateState(
            current.copy(
                users = updatedUsers,
                activeUserId = updatedActiveUserId
            )
        )
    }

    override suspend fun deleteBook(bookId: String) {
        val current = state.value
        val activeRole = current.users.find { it.id == current.activeUserId }?.role
        if (activeRole != Role.LIBRARIAN) {
            throw IllegalStateException("Недостаточно прав")
        }
        val updatedBooks = current.books.filterNot { it.id == bookId }
        if (updatedBooks.size == current.books.size) return

        updateState(
            current.copy(
                books = updatedBooks
            )
        )
    }

    override fun clearHistory() {
        val current = state.value
        updateState(current.copy(history = emptyList()))
    }

}