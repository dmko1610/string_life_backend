package dmitrykovalev.stringlife.plugins

import dmitrykovalev.stringlife.repositories.InstrumentRepository
import dmitrykovalev.stringlife.repositories.SessionRepository
import dmitrykovalev.stringlife.routes.instrumentRoutes
import dmitrykovalev.stringlife.routes.sessionRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to (cause.message ?: "Bad request")))
        }
        exception<NoSuchElementException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to (cause.message ?: "Not found")))
        }
    }

    val instrumentRepository = InstrumentRepository()
    val sessionRepository = SessionRepository()

    routing {
        route("/api") {
            instrumentRoutes(instrumentRepository)
            sessionRoutes(sessionRepository)
        }
    }
}
