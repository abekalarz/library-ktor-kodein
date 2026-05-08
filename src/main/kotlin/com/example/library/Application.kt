package com.example.library

import com.example.library.db.DatabaseFactory
import com.example.library.db.JdbiTransactionManager
import com.example.library.db.TransactionManager
import com.example.library.repository.BookRepository
import com.example.library.repository.CheckoutRepository
import com.example.library.repository.UserRepository
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.callloging.*
import io.ktor.serialization.jackson.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.kodein.di.*
import org.kodein.di.ktor.di
import org.kodein.di.ktor.closestDI
import com.example.library.routes.userRoutes
import com.example.library.routes.adminRoutes
import com.example.library.services.BookService
import com.example.library.services.CheckoutService
import com.example.library.services.UserService
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.http.content.*


fun main() {
    embeddedServer(
        factory = Netty,
        host = "0.0.0.0",
        port = 8080
    ) {
        install(CallLogging)
        install(ContentNegotiation) { jackson() }
        install(CORS) {
            allowHost("localhost:8080")
            allowHost("127.0.0.1:8080")
            allowHeader(HttpHeaders.ContentType)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Options)
        }

        di {
            bind<BookRepository>() with singleton { BookRepository(instance()) }
            bind<UserRepository>() with singleton { UserRepository(instance()) }
            bind<CheckoutRepository>() with singleton { CheckoutRepository(instance()) }
            bind<DatabaseFactory>() with singleton { DatabaseFactory() }
            bind<TransactionManager>() with singleton { JdbiTransactionManager(instance()) }
            bind<UserService>() with singleton { UserService(instance(), instance(), instance()) }
            bind<BookService>() with singleton { BookService(instance()) }
            bind<CheckoutService>() with singleton { CheckoutService(instance(), instance(), instance()) }
        }

        routing {
            userRoutes()
            adminRoutes()
            staticResources("", "static")
        }

        environment.monitor.subscribe(ApplicationStarted) { application ->
            val db = application.closestDI().direct.instance<DatabaseFactory>()
            db.init()
        }
    }.start(wait = true)
}
