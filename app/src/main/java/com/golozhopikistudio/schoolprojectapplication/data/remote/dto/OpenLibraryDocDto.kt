package com.golozhopikistudio.schoolprojectapplication.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenLibraryDocDto(
    val title: String? = null,

    @SerialName("author_name")
    val authorNames: List<String> = emptyList(),

    @SerialName("cover_i")
    val coverId: Int? = null
)