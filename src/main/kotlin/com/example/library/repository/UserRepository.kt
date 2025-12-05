package com.example.library.repository

import com.example.library.db.DatabaseFactory

class UserRepository (private val db: DatabaseFactory) {
    fun registerUser(name: String) {
        db.jdbi.useHandle<Exception> { handle ->
            handle.execute("INSERT INTO users (name) VALUES (?)", name)
        }
    }
}