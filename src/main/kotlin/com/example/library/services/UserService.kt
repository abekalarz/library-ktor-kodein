package com.example.library.services

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
    private val userRepository: UserRepository
) {
    fun registerUser(name: String): Int {
        return userRepository.registerUser(name)
    }

    fun getUser(userId: Int): User? {
        return userRepository.getUserById(userId)
    }

    fun deleteUser(userId: Int): DeleteUserResult {
        if (userRepository.getUserById(userId) == null) {
            return DeleteUserResult.UserNotFound
        }
        return try {
            userRepository.deleteUser(userId)
            DeleteUserResult.Success
        } catch (e: Exception) {
            if (e.message?.contains("foreign key constraint", ignoreCase = true) == true) {
                DeleteUserResult.HasActiveCheckouts
            } else {
                throw e
            }
        }
    }
}
