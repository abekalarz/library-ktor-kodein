package com.example.library.repository

import com.example.library.db.DatabaseFactory
import com.example.library.services.User

class UserRepository (private val db: DatabaseFactory) {
    fun registerUser(name: String): Int {
        require(name.isNotBlank()) { "User name cannot be empty" }
        
        return db.jdbi.withHandle<Int, Exception> { handle ->
            handle.createUpdate("INSERT INTO users (name) VALUES (:name)")
                .bind("name", name)
                .execute()
            handle.createQuery("SELECT LAST_INSERT_ID() as id")
                .mapTo(Int::class.java)
                .one()
        }
    }

    fun getUserById(userId: Int): User? {
        return db.jdbi.withHandle<User?, Exception> { handle ->
            handle.createQuery("SELECT id, name FROM users WHERE id = :userId AND is_active = TRUE")
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

    fun getAllUsers(): List<User> {
        return db.jdbi.withHandle<List<User>, Exception> { handle ->
            handle.createQuery("SELECT id, name FROM users WHERE is_active = TRUE")
                .map { rs, _ ->
                    User(
                        userId = rs.getInt("id"),
                        name = rs.getString("name"),
                    )
                }
                .list()
        }
    }

    fun softDeleteUser(userId: Int) {
        db.jdbi.useHandle<Exception> { handle ->
            handle.createUpdate(
                "UPDATE users SET is_active = FALSE, deleted_at = CURRENT_TIMESTAMP WHERE id = :userId"
            )
                .bind("userId", userId)
                .execute()
        }
    }
}