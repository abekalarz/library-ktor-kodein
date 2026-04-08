package com.example.library.services

import com.example.library.domain.DeleteUserResult
import com.example.library.domain.User
import com.example.library.repository.CheckoutRepository
import com.example.library.repository.UserRepository

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
