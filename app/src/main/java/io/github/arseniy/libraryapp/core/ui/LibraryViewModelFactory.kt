package io.github.arseniy.libraryapp.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepository
import com.golozhopikistudio.schoolprojectapplication.features.apiImport.ImportViewModel
import com.golozhopikistudio.schoolprojectapplication.features.catalog.CatalogViewModel
import com.golozhopikistudio.schoolprojectapplication.features.details.DetailsViewModel
import com.golozhopikistudio.schoolprojectapplication.features.journal.JournalViewModel
import com.golozhopikistudio.schoolprojectapplication.features.mybooks.MyBooksViewModel
import com.golozhopikistudio.schoolprojectapplication.features.profile.ProfileViewModel

class LibraryViewModelFactory(
    private val repository: LibraryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            CatalogViewModel::class.java -> CatalogViewModel(repository)
            DetailsViewModel::class.java -> DetailsViewModel(repository)
            MyBooksViewModel::class.java -> MyBooksViewModel(repository)
            JournalViewModel::class.java -> JournalViewModel(repository)
            ImportViewModel::class.java -> ImportViewModel(repository)
            ProfileViewModel::class.java -> ProfileViewModel(repository)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}