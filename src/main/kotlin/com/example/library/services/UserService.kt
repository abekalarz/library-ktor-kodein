package com.example.library.services

import com.example.library.repository.CheckoutRepository
import com.example.library.repository.UserRepository

data class User(
    val userId: Int,
    val name: String
)

sealed class DeleteUserResult {
    object Success : DeleteUserResult()
    object UserNotFound : DeleteUserResult()
    object HasActiveCheckouts : DeleteUserResult()
}

class UserService(
    private val userRepository: UserRepository,
    private val checkoutRepository: CheckoutRepository
) {
    fun registerUser(name: String): Int {
        return userRepository.registerUser(name)
    }

    fun getUser(userId: Int): User? {
        return userRepository.getUserById(userId)
    }

    fun getAllUsers(): List<User> {
        return userRepository.getAllUsers()
    }

    fun deleteUser(userId: Int): DeleteUserResult {
        if (userRepository.getUserById(userId) == null) {
            return DeleteUserResult.UserNotFound
        }
        if (checkoutRepository.hasActiveCheckouts(userId)) {
            return DeleteUserResult.HasActiveCheckouts
        }
        userRepository.softDeleteUser(userId)
        return DeleteUserResult.Success
    }
}
