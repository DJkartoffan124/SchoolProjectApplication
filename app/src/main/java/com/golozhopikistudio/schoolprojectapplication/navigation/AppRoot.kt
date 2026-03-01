package com.golozhopikistudio.schoolprojectapplication.navigation

import android.util.Log
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.golozhopikistudio.schoolprojectapplication.core.ui.LibraryViewModelFactory

@Composable
fun AppRoot(viewModelFactory: LibraryViewModelFactory) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val tabs = listOf(Route.Catalog, Route.MyBooks, Route.Import, Route.Journal, Route.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { route ->
                    val isSelected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == route.path } == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            Log.println(Log.WARN, "jopa" ,"норм")
                            val isOnDetails = currentDestination?.route == Route.Details.path
                            navController.navigate(route.path) {
                                if (isOnDetails) {
                                    popUpTo(Route.Details.path) { inclusive = true }
                                    Log.println(Log.WARN, "jopa" ,"жопа")
                                } else {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    restoreState = true
                                    Log.println(Log.WARN, "jopa" ,"говно")
                                }
                                launchSingleTop = true
                            }
                        },
                        icon = { Text(route.label.take(1)) },
                        label = { Text(route.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            viewModelFactory = viewModelFactory,
            contentPadding = innerPadding
        )
    }
}