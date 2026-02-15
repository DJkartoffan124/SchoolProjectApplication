package com.golozhopikistudio.schoolprojectapplication.features.journal

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalScreen(
    viewModel: JournalViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Журнал")
        LazyColumn {
            items(uiState.history) { item ->
                val formattedTime = formatter.format(Date(item.time))
                Text(text = "$formattedTime — ${item.message}", modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}