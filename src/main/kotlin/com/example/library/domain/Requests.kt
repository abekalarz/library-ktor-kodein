package com.example.library.domain

data class AddBookRequest(val title: String)

data class RegisterUserRequest(val name: String)

data class CheckoutRequest(val userId: Int, val bookId: Int)

data class ReturnRequest(val userId: Int, val bookId: Int)
