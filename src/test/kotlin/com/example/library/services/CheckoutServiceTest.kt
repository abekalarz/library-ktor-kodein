package com.example.library.services

import com.example.library.repository.CheckoutRepository
import com.example.library.repository.CheckoutRepositoryResult
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class CheckoutServiceTest {
    private val checkoutRepository = mockk<CheckoutRepository>()
    private val bookService = mockk<BookService>()
    private val userService = mockk<UserService>()

    private val checkoutService: CheckoutService = CheckoutService(
        checkoutRepository,
        bookService,
        userService
    )

    @Nested
    inner class CheckoutBookTests {
        private val userId = 1
        private val bookId = 10
        private val book = Book(bookId, "Sample Title", true)

        @Test
        fun `checkoutBook - when user not found then return UserNotFound`() {
            every { userService.getUser(userId) } returns null

            val result = checkoutService.checkoutBook(userId, 1)

            expectThat(result).isA<CheckoutResult.UserNotFound>()
            expectThat((result as CheckoutResult.UserNotFound).message)
                .isEqualTo("User with ID $userId does not exist")
        }

        @Test
        fun `checkoutBook - repository result success then return success`() {
            every { userService.getUser(userId) } returns User(userId, "Sample User")
            every { checkoutRepository.checkoutBook(userId, bookId) } returns
                    CheckoutRepositoryResult.Success(book)

            val result = checkoutService.checkoutBook(userId, bookId)

            expectThat(result).isA<CheckoutResult.Success>()
            expectThat(subject = (result as CheckoutResult.Success).message)
                .isEqualTo("Book '${book.title}' checked out successfully!")
        }

        @Test
        fun `checkoutBook - when book is not found then return BookNotFound`() {
            every { userService.getUser(userId) } returns User(userId, "Sample User")
            every { checkoutRepository.checkoutBook(userId, bookId) } returns
                    CheckoutRepositoryResult.BookNotFound

            val result = checkoutService.checkoutBook(userId, bookId)
            expectThat(result).isA<CheckoutResult.BookNotFound>()
            expectThat((result as CheckoutResult.BookNotFound).message)
                .isEqualTo("Book with ID $bookId does not exist in our collection")
        }

        @Test
        fun `checkoutBook - when book is not available then return BookNotAvailable`() {
            every { userService.getUser(userId) } returns User(userId, "Sample User")
            every { checkoutRepository.checkoutBook(userId, bookId) } returns
                    CheckoutRepositoryResult.BookNotAvailable

            val result = checkoutService.checkoutBook(userId, bookId)
            expectThat(result).isA<CheckoutResult.BookNotAvailable>()
            expectThat((result as CheckoutResult.BookNotAvailable).message)
                .isEqualTo("Book is currently checked out and not available")
        }
    }

    @Nested
    inner class ReturnBookTests {
        private val userId = 1
        private val bookId = 10
        private val book = Book(bookId, "Sample Title", true)

        @Test
        fun `returnBook - when book is not found then return BookNotFound`() {
            every { bookService.getBookById(bookId) } returns null

            val result = checkoutService.returnBook(userId, bookId)
            expectThat(result).isA<ReturnResult.BookNotFound>()
            expectThat((result as ReturnResult.BookNotFound).message)
                .isEqualTo("Book with ID $bookId does not exist in our collection")
        }

        @Test
        fun `returnBook - when book is not checked out then return BookNotCheckedOut`() {
            every { bookService.getBookById(bookId) } returns book
            every { checkoutRepository.isBookCheckedOutByUser(userId, bookId) } returns false

            val result = checkoutService.returnBook(userId, bookId)

            expectThat(result).isA<ReturnResult.BookNotCheckedOut>()
            expectThat((result as ReturnResult.BookNotCheckedOut).message)
                .isEqualTo("Book '${book.title}' is not checked out by user $userId")
        }

        @Test
        fun `returnBook - when book is returned successful then return success`() {
            every { bookService.getBookById(bookId) } returns book
            every { checkoutRepository.isBookCheckedOutByUser(userId, bookId) } returns true
            every { checkoutRepository.returnBook(userId, bookId) } returns Unit

            val result = checkoutService.returnBook(userId, bookId)

            expectThat(result).isA<ReturnResult.Success>()
            expectThat((result as ReturnResult.Success).message)
                .isEqualTo("Book '${book.title}' returned successfully!")
        }
    }
}
