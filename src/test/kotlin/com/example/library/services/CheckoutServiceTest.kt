package com.example.library.services

import com.example.library.repository.CheckoutRepository
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

        expectThat(result).isA<CheckoutResult.UserNotFound>()
        expectThat((result as CheckoutResult.UserNotFound).message).isEqualTo("User with ID $userId does not exist")
    }
}
