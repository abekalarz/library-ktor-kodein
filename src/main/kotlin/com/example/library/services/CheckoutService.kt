package com.example.library.services

import com.example.library.repository.CheckoutRepository

sealed class CheckoutResult {
    data class Success(val message: String) : CheckoutResult()
    data class BookNotFound(val message: String) : CheckoutResult()
    data class BookNotAvailable(val message: String) : CheckoutResult()
}

sealed class ReturnResult {
    data class Success(val message: String) : ReturnResult()
    data class BookNotFound(val message: String) : ReturnResult()
    data class BookNotCheckedOut(val message: String) : ReturnResult()
}

class CheckoutService(private val checkoutRepository: CheckoutRepository, private val bookService: BookService) {
    fun checkoutBook(userId: Int, bookId: Int): CheckoutResult {
        val book = bookService.getBookById(bookId)
            ?: return CheckoutResult.BookNotFound("Book with ID $bookId does not exist in our collection")

        if (!book.available) {
            return CheckoutResult.BookNotAvailable("Book '${book.title}' is currently checked out and not available")
        }

        checkoutRepository.checkoutBook(userId, bookId)
        return CheckoutResult.Success("Book '${book.title}' checked out successfully!")
    }

    fun returnBook(userId: Int, bookId: Int): ReturnResult {
        val book = bookService.getBookById(bookId)
            ?: return ReturnResult.BookNotFound("Book with ID $bookId does not exist in our collection")

        if (!checkoutRepository.isBookCheckedOutByUser(userId, bookId)) {
            return ReturnResult.BookNotCheckedOut("Book '${book.title}' is not checked out by user $userId")
        }

        checkoutRepository.returnBook(userId, bookId)
        return ReturnResult.Success("Book '${book.title}' returned successfully!")
    }

}
