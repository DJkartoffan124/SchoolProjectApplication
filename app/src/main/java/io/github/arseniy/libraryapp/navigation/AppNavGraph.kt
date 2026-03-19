package io.github.arseniy.libraryapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.arseniy.libraryapp.features.apiImport.ImportScreen
import io.github.arseniy.libraryapp.features.apiImport.ImportViewModel
import io.github.arseniy.libraryapp.features.catalog.CatalogScreen
import io.github.arseniy.libraryapp.features.catalog.CatalogViewModel
import io.github.arseniy.libraryapp.features.details.BookDetailsScreen
import io.github.arseniy.libraryapp.features.details.DetailsViewModel
import io.github.arseniy.libraryapp.features.journal.JournalScreen
import io.github.arseniy.libraryapp.features.journal.JournalViewModel
import io.github.arseniy.libraryapp.features.mybooks.MyBooksScreen
import io.github.arseniy.libraryapp.features.mybooks.MyBooksViewModel
import io.github.arseniy.libraryapp.features.profile.ProfileScreen
import io.github.arseniy.libraryapp.features.profile.ProfileViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory,
    startDestination: String = Route.Catalog.path,
    bottomInset: Dp = 0.dp,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Route.Catalog.path) {
            val vm: CatalogViewModel = viewModel(factory = viewModelFactory)
            CatalogScreen(
                bottomInset = bottomInset,
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
                bottomInset = bottomInset,
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
            JournalScreen(
                viewModel = vm
            )
        }
        composable(Route.Import.path) {
            val vm: ImportViewModel = viewModel(factory = viewModelFactory)
            ImportScreen(
                viewModel = vm
            )
        }
        composable(Route.Profile.path) {
            val vm: ProfileViewModel = viewModel(factory = viewModelFactory)
            ProfileScreen(
                viewModel = vm
            )
        }
    }
}