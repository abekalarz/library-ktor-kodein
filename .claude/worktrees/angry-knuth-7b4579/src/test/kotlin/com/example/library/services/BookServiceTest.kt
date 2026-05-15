package com.example.library.services

import com.example.library.domain.BookAvailabilityResponse
import com.example.library.domain.BookStatus
import com.example.library.domain.User
import com.example.library.repository.BookRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import strikt.assertions.isNotNull

class BookServiceTest {
    private val bookRepository = mockk<BookRepository>()
    private val bookService = BookService(bookRepository)

    @Nested
    inner class ListBooksWithAvailabilityTests {

        @Test
        fun `listBooksWithAvailability - when book is available then return response with AVAILABLE status`() {
            val availableBook = BookAvailabilityResponse(
                bookId = 1,
                title = "Clean Code",
                status = BookStatus.AVAILABLE,
                checkedOutTo = null
            )
            every { bookRepository.listBooksWithAvailability() } returns listOf(availableBook)

            val result = bookService.listBooksWithAvailability()

            expectThat(result[0].status).isEqualTo(BookStatus.AVAILABLE)
            expectThat(result[0].checkedOutTo).isNull()
        }

        @Test
        fun `listBooksWithAvailability - when book is checked out then return response with CHECKED_OUT status`() {
            val checkedOutUser = User(userId = 2, name = "John Doe")
            val checkedOutBook = BookAvailabilityResponse(
                bookId = 1,
                title = "Clean Code",
                status = BookStatus.CHECKED_OUT,
                checkedOutTo = checkedOutUser
            )
            every { bookRepository.listBooksWithAvailability() } returns listOf(checkedOutBook)

            val result = bookService.listBooksWithAvailability()

            expectThat(result[0].status).isEqualTo(BookStatus.CHECKED_OUT)
            expectThat(result[0].checkedOutTo).isNotNull().isEqualTo(checkedOutUser)
        }
    }
}
