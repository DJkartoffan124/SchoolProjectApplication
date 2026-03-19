package io.github.arseniy.libraryapp.domain.model


import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val role: Role
)