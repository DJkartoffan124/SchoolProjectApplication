package com.golozhopikistudio.schoolprojectapplication.features.mybooks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golozhopikistudio.schoolprojectapplication.core.ui.components.BookCard
import com.golozhopikistudio.schoolprojectapplication.core.ui.components.EmptyState

@Composable
fun MyBooksScreen(
    viewModel: MyBooksViewModel,
    onOpenDetails: (String) -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Мои книги")

        if (uiState.books.isEmpty()) {
            EmptyState(message = "У вас пока нет книг")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.books) { book ->
                    BookCard(book = book, onClick = { onOpenDetails(book.id) })
                }
            }
        }
    }
}