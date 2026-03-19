package io.github.arseniy.libraryapp.features.catalog

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golozhopikistudio.schoolprojectapplication.core.ui.components.BookCard
import com.golozhopikistudio.schoolprojectapplication.core.ui.components.EmptyState
import com.golozhopikistudio.schoolprojectapplication.core.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onOpenDetails: (String) -> Unit,
    viewModel: CatalogViewModel,
    bottomInset: Dp = 0.dp
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Каталог",
                        modifier = Modifier.combinedClickable(
                            onClick = {},
                            onLongClick = {
                                if (uiState.canClearCatalog) showClearDialog = true
                            }
                        )
                    )
                }
            )
        },
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = bottomInset)
                ) {
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
    if (uiState.canClearCatalog && showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить каталог?") },
            text = { Text("Это действие удалит все книги в каталоге") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCatalog()
                        showClearDialog = false
                    }
                ) {
                    Text("Очистить")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}