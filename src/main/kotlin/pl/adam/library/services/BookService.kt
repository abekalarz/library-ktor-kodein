package pl.adam.library.services

import pl.adam.library.db.DatabaseFactory

data class Book(val id: Int, val title: String, val available: Boolean)

class BookService(private val db: DatabaseFactory) {
    fun listBooks(titleFilter: String? = null): List<Book> = db.jdbi.withHandle<List<Book>, Exception> { handle ->
        val sql = if (titleFilter != null)
            "SELECT * FROM books WHERE title LIKE CONCAT('%', ?, '%')" else "SELECT * FROM books"
        handle.createQuery(sql).apply {
            if (titleFilter != null) bind(0, titleFilter)
        }.mapToBean(Book::class.java).list()
    }
}
