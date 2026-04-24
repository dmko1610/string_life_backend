package dmitrykovalev.stringlife.routes

import dmitrykovalev.stringlife.models.SessionRequest
import dmitrykovalev.stringlife.models.SessionUpdateRequest
import dmitrykovalev.stringlife.repositories.SessionRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.sessionRoutes(repository: SessionRepository) {
    route("/sessions") {
        get {
            val instrumentId = call.request.queryParameters["instrumentId"]?.let { UUID.fromString(it) }
            call.respond(repository.findAll(instrumentId))
        }

        post {
            val request = call.receive<SessionRequest>()
            call.respond(HttpStatusCode.Created, repository.create(request))
        }

        get("/{id}") {
            val id = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Missing id"))
            call.respond(repository.findById(id))
        }

        put("/{id}") {
            val id = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Missing id"))
            val request = call.receive<SessionUpdateRequest>()
            call.respond(repository.update(id, request))
        }

        delete("/{id}") {
            val id = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Missing id"))
            repository.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
