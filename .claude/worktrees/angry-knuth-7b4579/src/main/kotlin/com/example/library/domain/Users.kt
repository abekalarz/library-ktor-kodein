package com.example.library.domain

data class User(
    val userId: Int,
    val name: String
)

sealed class DeleteUserResult {
    object Success : DeleteUserResult()
    object UserNotFound : DeleteUserResult()
    object HasActiveCheckouts : DeleteUserResult()
}
