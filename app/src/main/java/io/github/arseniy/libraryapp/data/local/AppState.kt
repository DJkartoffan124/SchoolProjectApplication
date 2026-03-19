package io.github.arseniy.libraryapp.data.local


import io.github.arseniy.libraryapp.domain.model.Book
import io.github.arseniy.libraryapp.domain.model.HistoryItem
import io.github.arseniy.libraryapp.domain.model.User
import kotlinx.serialization.Serializable


@Serializable
data class AppState(
    val books: List<Book> = emptyList(),
    val users: List<User> = emptyList(),
    val history: List<HistoryItem> = emptyList(),
    val activeUserId: String? = null
)