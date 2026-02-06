package com.example.library.services

import com.example.library.repository.BookRepository

data class Book(val id: Int, val title: String, val available: Boolean)

class BookService(
    private val bookRepository: BookRepository
) {
    fun listBooks(titleFilter: String? = null): List<Book> = bookRepository.listBooks(titleFilter)

    fun addBook(title: String) {
        bookRepository.addBook(title)
    }

    fun getBookById(bookId: Int) = bookRepository.getBookById(bookId)
}
