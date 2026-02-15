package com.golozhopikistudio.schoolprojectapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.golozhopikistudio.schoolprojectapplication.core.ui.theme.SchoolProjectApplicationTheme
import com.golozhopikistudio.schoolprojectapplication.data.local.JsonStore
import com.golozhopikistudio.schoolprojectapplication.data.remote.RetrofitFactory
import com.golozhopikistudio.schoolprojectapplication.data.repository.LibraryRepositoryImpl
import com.golozhopikistudio.schoolprojectapplication.domain.model.Role
import com.golozhopikistudio.schoolprojectapplication.navigation.AppNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = LibraryRepositoryImpl(
            jsonStore = JsonStore(this),
            api = RetrofitFactory.createOpenLibraryApi()
        )
        repository.setActiveUser(name = "Reader", role = Role.READER)

        setContent {
            val navController = rememberNavController()
            val factory = remember { LibraryViewModelFactory(repository) }


                SchoolProjectApplicationTheme {
                    AppNavGraph(
                        navController = navController,
                        viewModelFactory = factory
                    )
                }

            }
        }
    }