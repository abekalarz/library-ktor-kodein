package com.example.library.services

import com.example.library.domain.DeleteUserResult
import com.example.library.domain.User
import com.example.library.repository.UserRepository

class UserService(
    private val userRepository: UserRepository
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
