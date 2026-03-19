package io.github.arseniy.libraryapp.domain.result

sealed class BorrowResult {

    data object Success : BorrowResult()

    data object NotFound : BorrowResult()

    data object AlreadyBorrowed : BorrowResult()

    data object NotAllowed : BorrowResult()
}