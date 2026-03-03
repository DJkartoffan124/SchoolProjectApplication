package com.golozhopikistudio.schoolprojectapplication.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.golozhopikistudio.schoolprojectapplication.features.apiImport.ImportScreen
import com.golozhopikistudio.schoolprojectapplication.features.apiImport.ImportViewModel
import com.golozhopikistudio.schoolprojectapplication.features.catalog.CatalogScreen
import com.golozhopikistudio.schoolprojectapplication.features.catalog.CatalogViewModel
import com.golozhopikistudio.schoolprojectapplication.features.details.BookDetailsScreen
import com.golozhopikistudio.schoolprojectapplication.features.details.DetailsViewModel
import com.golozhopikistudio.schoolprojectapplication.features.journal.JournalScreen
import com.golozhopikistudio.schoolprojectapplication.features.journal.JournalViewModel
import com.golozhopikistudio.schoolprojectapplication.features.mybooks.MyBooksScreen
import com.golozhopikistudio.schoolprojectapplication.features.mybooks.MyBooksViewModel
import com.golozhopikistudio.schoolprojectapplication.features.profile.ProfileScreen
import com.golozhopikistudio.schoolprojectapplication.features.profile.ProfileViewModel
@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory,
    startDestination: String = Route.Catalog.path,
    contentPadding: PaddingValues = PaddingValues()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(contentPadding)
    ) {
        composable(Route.Catalog.path) {
            val vm: CatalogViewModel = viewModel(factory = viewModelFactory)
            CatalogScreen(
                onOpenDetails = { bookId ->
                    navController.navigate(
                        Route.Details.create(
                            bookId = bookId,
                            source = Route.DetailsSource.Catalog
                        )
                    )
                },
                viewModel = vm
            )
        }

        composable(
            route = Route.Details.path,
            arguments = listOf(
                navArgument(Route.Details.ARG_BOOK_ID) { type = NavType.StringType },
                navArgument(Route.Details.ARG_SOURCE) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString(Route.Details.ARG_BOOK_ID)!!
            val source = backStackEntry.arguments?.getString(Route.Details.ARG_SOURCE)!!
            val sourceRoute = if (source == Route.DetailsSource.MyBooks) Route.MyBooks.path else Route.Catalog.path

            val vm: DetailsViewModel = viewModel(factory = viewModelFactory)
            BookDetailsScreen(
                bookId = bookId,
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(sourceRoute) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            restoreState = true
                        }
                    }
                },
                viewModel = vm,
                showDeleteButton = source == Route.DetailsSource.Catalog
            )
        }

        composable(Route.MyBooks.path) {
            val vm: MyBooksViewModel = viewModel(factory = viewModelFactory)
            MyBooksScreen(
                viewModel = vm,
                onOpenDetails = { bookId ->
                    navController.navigate(
                        Route.Details.create(
                            bookId = bookId,
                            source = Route.DetailsSource.MyBooks
                        )
                    )
                }
            )
        }
        composable(Route.Journal.path) {
            val vm: JournalViewModel = viewModel(factory = viewModelFactory)
            JournalScreen(viewModel = vm)
        }
        composable(Route.Import.path) {
            val vm: ImportViewModel = viewModel(factory = viewModelFactory)
            ImportScreen(viewModel = vm)
        }
        composable(Route.Profile.path) {
            val vm: ProfileViewModel = viewModel(factory = viewModelFactory)
            ProfileScreen(viewModel = vm)
        }
    }
}