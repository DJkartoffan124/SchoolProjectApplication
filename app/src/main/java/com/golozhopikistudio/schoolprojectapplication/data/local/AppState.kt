package com.golozhopikistudio.schoolprojectapplication.data.local


import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import com.golozhopikistudio.schoolprojectapplication.domain.model.HistoryItem
import com.golozhopikistudio.schoolprojectapplication.domain.model.User
import kotlinx.serialization.Serializable


@Serializable
data class AppState(
    val books: List<Book> = emptyList(),
    val users: List<User> = emptyList(),
    val history: List<HistoryItem> = emptyList(),
    val activeUserId: String? = null
)