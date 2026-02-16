package com.example.library.repository

import com.example.library.db.DatabaseFactory
import com.example.library.services.User

class UserRepository (private val db: DatabaseFactory) {
    fun registerUser(name: String): Int {
        require(name.isNotBlank()) { "User name cannot be empty" }
        
        return db.jdbi.withHandle<Int, Exception> { handle ->
            handle.execute("INSERT INTO users (name) VALUES (:name)", mapOf("name" to name))
            handle.createQuery("SELECT last_insert_rowid() as id")
                .mapTo(Int::class.java)
                .one()
        }
    }

    fun getUserById(userId: Int): User? {
        return db.jdbi.withHandle<User?, Exception> { handle ->
            handle.createQuery("SELECT id, name FROM users WHERE id = :userId")
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