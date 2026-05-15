package com.example.library.services

import com.example.library.domain.Book
import com.example.library.domain.BookAvailabilityResponse
import com.example.library.domain.DeleteBookResult
import com.example.library.repository.BookRepository

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
