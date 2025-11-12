package pl.adam.library.services

import pl.adam.library.db.DatabaseFactory

class UserService(private val db: DatabaseFactory) {
    fun registerUser(name: String) {
        db.jdbi.useHandle<Exception> { handle ->
            handle.execute("INSERT INTO users (name) VALUES (?)", name)
        }
    }
}
