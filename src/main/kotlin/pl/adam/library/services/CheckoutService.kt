package pl.adam.library.services

import pl.adam.library.db.DatabaseFactory

class CheckoutService(private val db: DatabaseFactory) {
    fun checkoutBook(userId: Int, bookId: Int) {
        db.jdbi.useHandle<Exception> { handle ->
            handle.begin()
            val available = handle.createQuery("SELECT available FROM books WHERE id = ?")
                .bind(0, bookId).mapTo(Boolean::class.java).findOne().orElse(false)
            if (!available) throw IllegalStateException("Book is not available")
            handle.execute("UPDATE books SET available = FALSE WHERE id = ?", bookId)
            handle.execute("INSERT INTO checkouts (user_id, book_id) VALUES (?, ?)", userId, bookId)
            handle.commit()
        }
    }
}
