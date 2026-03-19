package io.github.arseniy.libraryapp.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.arseniy.libraryapp.data.repository.LibraryRepository
import io.github.arseniy.libraryapp.domain.model.Role
import io.github.arseniy.libraryapp.domain.model.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(private val repository: LibraryRepository) : ViewModel() {

    companion object {
        fun canDeleteProfile(activeUser: User?, targetUser: User): Boolean {
            return when (activeUser?.role) {
                Role.LIBRARIAN -> true
                Role.READER -> activeUser.id == targetUser.id
                null -> false
            }
        }
    }

    val state: StateFlow<ProfileUiState> = repository.state.map { appState ->
        val activeUser = appState.users.find { it.id == appState.activeUserId }
        ProfileUiState(
            user = activeUser,
            users = appState.users
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun setActiveUser(name: String, role: Role) {
        if (name.isBlank()) return
        repository.setActiveUser(name.trim(), role)
    }

    fun setActiveUser(user: User) {
        repository.setActiveUser(user.name, user.role)
    }

    fun canDeleteUser(user: User): Boolean = canDeleteProfile(
        activeUser = state.value.user,
        targetUser = user
    )

    fun deleteUser(user: User): Boolean {
        if (!canDeleteUser(user)) return false
        repository.deleteUser(user.id)
        return true
    }
}


data class ProfileUiState(
    val user: User? = null,
    val users: List<User> = emptyList()
)