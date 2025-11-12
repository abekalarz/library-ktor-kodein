package pl.adam.library.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import pl.adam.library.services.*

data class RegisterUserRequest(val name: String)
data class CheckoutRequest(val userId: Int, val bookId: Int)

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
    }

    route("/checkout") {
        post {
            val req = call.receive<CheckoutRequest>()
            checkoutService.checkoutBook(req.userId, req.bookId)
            call.respondText("Book checked out successfully!")
        }
    }
}
