package io.github.arseniy.libraryapp.data.remote.mapper


import io.github.arseniy.libraryapp.data.remote.dto.OpenLibraryDocDto
import io.github.arseniy.libraryapp.domain.model.Book
import java.util.UUID

private fun coverUrl(coverId: Int): String =
    "https://covers.openlibrary.org/b/id/$coverId-L.jpg"

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
