package com.example.library.services

import com.example.library.repository.BookRepository

data class Book(val id: Int, val title: String, val available: Boolean)

class BookService(
    private val bookRepository: BookRepository
) {
//    fun listBooks(titleFilter: String? = null): List<Book> = db.jdbi.withHandle<List<Book>, Exception> { handle ->
//        val sql = if (titleFilter != null)
//            "SELECT * FROM books WHERE title LIKE CONCAT('%', ?, '%')" else "SELECT * FROM books"
//        handle.createQuery(sql).apply {
//            if (titleFilter != null) bind(0, titleFilter)
//        }.map { rs, _ ->
//            Book(
//                id = rs.getInt("id"),
//                title = rs.getString("title"),
//                available = rs.getBoolean("available")
//            )
//        }.list()
//    }

    fun listBooks(titleFilter: String? = null): List<Book> = bookRepository.listBooks(titleFilter)

//    fun addBook(title: String): Int = db.jdbi.withHandle<Int, Exception> { handle ->
//        handle.createUpdate("INSERT INTO books (title, available) VALUES (?, true)")
//            .bind(0, title)
//            .executeAndReturnGeneratedKeys("id")
//            .mapTo(Int::class.java)
//            .one()
//    }
    fun addBook(title: String) {
        bookRepository.addBook(title)
    }
    
//    fun getBookById(bookId: Int): Book? = db.jdbi.withHandle<Book?, Exception> { handle ->
//        handle.createQuery("SELECT * FROM books WHERE id = ?")
//            .bind(0, bookId)
//            .map { rs, _ ->
//                Book(
//                    id = rs.getInt("id"),
//                    title = rs.getString("title"),
//                    available = rs.getBoolean("available")
//                )
//            }
//            .findOne()
//            .orElse(null)
//    }
    fun getBookById(bookId: Int) = bookRepository.getBookById(bookId)
}
