package com.golozhopikistudio.schoolprojectapplication.features.journal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
    var showClearConfirmation by remember { mutableStateOf(false) }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Очистить журнал?") },
            text = { Text("Это действие удалит все записи журнала") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearHistory()
                        showClearConfirmation = false
                    }
                ) {
                    Text("Очистить")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirmation = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Журнал") },
                actions = {
                    TextButton(onClick = { showClearConfirmation = true }) {
                        Text("Очистить")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            items(uiState.history) { item ->
                val formattedTime = formatter.format(Date(item.time))
                Text(
                    text = "$formattedTime — ${item.message}",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}