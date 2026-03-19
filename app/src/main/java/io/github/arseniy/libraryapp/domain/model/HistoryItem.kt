package io.github.arseniy.libraryapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
    val time: Long,
    val message: String
)