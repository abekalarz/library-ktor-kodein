package com.example.library.routes

import com.example.library.services.BookService
import com.example.library.services.CheckoutService
import com.example.library.services.UserService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

data class RegisterUserRequest(val name: String)
data class CheckoutRequest(val userId: Int, val bookId: Int)
data class AddBookRequest(val title: String)

fun Route.libraryRoutes() {
    val userService by closestDI().instance<UserService>()
    val bookService by closestDI().instance<BookService>()
    val checkoutService by closestDI().instance<CheckoutService>()

    route("/users") {
        post {
            val req = call.receive<RegisterUserRequest>()
            userService.registerUser(req.name)
            call.respondText("User registered: ${req.name}")
        }
    }

    route("/books") {
        get {
            val title = call.request.queryParameters["title"]
            call.respond(bookService.listBooks(title))
        }
        post {
            val req = call.receive<AddBookRequest>()
            val bookId = bookService.addBook(req.title)
            call.respondText("Book added with ID: $bookId")
        }
    }

    route("/checkout") {
        post {
            val req = call.receive<CheckoutRequest>()
            checkoutService.checkoutBook(req.userId, req.bookId)
            call.respondText("Book checked out successfully!")
        }
    }
}
