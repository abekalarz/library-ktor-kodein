package pl.adam.library

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.callloging.*
import io.ktor.serialization.jackson.*
import io.ktor.server.routing.*
import org.kodein.di.*
import org.kodein.di.ktor.di
import org.kodein.di.ktor.closestDI
import pl.adam.library.routes.libraryRoutes
import pl.adam.library.services.*
import pl.adam.library.db.*

fun main() {
    embeddedServer(
        factory = Netty,
        host = "0.0.0.0",
        port = 8080
    ) {
        install(CallLogging)
        install(ContentNegotiation) { jackson() }

        di {
            bind<DatabaseFactory>() with singleton { DatabaseFactory() }
            bind<UserService>() with singleton { UserService(instance()) }
            bind<BookService>() with singleton { BookService(instance()) }
            bind<CheckoutService>() with singleton { CheckoutService(instance()) }
        }

        routing { libraryRoutes() }

        environment.monitor.subscribe(ApplicationStarted) { application ->
            val db = application.closestDI().direct.instance<DatabaseFactory>()
            db.init()
        }
    }.start(wait = true)
}
