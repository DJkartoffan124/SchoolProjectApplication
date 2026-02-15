package com.golozhopikistudio.schoolprojectapplication.features.apiImport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ImportScreen(
    viewModel: ImportViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.importedCount) {
        if (uiState.importedCount > 0) {
            snackbarHostState.showSnackbar("Импортировано книг: ${uiState.importedCount}")
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Запрос") }
            )
            Button(
                onClick = { viewModel.import(query) },
                enabled = query.isNotBlank() && !uiState.isLoading
            ) {
                Text("Импорт")
            }
            if (uiState.isLoading) {
                CircularProgressIndicator()
            }
            Text("Импортировано: ${uiState.importedCount}")
            uiState.error?.let { Text("Ошибка: $it") }
        }
    }
}