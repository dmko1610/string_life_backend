package dmitrykovalev.stringlife.routes

import dmitrykovalev.stringlife.models.SessionRequest
import dmitrykovalev.stringlife.models.SessionUpdateRequest
import dmitrykovalev.stringlife.repositories.SessionRepository
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
fun Route.sessionRoutes(repository: SessionRepository) {
    route("/sessions") {
        get {
            val instrumentId =
                call.request.queryParameters["instrumentId"]?.let { UUID.fromString(it) }
            call.respond(repository.findAll(instrumentId))
        }.describe {
            operationId = "listSessions"
            summary = "List sessions"
            description = "Returns sessions, optionally filtered by instrument ID."
            tag("Sessions")
            parameters {
                query("instrumentId") {
                    description = "Optional instrument UUID used to filter sessions."
                    required = false
                }
            }
            responses {
                response(HttpStatusCode.OK.value) {
                    description = "Session list."
                }
                response(HttpStatusCode.BadRequest.value) {
                    description = "Invalid instrument ID."
                }
            }
        }

        post {
            val request = call.receive<SessionRequest>()
            call.respond(HttpStatusCode.Created, repository.create(request))
        }.describe {
            operationId = "createSession"
            summary = "Create session"
            description = "Creates a new practice session."
            tag("Sessions")
            responses {
                response(HttpStatusCode.Created.value) {
                    description = "Session created."
                }
                response(HttpStatusCode.BadRequest.value) {
                    description = "Invalid session payload."
                }
                response(HttpStatusCode.NotFound.value) {
                    description = "Instrument not found."
                }
            }
        }

        get("/{id}") {
            val id = UUID.fromString(
                call.parameters["id"] ?: throw IllegalArgumentException("Missing id")
            )
            call.respond(repository.findById(id))
        }.describe {
            operationId = "getSession"
            summary = "Get session"
            description = "Returns one session by ID."
            tag("Sessions")
            responses {
                response(HttpStatusCode.OK.value) {
                    description = "Session found."
                }
                response(HttpStatusCode.BadRequest.value) {
                    description = "Invalid session ID."
                }
                response(HttpStatusCode.NotFound.value) {
                    description = "Session not found."
                }
            }
        }

        put("/{id}") {
            val id = UUID.fromString(
                call.parameters["id"] ?: throw IllegalArgumentException("Missing id")
            )
            val request = call.receive<SessionUpdateRequest>()
            call.respond(repository.update(id, request))
        }.describe {
            operationId = "updateSession"
            summary = "Update session"
            description = "Updates an existing session by ID."
            tag("Sessions")
            responses {
                response(HttpStatusCode.OK.value) {
                    description = "Session updated."
                }
                response(HttpStatusCode.BadRequest.value) {
                    description = "Invalid session ID or payload."
                }
                response(HttpStatusCode.NotFound.value) {
                    description = "Session not found."
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
            operationId = "deleteSession"
            summary = "Delete session"
            description = "Soft-deletes a session by ID."
            tag("Sessions")
            responses {
                response(HttpStatusCode.NoContent.value) {
                    description = "Session deleted."
                }
                response(HttpStatusCode.BadRequest.value) {
                    description = "Invalid session ID."
                }
                response(HttpStatusCode.NotFound.value) {
                    description = "Session not found."
                }
            }
        }
    }
}
