package com.golozhopikistudio.schoolprojectapplication.domain.result

sealed class BorrowResult {

    data object Success : BorrowResult()

    data object NotFound : BorrowResult()

    data object AlreadyBorrowed : BorrowResult()

    data object NotAllowed : BorrowResult()
}