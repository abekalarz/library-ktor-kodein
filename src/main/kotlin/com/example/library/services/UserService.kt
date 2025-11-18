package com.example.library.services

import com.example.library.db.DatabaseFactory

class UserService(private val db: DatabaseFactory) {
    fun registerUser(name: String) {
        db.jdbi.useHandle<Exception> { handle ->
            handle.execute("INSERT INTO users (name) VALUES (?)", name)
        }
    }
}
