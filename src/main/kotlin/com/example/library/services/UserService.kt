package com.example.library.services

import com.example.library.repository.UserRepository

data class User(
    val userId: Int,
    val name: String
)

class UserService(
    private val userRepository: UserRepository
) {
    fun registerUser(name: String) {
        userRepository.registerUser(name)
    }

    fun getUser(userId: Int): User? {
        return userRepository.getUserById(userId)
    }
}
