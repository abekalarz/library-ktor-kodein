package com.example.library.routes

import com.example.library.domain.AddBookRequest
import com.example.library.domain.DeleteBookResult
import com.example.library.services.BookService
import com.example.library.validation.Validators
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.adminRoutes() {
    val bookService by closestDI().instance<BookService>()

    route("/admin/books") {
        post {
            val req = call.receive<AddBookRequest>()

            val errors = listOfNotNull(
                Validators.notBlank("title", req.title),
                Validators.minLength("title", req.title, 3)
            )

            if (errors.isNotEmpty()) {
                call.respondValidationError(errors)
                return@post
            }

            val bookId = bookService.addBook(req.title)
            call.respondText("Book added with ID: $bookId")
        }

        delete("/{bookId}") {
            val bookId = call.parameters["bookId"]?.toIntOrNull()

            val errors = listOfNotNull(
                Validators.positive("bookId", bookId)
            )

            if (errors.isNotEmpty()) {
                call.respondValidationError(errors)
                return@delete
            }

            when (bookService.deleteBook(bookId!!)) {
                is DeleteBookResult.Success -> call.respondText("Book $bookId deleted successfully")
                is DeleteBookResult.BookNotFound -> call.respondText("Book with ID $bookId does not exist", status = HttpStatusCode.NotFound)
                is DeleteBookResult.BookIsCheckedOut -> call.respondText("Cannot delete book that is currently checked out", status = HttpStatusCode.Conflict)
            }
        }
    }
}
