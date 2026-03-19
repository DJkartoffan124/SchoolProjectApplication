package io.github.arseniy.libraryapp.features.apiImport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role
import com.golozhopikistudio.schoolprojectapplication.domain.result.ImportResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImportViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ImportLocalState())
    val state: StateFlow<ImportUiState> = combine(repository.state, _state) { appState, localState ->
        ImportUiState(
            isLoading = localState.isLoading,
            importedCount = localState.importedCount,
            error = localState.error,
            toastMessage = localState.toastMessage,
            isLibrarian = appState.users
                .find { it.id == appState.activeUserId }
                ?.role == Role.LIBRARIAN
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ImportUiState()
    )

    fun import(query: String, requestedCount: Int) = viewModelScope.launch {
        if (!state.value.isLibrarian) {
            _state.update { it.copy(toastMessage = "Недостаточно прав") }
            return@launch
        }

        _state.update { it.copy(isLoading = true, error = null, toastMessage = null) }

        when (val result = repository.importFromOpenLibrary(query, requestedCount)) {
            is ImportResult.Success -> {
                val toastMessage = "Импорт завершён: добавлено ${result.importedCount} книг"
                _state.update {
                    it.copy(
                        isLoading = false,
                        importedCount = result.importedCount,
                        toastMessage = toastMessage
                    )
                }
            }

            is ImportResult.Error -> {
                _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun consumeSnackbar() {
        _state.update { it.copy(toastMessage = null) }
    }
}

private data class ImportLocalState(
    val isLoading: Boolean = false,
    val importedCount: Int = 0,
    val error: String? = null,
    val toastMessage: String? = null
)

data class ImportUiState(
    val isLoading: Boolean = false,
    val importedCount: Int = 0,
    val error: String? = null,
    val toastMessage: String? = null,
    val isLibrarian: Boolean = false
)