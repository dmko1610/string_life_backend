package dmitrykovalev.stringlife.routes

import dmitrykovalev.stringlife.models.InstrumentRequest
import dmitrykovalev.stringlife.repositories.InstrumentRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.instrumentRoutes(repository: InstrumentRepository) {
    route("/instruments") {
        get {
            call.respond(repository.findAll())
        }

        post {
            val request = call.receive<InstrumentRequest>()
            call.respond(HttpStatusCode.Created, repository.create(request))
        }

        get("/{id}") {
            val id = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Missing id"))
            call.respond(repository.findById(id))
        }

        put("/{id}") {
            val id = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Missing id"))
            val request = call.receive<InstrumentRequest>()
            call.respond(repository.update(id, request))
        }

        delete("/{id}") {
            val id = UUID.fromString(call.parameters["id"] ?: throw IllegalArgumentException("Missing id"))
            repository.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
