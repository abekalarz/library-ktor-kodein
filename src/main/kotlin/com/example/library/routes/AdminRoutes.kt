package com.example.library.routes

import com.example.library.services.BookService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

data class AddBookRequest(val title: String)

fun Route.adminRoutes() {
    val bookService by closestDI().instance<BookService>()

    route("/admin/books") {
        post {
            val req = call.receive<AddBookRequest>()
            val bookId = bookService.addBook(req.title)
            call.respondText("Book added with ID: $bookId")
        }
    }
}
