package com.golozhopikistudio.schoolprojectapplication.features.apiImport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ImportScreen(
    viewModel: ImportViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var requestedCountText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    val requestedCount = requestedCountText.toIntOrNull()
    val isCountValid = requestedCount != null && requestedCount >= 1

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeSnackbar()
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

            OutlinedTextField(
                value = requestedCountText,
                onValueChange = { requestedCountText = it },
                label = { Text("Сколько книг импортировать") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = requestedCountText.isNotBlank() && !isCountValid,
                supportingText = {
                    if (requestedCountText.isNotBlank() && !isCountValid) {
                        Text("Введите целое число не меньше 1")
                    }
                },
                singleLine = true
            )

            Button(
                onClick = { viewModel.import(query, requestedCount!!) },
                enabled = query.isNotBlank() && isCountValid && !uiState.isLoading
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