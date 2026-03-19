package io.github.arseniy.libraryapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    READER,
    LIBRARIAN
}