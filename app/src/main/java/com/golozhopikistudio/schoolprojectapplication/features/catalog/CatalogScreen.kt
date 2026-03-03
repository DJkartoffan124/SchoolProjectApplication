package com.golozhopikistudio.schoolprojectapplication.features.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golozhopikistudio.schoolprojectapplication.core.ui.components.BookCard
import com.golozhopikistudio.schoolprojectapplication.core.ui.components.EmptyState
import com.golozhopikistudio.schoolprojectapplication.core.ui.components.SearchBar

@Composable
fun CatalogScreen(
    onOpenDetails: (String) -> Unit,
    viewModel: CatalogViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
                if (uiState.canAddBook) {
                    FloatingActionButton(onClick = { showAddDialog = true }) {
                        Text("+")
                    }
                }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchBar(query = uiState.query, onQueryChange = viewModel::onQueryChange)

            if (uiState.books.isEmpty()) {
                EmptyState(message = "Книги не найдены")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.books) { book ->
                        BookCard(book = book, onClick = { onOpenDetails(book.id) })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Новая книга") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Название") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Автор") },
                        singleLine = true
                    )
                }
            },
            dismissButton = {
                Button(onClick = { showAddDialog = false }) {
                    Text("Отмена")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addManualBook(title = title, author = author)
                        title = ""
                        author = ""
                        showAddDialog = false
                    },
                    enabled = title.isNotBlank() && author.isNotBlank()
                ) {
                    Text("Добавить")
                }
            }
        )
    }
}