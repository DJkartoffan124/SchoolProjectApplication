package com.golozhopikistudio.schoolprojectapplication.features.mybooks

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

@Composable
fun MyBooksScreen(
    viewModel: MyBooksViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Мои книги")
        LazyColumn {
            items(uiState.books) { book ->
                Text(text = "${book.title} — ${book.author}", modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}