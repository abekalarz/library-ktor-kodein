package com.example.library.repository

import com.example.library.db.DatabaseFactory
import com.example.library.domain.RegisterUserRequest
import com.example.library.domain.User
import org.jdbi.v3.core.Handle

class UserRepository (private val db: DatabaseFactory) {
    fun registerUser(userRequest: RegisterUserRequest): Int {
        // TODO Move this at service-level and apply 'Validators' !
//        require(userRequest.isNotBlank()) { "User name cannot be empty" }

        return db.jdbi.withHandle<Int, Exception> { handle ->
            handle.createUpdate("INSERT INTO users (name, surname, username) VALUES (:name, :surname, :username)")
                .bind("name", userRequest.name)
                .bind("surname", userRequest.surname)
                .bind("username", userRequest.username)
                .execute()
            handle.createQuery("SELECT LAST_INSERT_ID() as id")
                .mapTo(Int::class.java)
                .one()
        }
    }

    fun getUserById(userId: Int, handle: Handle? = null): User? {
        val query = { h: Handle ->
            h.createQuery("SELECT id, name FROM users WHERE id = :userId AND is_active = TRUE")
                .bind("userId", userId)
                .map { rs, _ ->
                    User(
                        userId = rs.getInt("id"),
                        name = rs.getString("name"),
                        surname = rs.getString("surname"),
                        username = rs.getString("username"),
                    )
                }
                .findFirst()
                .orElse(null)
        }

        return handle?.let(query) ?: db.jdbi.withHandle<User?, Exception>(query)
    }

    fun getUserByUsername(username: String): User? {
        return db.jdbi.withHandle<User?, Exception> { handle ->
            handle.createQuery("SELECT id, name, surname, username FROM users WHERE username = :username AND is_active = TRUE")
                .bind("username", username)
                .map { rs, _ ->
                    User(
                        userId = rs.getInt("id"),
                        name = rs.getString("name"),
                        surname = rs.getString("surname"),
                        username = rs.getString("username"),
                    )
                }
                .findFirst()
                .orElse(null)
        }
    }

    fun getAllUsers(): List<User> {
        return db.jdbi.withHandle<List<User>, Exception> { handle ->
            handle.createQuery("SELECT id, name, surname, username FROM users WHERE is_active = TRUE")
                .map { rs, _ ->
                    User(
                        userId = rs.getInt("id"),
                        name = rs.getString("name"),
                        surname = rs.getString("surname"),
                        username = rs.getString("username")
                    )
                }
                .list()
        }
    }

    fun softDeleteUser(userId: Int, handle: Handle? = null) {
        val update = { h: Handle ->
            h.createUpdate(
                "UPDATE users SET is_active = FALSE, deleted_at = CURRENT_TIMESTAMP WHERE id = :userId"
            )
                .bind("userId", userId)
                .execute()
        }

        handle?.let(update) ?: db.jdbi.withHandle<Int, Exception>(update)
    }
}
