package com.example.library.services

import com.example.library.repository.CheckoutRepository

sealed class CheckoutResult {
    data class Success(val message: String) : CheckoutResult()
    data class BookNotFound(val message: String) : CheckoutResult()
    data class BookNotAvailable(val message: String) : CheckoutResult()
}

class CheckoutService(private val checkoutRepository: CheckoutRepository, private val bookService: BookService) {
    fun checkoutBook(userId: Int, bookId: Int): CheckoutResult {
        val book = bookService.getBookById(bookId)
            ?: return CheckoutResult.BookNotFound("Book with ID $bookId does not exist in our collection")

        if (!book.available) {
            return CheckoutResult.BookNotAvailable("Book '${book.title}' is currently checked out and not available")
        }
        
//        db.jdbi.useHandle<Exception> { handle ->
//            handle.begin()
//            handle.execute("UPDATE books SET available = FALSE WHERE id = ?", bookId)
//            handle.execute("INSERT INTO checkouts (user_id, book_id) VALUES (?, ?)", userId, bookId)
//            handle.commit()
//        }

        checkoutRepository.checkoutBook(userId, bookId)
        return CheckoutResult.Success("Book '${book.title}' checked out successfully!")
    }

}
