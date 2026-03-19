package io.github.arseniy.libraryapp.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.arseniy.libraryapp.core.ui.LibraryViewModelFactory

@Composable
fun AppRoot(viewModelFactory: LibraryViewModelFactory) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val tabs = listOf(Route.Catalog, Route.MyBooks, Route.Import, Route.Journal, Route.Profile)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar {
                tabs.forEach { route ->
                    val isSelected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == route.path } == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            val currentRoute = currentDestination?.route
                            val isDetailsScreen = currentRoute?.startsWith("details/") == true
                            val shouldReturnFromDetails =
                                isDetailsScreen && (route == Route.Catalog || route == Route.MyBooks)

                            if (shouldReturnFromDetails && navController.popBackStack(route.path, false)) {
                                return@NavigationBarItem
                            }

                            navController.navigate(route.path) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(route.label.take(1)) },
                        label = { Text(route.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val bottomInset: Dp = innerPadding.calculateBottomPadding()
        AppNavGraph(
            modifier = Modifier,
            navController = navController,
            viewModelFactory = viewModelFactory,
            bottomInset = bottomInset
        )
    }
}
