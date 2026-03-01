package com.golozhopikistudio.schoolprojectapplication.navigation

sealed class Route(val path: String, val label: String) {
    data object Catalog : Route("catalog", "Catalog")
    data object MyBooks : Route("mybooks", "MyBooks")
    data object Journal : Route("journal", "Journal")
    data object Import : Route("import", "Import")
    data object Profile : Route("profile", "Profile")

    data object Details : Route("details/{bookId}", "Details") {
        const val ARG_BOOK_ID = "bookId"
        fun create(bookId: String) = "details/$bookId"
    }

    data object MyBookDetails : Route("mybooks/details/{bookId}", "MyBookDetails") {
        const val ARG_BOOK_ID = "bookId"
        fun create(bookId: String) = "mybooks/details/$bookId"
    }
}