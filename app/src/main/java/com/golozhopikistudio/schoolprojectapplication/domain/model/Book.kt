package com.golozhopikistudio.schoolprojectapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String? = null,
    val isBorrowed: Boolean = false,
    val borrowerId: String? = null
)