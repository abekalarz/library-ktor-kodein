package com.example.library.services

import com.example.library.repository.UserRepository

class UserService(
    private val userRepository: UserRepository
) {
    fun registerUser(name: String) {
        userRepository.registerUser(name)
    }
}
