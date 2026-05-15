package com.example.library.domain

data class Book(val id: Int, val title: String, val available: Boolean)

data class BookAvailabilityResponse(
    val bookId: Int,
    val title: String,
    val status: BookStatus,
    val checkedOutTo: User? = null
)

sealed class DeleteBookResult {
    object Success : DeleteBookResult()
    object BookNotFound : DeleteBookResult()
    object BookIsCheckedOut : DeleteBookResult()
}
