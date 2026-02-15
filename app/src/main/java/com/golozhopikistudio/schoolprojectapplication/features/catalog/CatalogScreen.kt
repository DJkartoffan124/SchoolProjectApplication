package com.golozhopikistudio.schoolprojectapplication.features.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CatalogScreen(
    onOpenDetails: (String) -> Unit,
    viewModel: CatalogViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Поиск") },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn {
            items(uiState.books) { book ->
                Text(
                    text = "${book.title} — ${book.author}",
                    modifier = Modifier
                        .clickable { onOpenDetails(book.id) }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}