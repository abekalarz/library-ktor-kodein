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

    fun addBook(title: String): Int = db.jdbi.withHandle<Int, Exception> { handle ->
        handle.createUpdate("INSERT INTO books (title, available) VALUES (?, true)")
            .bind(0, title)
            .executeAndReturnGeneratedKeys("id")
            .mapTo(Int::class.java)
            .one()
    }

    fun getBookById(bookId: Int): Book? = db.jdbi.withHandle<Book?, Exception> { handle ->
        handle.createQuery("SELECT * FROM books WHERE id = ?")
            .bind(0, bookId)
            .map { rs, _ ->
                Book(
                    id = rs.getInt("id"),
                    title = rs.getString("title"),
                    available = rs.getBoolean("available")
                )
            }
            .findOne()
            .orElse(null)
    }

}