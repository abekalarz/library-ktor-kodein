package com.example.library.services

import com.example.library.repository.CheckoutRepository
import com.example.library.repository.CheckoutRepositoryResult

sealed class CheckoutResult {
    data class Success(val message: String) : CheckoutResult()
    data class BookNotFound(val message: String) : CheckoutResult()
    data class BookNotAvailable(val message: String) : CheckoutResult()
    data class UserNotFound(val message: String) : CheckoutResult()
}

sealed class ReturnResult {
    data class Success(val message: String) : ReturnResult()
    data class BookNotFound(val message: String) : ReturnResult()
    data class BookNotCheckedOut(val message: String) : ReturnResult()
}

class CheckoutService(
    private val checkoutRepository: CheckoutRepository,
    private val bookService: BookService,
    private val userService: UserService
) {
    fun checkoutBook(userId: Int, bookId: Int): CheckoutResult {
        if (userService.getUser(userId) == null) {
            return CheckoutResult.UserNotFound("User with ID $userId does not exist")
        }
        
        return when (val result = checkoutRepository.checkoutBook(userId, bookId)) {
            is CheckoutRepositoryResult.Success -> 
                CheckoutResult.Success("Book '${result.book.title}' checked out successfully!")
            is CheckoutRepositoryResult.BookNotFound -> 
                CheckoutResult.BookNotFound("Book with ID $bookId does not exist in our collection")
            is CheckoutRepositoryResult.BookNotAvailable -> 
                CheckoutResult.BookNotAvailable("Book is currently checked out and not available")
        }
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
