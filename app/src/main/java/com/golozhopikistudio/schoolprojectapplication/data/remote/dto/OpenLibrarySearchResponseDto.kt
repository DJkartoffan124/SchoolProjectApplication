package com.golozhopikistudio.schoolprojectapplication.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OpenLibrarySearchResponseDto(
    val docs: List<OpenLibraryDocDto> = emptyList()
)