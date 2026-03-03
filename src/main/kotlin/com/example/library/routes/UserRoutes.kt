package com.example.library.routes

import com.example.library.services.BookService
import com.example.library.services.CheckoutService
import com.example.library.services.CheckoutResult
import com.example.library.services.ReturnResult
import com.example.library.services.UserService
import com.example.library.api.ErrorResponse
import com.example.library.validation.Validators
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

data class RegisterUserRequest(val name: String)
data class CheckoutRequest(val userId: Int, val bookId: Int)
data class ReturnRequest(val userId: Int, val bookId: Int)

fun Route.userRoutes() {
    val userService by closestDI().instance<UserService>()
    val bookService by closestDI().instance<BookService>()
    val checkoutService by closestDI().instance<CheckoutService>()

    route("/users") {
        post {
            val req = call.receive<RegisterUserRequest>()

            val errors = listOfNotNull(
                Validators.notBlank("name", req.name),
                Validators.minLength("name", req.name, 2)
            )

            if (errors.isNotEmpty()) {
                call.respondValidationError(errors)
                return@post
            }

            val userId = userService.registerUser(req.name)
            call.respondText("User registered: ${req.name} (ID: $userId)")
        }

        get("/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respondText("Invalid user ID", status = HttpStatusCode.BadRequest)
            } else {
                val user = userService.getUser(userId)
                if (user == null) {
                    call.respondText("User was not found", status = HttpStatusCode.NotFound)
                } else {
                    call.respond(user)
                }
            }
        }
    }

    route("/books") {
        get {
            val title = call.request.queryParameters["title"]
            call.respond(bookService.listBooks(title))
        }
    }

    route("/checkout") {
        post {
            val req = call.receive<CheckoutRequest>()

            val errors = listOfNotNull(
                Validators.positive("userId", req.userId),
                Validators.positive("bookId", req.bookId)
            )

            if (errors.isNotEmpty()) {
                call.respondValidationError(errors)
                return@post
            }

            when (val result = checkoutService.checkoutBook(req.userId, req.bookId)) {
                is CheckoutResult.Success -> call.respondText(result.message, status = HttpStatusCode.OK)
                is CheckoutResult.BookNotFound -> call.respondText(result.message, status = HttpStatusCode.NotFound)
                is CheckoutResult.BookNotAvailable -> call.respondText(result.message, status = HttpStatusCode.Conflict)
                is CheckoutResult.UserNotFound -> call.respondText(result.message, status = HttpStatusCode.NotFound)
            }
        }
    }

    route("/return") {
        post {
            val req = call.receive<ReturnRequest>()

            val errors = listOfNotNull(
                Validators.positive("userId", req.userId),
                Validators.positive("bookId", req.bookId)
            )

            if (errors.isNotEmpty()) {
                call.respondValidationError(errors)
                return@post
            }

            when (val result = checkoutService.returnBook(req.userId, req.bookId)) {
                is ReturnResult.Success -> call.respondText(result.message, status = HttpStatusCode.OK)
                is ReturnResult.BookNotFound -> call.respondText(result.message, status = HttpStatusCode.NotFound)
                is ReturnResult.BookNotCheckedOut -> call.respondText(result.message, status = HttpStatusCode.BadRequest)
            }
        }
    }
}
