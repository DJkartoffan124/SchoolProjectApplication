package com.golozhopikistudio.schoolprojectapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
    val time: Long,
    val message: String
)