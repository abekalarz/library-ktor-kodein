package com.example.library.domain

sealed class CheckoutRepositoryResult {
    data class Success(val book: Book) : CheckoutRepositoryResult()
    object BookNotFound : CheckoutRepositoryResult()
    object BookNotAvailable : CheckoutRepositoryResult()
}

sealed class CheckoutResult {
    data class Success(val message: String) : CheckoutResult()
    data class BookNotFound(val message: String) : CheckoutResult()
    data class BookNotAvailable(val message: String) : CheckoutResult()
    data class UserNotFound(val message: String) : CheckoutResult()
    data class AlreadyCheckedOut(val message: String) : CheckoutResult()
    data class CheckoutLimitExceeded(val message: String) : CheckoutResult()
}

sealed class ReturnResult {
    data class Success(val message: String) : ReturnResult()
    data class BookNotFound(val message: String) : ReturnResult()
    data class BookNotCheckedOut(val message: String) : ReturnResult()
}
