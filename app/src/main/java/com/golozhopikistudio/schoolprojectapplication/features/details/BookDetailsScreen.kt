package com.golozhopikistudio.schoolprojectapplication.features.details

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    bookId: String,
    onBack: () -> Unit,
    viewModel: DetailsViewModel,
    showDeleteButton: Boolean = true
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(bookId) {
        viewModel.load(bookId)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailsEffect.ShowToast -> Toast.makeText(
                    context,
                    effect.message,
                    Toast.LENGTH_SHORT
                )
                    .show()

                DetailsEffect.NavigateBack -> onBack()
            }
        }
    }

    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val book = uiState.book
    val canDelete = showDeleteButton && uiState.isLibrarian
    val borrowedByOther = book?.isBorrowed == true && book.borrowerId != uiState.activeUserId
    val borrowedByCurrent = book?.isBorrowed == true && book.borrowerId == uiState.activeUserId

    if (canDelete && showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Удалить книгу?") },
            text = { Text("Это действие нельзя отменить") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.onAction(DetailsAction.DeleteClicked)
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Отмена")
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали книги") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    Text(
                        text = "Загрузка данных о книге...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                uiState.emptyMessage != null -> {
                    Text(
                        text = uiState.emptyMessage!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                book != null -> {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val url = book.coverUrl

                    if (!url.isNullOrBlank()) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Обложка книги",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = "Источник: $url",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text("Обложка отсутствует")
                    }

                    Text(
                        text = if (book.isBorrowed) "Статус: выдана" else "Статус: в библиотеке",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (borrowedByOther) {
                        Text(
                            text = "Выдана другому пользователю",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Button(
                        onClick = { viewModel.onAction(DetailsAction.BorrowClicked) },
                        enabled = !book.isBorrowed,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Выдать")
                    }

                    OutlinedButton(
                        onClick = { viewModel.onAction(DetailsAction.ReturnClicked) },
                        enabled = borrowedByCurrent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Вернуть")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (canDelete) {
                Button(
                    onClick = { showDeleteConfirmation = true },
                    enabled = book != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Удалить книгу")
                }
            }
        }
    }
}