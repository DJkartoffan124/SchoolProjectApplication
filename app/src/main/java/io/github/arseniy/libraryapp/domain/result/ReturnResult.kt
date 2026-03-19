package io.github.arseniy.libraryapp.domain.result

sealed class ReturnResult {

    data object Success : ReturnResult()

    data object NotFound : ReturnResult()

    data object NotBorrowed : ReturnResult()

    data object NotAllowed : ReturnResult()
}