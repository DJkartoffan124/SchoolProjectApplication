package com.golozhopikistudio.schoolprojectapplication.data.repository

import com.golozhopikistudio.schoolprojectapplication.data.local.AppState
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role
import com.golozhopikistudio.schoolprojectapplication.domain.result.BorrowResult
import com.golozhopikistudio.schoolprojectapplication.domain.result.ImportResult
import com.golozhopikistudio.schoolprojectapplication.domain.result.ReturnResult
import kotlinx.coroutines.flow.StateFlow

interface LibraryRepository {

    val state: StateFlow<AppState>

    fun setActiveUser(name: String, role: Role)

    fun addManualBook(title: String, author: String)

    fun borrow(bookId: String): BorrowResult

    fun returnBook(bookId: String): ReturnResult

    suspend fun importFromOpenLibrary(query: String): ImportResult

    suspend fun deleteBook(bookId: String)
    
}
