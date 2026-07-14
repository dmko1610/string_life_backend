package dmitrykovalev.stringlife.routes

import dmitrykovalev.stringlife.models.InstrumentRequest
import dmitrykovalev.stringlife.repositories.InstrumentRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import java.util.UUID

@OptIn(ExperimentalKtorApi::class)
fun Route.instrumentRoutes(repository: InstrumentRepository) {
    route("/instruments") {
        get {
            val updatedSince = call.request.queryParameters["updatedSince"]?.toLongOrNull()
            call.respond(repository.findAll(updatedSince))
        }.describe {
            operationId = "listInstruments"
            summary = "List instruments"
            description = "Returns all instruments, optionally filtered by update timestamp."
            tag("Instruments")
            parameters {
                query("updatedSince") {
                    description =
                        "Optional timestamp. When provided, only instruments updated after this value are returned."
                    required = false
                }
            }
            responses {
                response(HttpStatusCode.OK.value) {
                    description = "Instrument list."
                }
                response(HttpStatusCode.BadRequest.value) {
                    description = "Invalid query parameter."
                }
            }
        }

        post {
            val request = call.receive<InstrumentRequest>()
            call.respond(HttpStatusCode.Created, repository.create(request))
        }.describe {
            operationId = "createInstrument"
            summary = "Create instrument"
            description = "Creates a new instrument."
            tag("Instruments")
            responses {
                response(HttpStatusCode.Created.value) {
                    description = "Instrument created."
                }
                response(HttpStatusCode.BadRequest.value) {
                    description = "Invalid instrument payload."
                }
            }
        }

        get("/{id}") {
            val id = UUID.fromString(
                call.parameters["id"] ?: throw IllegalArgumentException("Missing id")
            )
            call.respond(repository.findById(id))
        }.describe {
            operationId = "getInstrument"
            summary = "Get instrument"
            description = "Returns one instrument by ID."
            tag("Instruments")
            responses {
                response(HttpStatusCode.OK.value) {
                    description = "Instrument found."
                }
                response(HttpStatusCode.BadRequest.value) {
                    description = "Invalid instrument ID."
                }
                response(HttpStatusCode.NotFound.value) {
                    description = "Instrument not found."
                }
            }
        }

        put("/{id}") {
            val id = UUID.fromString(
                call.parameters["id"] ?: throw IllegalArgumentException("Missing id")
            )
            val request = call.receive<InstrumentRequest>()
            call.respond(repository.update(id, request))
        }.describe {
            operationId = "updateInstrument"
            summary = "Update instrument"
            description = "Updates an existing instrument by ID."
            tag("Instruments")
            responses {
                response(HttpStatusCode.OK.value) {
                    description = "Instrument updated."
                }
                response(HttpStatusCode.BadRequest.value) {
                    description = "Invalid instrument ID or payload."
                }
                response(HttpStatusCode.NotFound.value) {
                    description = "Instrument not found."
                }
            }
        }

        delete("/{id}") {
            val id = UUID.fromString(
                call.parameters["id"] ?: throw IllegalArgumentException("Missing id")
            )
            repository.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }.describe {
            operationId = "deleteInstrument"
            summary = "Delete instrument"
            description = "Soft-deletes an instrument and its sessions by ID."
            tag("Instruments")
            responses {
                response(HttpStatusCode.NoContent.value) {
                    description = "Instrument deleted."
                }
                response(HttpStatusCode.BadRequest.value) {
                    description = "Invalid instrument ID."
                }
                response(HttpStatusCode.NotFound.value) {
                    description = "Instrument not found."
                }
            }
        }
    }
}
