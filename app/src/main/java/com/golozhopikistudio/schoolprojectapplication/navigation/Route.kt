package com.golozhopikistudio.schoolprojectapplication.navigation

sealed class Route(val path: String, val label: String) {
    data object Catalog : Route("catalog", "Catalog")
    data object MyBooks : Route("mybooks", "MyBooks")
    data object Journal : Route("journal", "Journal")
    data object Import : Route("import", "Import")
    data object Profile : Route("profile", "Profile")

    data object Details : Route("details/{bookId}/{source}", "Details") {
        const val ARG_BOOK_ID = "bookId"
        const val ARG_SOURCE = "source"
        fun create(bookId: String, source: String) = "details/$bookId/$source"
    }

    data object DetailsSource {
        const val Catalog = "Catalog"
        const val MyBooks = "MyBooks"
    }
}