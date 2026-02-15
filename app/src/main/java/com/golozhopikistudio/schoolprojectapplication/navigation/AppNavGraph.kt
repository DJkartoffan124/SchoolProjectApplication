package com.golozhopikistudio.schoolprojectapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.golozhopikistudio.schoolprojectapplication.features.apiImport.ImportScreen
import com.golozhopikistudio.schoolprojectapplication.features.catalog.CatalogScreen
import com.golozhopikistudio.schoolprojectapplication.features.details.BookDetailsScreen
import com.golozhopikistudio.schoolprojectapplication.features.journal.JournalScreen
import com.golozhopikistudio.schoolprojectapplication.features.mybooks.MyBooksScreen
import com.golozhopikistudio.schoolprojectapplication.features.profile.ProfileScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Route.Catalog.path
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Route.Catalog.path) {
            CatalogScreen(
                onOpenDetails = { bookId ->
                    navController.navigate(Route.Details.create(bookId))
                }
            )
        }

        composable(
            route = Route.Details.path,
            arguments = listOf(
                navArgument(Route.Details.ARG_BOOK_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString(Route.Details.ARG_BOOK_ID)!!

            BookDetailsScreen(
                bookId = bookId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.MyBooks.path) { MyBooksScreen() }
        composable(Route.Journal.path) { JournalScreen() }
        composable(Route.Import.path) { ImportScreen() }
        composable(Route.Profile.path) { ProfileScreen() }
    }
}
