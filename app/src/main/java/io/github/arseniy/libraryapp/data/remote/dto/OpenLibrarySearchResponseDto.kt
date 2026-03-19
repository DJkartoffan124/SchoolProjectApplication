package io.github.arseniy.libraryapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OpenLibrarySearchResponseDto(
    val docs: List<OpenLibraryDocDto> = emptyList()
)