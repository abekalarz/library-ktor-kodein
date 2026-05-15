package com.example.library.domain

data class User(
    val userId: Int,
    val name: String,
    val surname: String,
    val username: String,
)

sealed class RegisterUserResult {
    object DuplicateUsername : RegisterUserResult()
    object Success : RegisterUserResult()
    object Failure : RegisterUserResult()
}

sealed class DeleteUserResult {
    object Success : DeleteUserResult()
    object UserNotFound : DeleteUserResult()
    object HasActiveCheckouts : DeleteUserResult()
}
