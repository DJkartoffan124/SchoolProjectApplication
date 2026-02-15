package com.golozhopikistudio.schoolprojectapplication.domain.result

sealed class ReturnResult {

    data object Success : ReturnResult()

    data object NotFound : ReturnResult()

    data object NotBorrowed : ReturnResult()

    data object NotAllowed : ReturnResult()
}