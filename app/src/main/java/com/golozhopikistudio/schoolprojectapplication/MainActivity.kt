package com.golozhopikistudio.schoolprojectapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.golozhopikistudio.schoolprojectapplication.core.ui.LibraryViewModelFactory
import com.golozhopikistudio.schoolprojectapplication.core.ui.theme.SchoolProjectApplicationTheme
import com.golozhopikistudio.schoolprojectapplication.data.local.JsonStore
import com.golozhopikistudio.schoolprojectapplication.data.remote.RetrofitFactory
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepositoryImpl
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role
import com.golozhopikistudio.schoolprojectapplication.navigation.AppRoot

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