package com.golozhopikistudio.schoolprojectapplication.features.profile

import androidx.lifecycle.ViewModel
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun load() {
        _state.update {
            it.copy(user = repository.getUser())
        }
    }
}


data class ProfileUiState(
    val user: User? = null
)
