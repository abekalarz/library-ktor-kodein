package com.example.library.repository

import com.example.library.db.DatabaseFactory

class CheckoutRepository(
    private val db: DatabaseFactory
) {
    fun checkoutBook(userId: Int, bookId: Int) {
        db.jdbi.useHandle<Exception> { handle ->
            handle.begin()
            handle.execute("UPDATE books SET available = FALSE WHERE id = ?", bookId)
            handle.execute("INSERT INTO checkouts (user_id, book_id) VALUES (?, ?)", userId, bookId)
            handle.commit()
        }
    }

}