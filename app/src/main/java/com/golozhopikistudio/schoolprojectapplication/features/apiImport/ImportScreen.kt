package com.golozhopikistudio.schoolprojectapplication.features.apiImport

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    contentPadding: PaddingValues,
    viewModel: ImportViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var requestedCountText by remember { mutableStateOf("") }

    val requestedCount = requestedCountText.toIntOrNull()
    val isCountValid = requestedCount != null && requestedCount >= 1

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.padding(contentPadding),
        topBar = {
            TopAppBar(title = { Text("Импорт") })
        }
    ) { innerPadding ->
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
                label = { Text("Запрос") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
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
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.import(query, requestedCount!!) },
                enabled = query.isNotBlank() && isCountValid && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
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