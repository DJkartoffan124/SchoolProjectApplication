package com.golozhopikistudio.schoolprojectapplication.domain.result

sealed class ImportResult {
    data class Success(val importedCount: Int) : ImportResult()
    data class Error(val message: String) : ImportResult()
}