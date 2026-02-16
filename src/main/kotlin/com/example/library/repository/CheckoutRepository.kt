package com.example.library.repository

import com.example.library.db.DatabaseFactory
import com.example.library.services.Book

sealed class CheckoutRepositoryResult {
    data class Success(val book: Book) : CheckoutRepositoryResult()
    object BookNotFound : CheckoutRepositoryResult()
    object BookNotAvailable : CheckoutRepositoryResult()
}

class CheckoutRepository(
    private val db: DatabaseFactory
) {
    fun checkoutBook(userId: Int, bookId: Int): CheckoutRepositoryResult {
        return db.jdbi.inTransaction<CheckoutRepositoryResult, Exception> { handle ->
            val book = handle.createQuery(
                "SELECT id, title, available FROM books WHERE id = :bookId FOR UPDATE"
            )
                .bind("bookId", bookId)
                .map { rs, _ ->
                    Book(
                        id = rs.getInt("id"),
                        title = rs.getString("title"),
                        available = rs.getBoolean("available")
                    )
                }
                .findOne()
                .orElse(null)

            if (book == null) {
                return@inTransaction CheckoutRepositoryResult.BookNotFound
            }

            if (!book.available) {
                return@inTransaction CheckoutRepositoryResult.BookNotAvailable
            }

            handle.execute("UPDATE books SET available = FALSE WHERE id = :bookId", mapOf("bookId" to bookId))
            handle.execute("INSERT INTO checkouts (user_id, book_id) VALUES (:userId, :bookId)", mapOf("userId" to userId, "bookId" to bookId))

            CheckoutRepositoryResult.Success(book)
        }
    }

    fun isBookCheckedOutByUser(userId: Int, bookId: Int): Boolean {
        return db.jdbi.withHandle<Boolean, Exception> { handle ->
            val count = handle.createQuery(
                "SELECT COUNT(*) FROM checkouts WHERE user_id = :userId AND book_id = :bookId"
            )
                .bind("userId", userId)
                .bind("bookId", bookId)
                .mapTo(Int::class.java)
                .one()
            count > 0
        }
    }

    fun returnBook(userId: Int, bookId: Int) {
        db.jdbi.useHandle<Exception> { handle ->
            handle.begin()
            handle.execute("UPDATE books SET available = TRUE WHERE id = :bookId", mapOf("bookId" to bookId))
            handle.execute("DELETE FROM checkouts WHERE user_id = :userId AND book_id = :bookId", mapOf("userId" to userId, "bookId" to bookId))
            handle.commit()
        }
    }

}