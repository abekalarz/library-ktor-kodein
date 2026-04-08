package com.example.library.repository

import com.example.library.db.DatabaseFactory
import com.example.library.domain.Book
import com.example.library.domain.CheckoutRepositoryResult

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

            handle.createUpdate("UPDATE books SET available = FALSE WHERE id = :bookId")
                .bindMap(mapOf("bookId" to bookId))
                .execute()

            handle.createUpdate(
                "INSERT INTO checkouts (user_id, book_id) VALUES (:userId, :bookId)"
            )
                .bindMap(mapOf("userId" to userId, "bookId" to bookId))
                .execute()


            CheckoutRepositoryResult.Success(book)
        }
    }

    fun isBookCheckedOutByUser(userId: Int, bookId: Int): Boolean {
        return db.jdbi.withHandle<Boolean, Exception> { handle ->
            val count = handle.createQuery(
                "SELECT COUNT(*) FROM checkouts WHERE user_id = :userId AND book_id = :bookId AND returned_at IS NULL"
            )
                .bind("userId", userId)
                .bind("bookId", bookId)
                .mapTo(Int::class.java)
                .one()
            count > 0
        }
    }

    fun getCheckedOutBooksCount(userId: Int): Int {
        return db.jdbi.withHandle<Int, Exception> { handle ->
            handle.createQuery(
                "SELECT COUNT(*) FROM checkouts WHERE user_id = :userId AND returned_at IS NULL"
            )
                .bind("userId", userId)
                .mapTo(Int::class.java)
                .one()
        }
    }

    fun hasActiveCheckouts(userId: Int): Boolean {
        return db.jdbi.withHandle<Boolean, Exception> { handle ->
            val count = handle.createQuery(
                "SELECT COUNT(*) FROM checkouts WHERE user_id = :userId AND returned_at IS NULL"
            )
                .bind("userId", userId)
                .mapTo(Int::class.java)
                .one()
            count > 0
        }
    }

    fun returnBook(userId: Int, bookId: Int) {
        db.jdbi.inTransaction<Unit, Exception> { handle ->
            handle.createUpdate("UPDATE books SET available = TRUE WHERE id = :bookId")
                .bind("bookId", bookId)
                .execute()
            handle.createUpdate(
                "UPDATE checkouts SET returned_at = CURRENT_TIMESTAMP WHERE user_id = :userId AND book_id = :bookId AND returned_at IS NULL"
            )
                .bind("userId", userId)
                .bind("bookId", bookId)
                .execute()
        }
    }

}

