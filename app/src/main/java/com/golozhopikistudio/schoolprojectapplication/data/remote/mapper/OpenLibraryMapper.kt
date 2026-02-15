package com.golozhopikistudio.schoolprojectapplication.data.remote.mapper

import com.golozhopikistudio.schoolprojectapplication.data.remote.dto.OpenLibraryDocDto
import com.golozhopikistudio.schoolprojectapplication.domain.model.Book
import java.util.UUID

private fun coverUrl(coverId: Int, size: String = "M"): String =
    "https://covers.openlibrary.org/b/id/$coverId-$size.jpg"

fun OpenLibraryDocDto.toDomainBook(): Book {
    val safeTitle = title?.takeIf { it.isNotBlank() } ?: "Untitled"
    val safeAuthor = authorNames.firstOrNull()?.takeIf { it.isNotBlank() } ?: "Unknown author"

    return Book(
        id = UUID.randomUUID().toString(),
        title = safeTitle,
        author = safeAuthor,
        coverUrl = coverId?.let { coverUrl(it) },
        isBorrowed = false,
        borrowerId = null
    )
}
