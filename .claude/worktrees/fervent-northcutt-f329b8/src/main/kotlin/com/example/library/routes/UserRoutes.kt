package com.example.library.routes

import com.example.library.domain.CheckoutRequest
import com.example.library.domain.CheckoutResult
import com.example.library.domain.DeleteUserResult
import com.example.library.domain.RegisterUserRequest
import com.example.library.domain.ReturnRequest
import com.example.library.domain.ReturnResult
import com.example.library.services.BookService
import com.example.library.services.CheckoutService
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

fun Route.userRoutes() {
    val userService by closestDI().instance<UserService>()
    val bookService by closestDI().instance<BookService>()
    val checkoutService by closestDI().instance<CheckoutService>()

    route("/users") {
        get {
            val users = userService.getAllUsers()
            call.respond(users)
        }

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

            val userId = userService.registerUser(req)
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

        delete("/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()

            val errors = listOfNotNull(
                Validators.positive("userId", userId)
            )

            if (errors.isNotEmpty()) {
                call.respondValidationError(errors)
                return@delete
            }

            when (userService.deleteUser(userId!!)) {
                is DeleteUserResult.Success -> call.respondText("User $userId deleted successfully")
                is DeleteUserResult.UserNotFound -> call.respondText("User with ID $userId does not exist", status = HttpStatusCode.NotFound)
                is DeleteUserResult.HasActiveCheckouts -> call.respondText("Cannot delete user with active checkouts", status = HttpStatusCode.Conflict)
            }
        }
    }

    route("/books") {
        get {
            val title = call.request.queryParameters["title"]
            call.respond(bookService.listBooks(title))
        }

        get("/availability") {
            call.respond(bookService.listBooksWithAvailability())
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
                is CheckoutResult.AlreadyCheckedOut -> call.respondText(result.message, status = HttpStatusCode.Conflict)
                is CheckoutResult.CheckoutLimitExceeded -> call.respondText(result.message, status = HttpStatusCode.BadRequest)
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
