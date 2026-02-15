package com.golozhopikistudio.schoolprojectapplication.features.details

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BookDetailsScreen(
    bookId: String,
    onBack: () -> Unit,
    viewModel: DetailsViewModel
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
            }
        }
    }

    val book = uiState.book
    val borrowedByOther = book?.isBorrowed == true && book.borrowerId != uiState.activeUserId
    val borrowedByCurrent = book?.isBorrowed == true && book.borrowerId == uiState.activeUserId

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = book?.title ?: "Книга не найдена")
        Text(text = book?.author ?: "")
        if (borrowedByOther) {
            Text(text = "Выдана другому")
        }
        Button(
            onClick = viewModel::borrow,
            enabled = book != null && !book.isBorrowed
        ) { Text("Выдать") }
        Button(
            onClick = viewModel::returnBook,
            enabled = borrowedByCurrent
        ) { Text("Вернуть") }
        Button(onClick = onBack) { Text("Назад") }
    }
}