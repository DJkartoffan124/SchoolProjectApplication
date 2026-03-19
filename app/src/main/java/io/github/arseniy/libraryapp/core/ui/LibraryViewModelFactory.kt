package io.github.arseniy.libraryapp.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.arseniy.libraryapp.data.repository.LibraryRepository
import io.github.arseniy.libraryapp.features.apiImport.ImportViewModel
import io.github.arseniy.libraryapp.features.catalog.CatalogViewModel
import io.github.arseniy.libraryapp.features.details.DetailsViewModel
import io.github.arseniy.libraryapp.features.journal.JournalViewModel
import io.github.arseniy.libraryapp.features.mybooks.MyBooksViewModel
import io.github.arseniy.libraryapp.features.profile.ProfileViewModel


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