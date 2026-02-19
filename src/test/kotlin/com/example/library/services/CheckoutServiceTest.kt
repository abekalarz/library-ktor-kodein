package com.example.library.services

import com.example.library.repository.CheckoutRepository
import com.example.library.repository.CheckoutRepositoryResult
import com.example.library.services.CheckoutResult.*
import io.mockk.every
import io.mockk.mockk
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

    @Test
    fun `checkoutBook - when user not found then return UserNotFound`() {
        val userId = 1
        every { userService.getUser(userId) } returns null

        val result = checkoutService.checkoutBook(userId, 1)

        expectThat(result).isA<UserNotFound>()
        expectThat((result as UserNotFound).message).isEqualTo("User with ID $userId does not :D exist")
    }

    // checkoutBook success
    @Test
    fun `checkoutBook - repository result success then return success` () {
        val userId = 1
        val bookId = 10

        val book = mockk<Book>()
        every { book.title } returns "Sample Title"

        every { userService.getUser(userId) } returns mockk()
        every { checkoutRepository.checkoutBook(userId, bookId) } returns
                CheckoutRepositoryResult.Success(book)

        val result = checkoutService.checkoutBook(userId, bookId)

        expectThat(result).isA<CheckoutResult.Success>()
        expectThat(subject = (result as CheckoutResult.Success).message)
            .isEqualTo("Book '${book.title}' checked out successfully!")

    }

    // checkoutBook book not found
    @Test
    fun `checkoutBook - when book is not found then return BookNotFound`() {
        val userId = 1
        val bookId = 10

        every { userService.getUser(userId) } returns mockk()
        every { checkoutRepository.checkoutBook(userId, bookId) } returns
                CheckoutRepositoryResult.BookNotFound

        val result = checkoutService.checkoutBook(userId, bookId)
        expectThat(result).isA<BookNotFound>()
        expectThat((result as BookNotFound).message)
            .isEqualTo("Book with ID $bookId does not exist in our collection")

    }
    // checkoutBook book not available
    @Test
    fun `checkoutBook - when book is not available then return BookNotAvailable`() {
        val userId = 1
        val bookId = 10

        every { userService.getUser(userId) } returns mockk()
        every { checkoutRepository.checkoutBook(userId, bookId) } returns
                CheckoutRepositoryResult.BookNotAvailable

        val result = checkoutService.checkoutBook(userId, bookId)
        expectThat(result).isA<BookNotAvailable>()
        expectThat((result as BookNotAvailable).message)
            .isEqualTo("Book is currently checked out and not available")
    }

    // returnBook book not found
    @Test
    fun `returnBook - when book is not found then return BookNotFound`() {
        val userId = 1
        val bookId = 10

        every { bookService.getBookById(bookId) } returns null

        val result = checkoutService.returnBook(userId, bookId)
        expectThat(result).isA<ReturnResult.BookNotFound>()
        expectThat((result as ReturnResult.BookNotFound).message)
            .isEqualTo("Book with ID $bookId does not exist in our collection")
    }

    // returnBook book not checked out
    @Test
    fun `returnBook - when book is not checked out then return BookNotCheckedOut` () {
        val userId = 1
        val bookId = 10

        val book = mockk<Book>()
        every { book.title } returns "Sample Title"

        every { bookService.getBookById(bookId) } returns book
        every { checkoutRepository.isBookCheckedOutByUser(userId, bookId) } returns false

        val result = checkoutService.returnBook(userId, bookId)

        expectThat(result).isA<ReturnResult.BookNotCheckedOut>()
        expectThat((result as ReturnResult.BookNotCheckedOut).message)
            .isEqualTo("Book '${book.title}' is not checked out by user $userId")
    }

    // returnBook successful
    @Test
    fun `returnBook - when book is returned successful then return success` () {
        val userId = 1
        val bookId = 10

        val book = mockk<Book>()
        every { book.title } returns "Sample Title"

        every { bookService.getBookById(bookId) } returns book
        every { checkoutRepository.isBookCheckedOutByUser(userId, bookId) } returns true
        every { checkoutRepository.returnBook(userId, bookId) } returns Unit

        val result = checkoutService.returnBook(userId, bookId)

        expectThat(result).isA<ReturnResult.Success>()
        expectThat((result as ReturnResult.Success).message)
            .isEqualTo("Book '${book.title}' returned successfully!")
    }
}
