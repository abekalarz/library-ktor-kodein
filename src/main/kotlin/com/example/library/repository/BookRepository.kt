package com.example.library.repository

import com.example.library.db.DatabaseFactory
import com.example.library.services.Book
import com.example.library.services.BookAvailabilityResponse
import com.example.library.services.CheckedOutTo

class BookRepository(
    private val db: DatabaseFactory
) {
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

    fun listBooksWithAvailability(): List<BookAvailabilityResponse> =
        db.jdbi.withHandle<List<BookAvailabilityResponse>, Exception> { handle ->
            handle.createQuery(
                """
                SELECT b.id AS book_id, b.title, b.available, u.id AS user_id, u.name AS username
                FROM books b
                LEFT JOIN checkouts c ON b.id = c.book_id
                LEFT JOIN users u ON c.user_id = u.id
                """.trimIndent()
            ).map { rs, _ ->
                val available = rs.getBoolean("available")
                BookAvailabilityResponse(
                    bookId = rs.getInt("book_id"),
                    title = rs.getString("title"),
                    status = if (available) "Available" else "Checked out",
                    checkedOutTo = if (!available) CheckedOutTo(
                        userId = rs.getInt("user_id"),
                        username = rs.getString("username")
                    ) else null
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

    fun deleteBook(bookId: Int) {
        db.jdbi.useHandle<Exception> { handle ->
            handle.createUpdate("DELETE FROM books WHERE id = ?")
                .bind(0, bookId)
                .execute()
        }
    }
}
