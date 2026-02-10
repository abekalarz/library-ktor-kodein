package com.example.library.repository

import com.example.library.db.DatabaseFactory
import com.example.library.services.User

class UserRepository (private val db: DatabaseFactory) {
    fun registerUser(name: String) {
        db.jdbi.useHandle<Exception> { handle ->
            handle.execute("INSERT INTO users (name) VALUES (?)", name)
        }
    }

    fun getUserById(userId: Int): User? {
        return db.jdbi.withHandle<User?, Exception> { handle ->
            handle.createQuery("SELECT * FROM users WHERE id = :userId")
                .bind("userId", userId)
                .map { rs, _ ->
                    User(
                        userId = rs.getInt("id"),
                        name = rs.getString("name"),
                    )
                }
                .findFirst()
                .orElse(null)
        }
    }
}