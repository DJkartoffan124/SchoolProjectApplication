package com.golozhopikistudio.schoolprojectapplication.navigation

sealed class Route(val path: String) {
    data object Catalog : Route("catalog")
    data object MyBooks : Route("mybooks")
    data object Journal : Route("journal")
    data object Import : Route("import")
    data object Profile : Route("profile")

    data object Details : Route("details/{bookId}") {
        const val ARG_BOOK_ID = "bookId"
        fun create(bookId: String) = "details/$bookId"
    }
}