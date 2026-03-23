package com.example.library.services

import com.example.library.repository.BookRepository

data class Book(val id: Int, val title: String, val available: Boolean)

data class CheckedOutTo(val userId: Int, val username: String)

data class BookAvailabilityResponse(
    val bookId: Int,
    val title: String,
    val status: String,
    val checkedOutTo: CheckedOutTo? = null
)

sealed class DeleteBookResult {
    object Success : DeleteBookResult()
    object BookNotFound : DeleteBookResult()
    object BookIsCheckedOut : DeleteBookResult()
}

class BookService(
    private val bookRepository: BookRepository
) {
    fun listBooks(titleFilter: String? = null): List<Book> = bookRepository.listBooks(titleFilter)

    fun listBooksWithAvailability(): List<BookAvailabilityResponse> =
        bookRepository.listBooksWithAvailability()

    fun addBook(title: String): Int = bookRepository.addBook(title)

    fun getBookById(bookId: Int) = bookRepository.getBookById(bookId)

    fun deleteBook(bookId: Int): DeleteBookResult {
        if (bookRepository.getBookById(bookId) == null) {
            return DeleteBookResult.BookNotFound
        }
        return try {
            bookRepository.deleteBook(bookId)
            DeleteBookResult.Success
        } catch (e: Exception) {
            if (e.message?.contains("foreign key constraint", ignoreCase = true) == true) {
                DeleteBookResult.BookIsCheckedOut
            } else {
                throw e
            }
        }
    }
}
