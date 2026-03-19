package io.github.arseniy.libraryapp.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role
import com.golozhopikistudio.schoolprojectapplication.domain.model.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(private val repository: LibraryRepository) : ViewModel() {

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
    
    fun deleteUser(user: User) {
        repository.deleteUser(user.id)
    }
}


data class ProfileUiState(
    val user: User? = null,
    val users: List<User> = emptyList()
)