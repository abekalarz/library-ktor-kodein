package com.example.library.repository

import com.example.library.db.DatabaseFactory
import com.example.library.services.Book
import kotlin.collections.List

class BookRepository(private val db: DatabaseFactory) {

    fun listBooks(titleFilter: String? = null): List<Book> = db.jdbi.withHandle<List<Book>, Exception> { handle ->
        val sql = if (titleFilter != null)
            "SELECT * FROM books WHERE title LIKE CONCAT('%', ?, '%')" else "SELECT * FROM books"
        handle.createQuery(sql).apply {
            if (titleFilter != null) bind(0, titleFilter)
        }.map { rs, _ ->
            Book(
                id = rs.getInt("id"),
                title = rs.getString("title"),
                available = rs.getBoolean("available")
            )
        }.list()
    }

}