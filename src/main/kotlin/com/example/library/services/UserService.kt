package com.example.library.services

import com.example.library.repository.UserRepository
import com.sun.jna.platform.win32.Netapi32Util

class UserService(
    private val userRepository: UserRepository
) {
//    fun registerUser(name: String) {
//        db.jdbi.useHandle<Exception> { handle ->
//            handle.execute("INSERT INTO users (name) VALUES (?)", name)
//        }
//    }
    fun registerUser(name: String) {
        userRepository.registerUser(name)
    }
}
