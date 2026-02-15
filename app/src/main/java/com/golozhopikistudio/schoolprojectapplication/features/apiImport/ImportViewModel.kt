package com.golozhopikistudio.schoolprojectapplication.features.apiImport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.result.ImportResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImportViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ImportUiState())
    val state: StateFlow<ImportUiState> = _state.asStateFlow()

    fun import(query: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }

        when (val result = repository.importFromOpenLibrary(query)) {
            is ImportResult.Success -> {
                _state.update { it.copy(isLoading = false, importedCount = result.importedCount) }
            }

            is ImportResult.Error -> {
                _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }
}
data class ImportUiState(
    val isLoading: Boolean = false,
    val importedCount: Int = 0,
    val error: String? = null
)
