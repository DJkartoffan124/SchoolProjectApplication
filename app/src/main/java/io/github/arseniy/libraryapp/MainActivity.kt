package io.github.arseniy.libraryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import io.github.arseniy.libraryapp.core.ui.LibraryViewModelFactory
import io.github.arseniy.libraryapp.core.ui.theme.SchoolProjectApplicationTheme
import io.github.arseniy.libraryapp.data.local.JsonStore
import io.github.arseniy.libraryapp.data.remote.RetrofitFactory
import io.github.arseniy.libraryapp.data.repository.LibraryRepositoryImpl
import io.github.arseniy.libraryapp.domain.model.Role
import io.github.arseniy.libraryapp.navigation.AppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = LibraryRepositoryImpl(
            jsonStore = JsonStore(this),
            api = RetrofitFactory.createOpenLibraryApi()
        )

        if (repository.state.value.activeUserId == null) {
            repository.setActiveUser(name = "Reader", role = Role.READER)
        }

        setContent {
            val factory = remember { LibraryViewModelFactory(repository) }

            SchoolProjectApplicationTheme {
                AppRoot(viewModelFactory = factory)
            }
        }
    }
}