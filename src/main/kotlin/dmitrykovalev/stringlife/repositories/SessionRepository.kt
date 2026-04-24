package dmitrykovalev.stringlife.repositories

import dmitrykovalev.stringlife.db.tables.Sessions
import dmitrykovalev.stringlife.models.Session
import dmitrykovalev.stringlife.models.SessionRequest
import dmitrykovalev.stringlife.models.SessionUpdateRequest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class SessionRepository {

    fun findAll(instrumentId: UUID? = null): List<Session> = transaction {
        val query = Sessions.selectAll()
        instrumentId?.let { query.where { Sessions.instrumentId eq it } }
        query.map { it.toSession() }
    }

    fun findById(id: UUID): Session = transaction {
        Sessions.selectAll()
            .where { Sessions.id eq id }
            .firstOrNull()
            ?.toSession()
            ?: throw NoSuchElementException("Session $id not found")
    }

    fun create(request: SessionRequest): Session = transaction {
        val insertedId = Sessions.insert {
            it[instrumentId] = UUID.fromString(request.instrumentId)
            it[startTime] = Instant.parse(request.startTime)
            it[endTime] = null
            it[notes] = request.notes
            it[createdAt] = Clock.System.now()
        }[Sessions.id]

        findById(insertedId)
    }

    fun update(id: UUID, request: SessionUpdateRequest): Session = transaction {
        val count = Sessions.update({ Sessions.id eq id }) {
            request.endTime?.let { t -> it[endTime] = Instant.parse(t) }
            it[notes] = request.notes
        }
        if (count == 0) throw NoSuchElementException("Session $id not found")
        findById(id)
    }

    fun delete(id: UUID): Unit = transaction {
        val count = Sessions.deleteWhere { Sessions.id eq id }
        if (count == 0) throw NoSuchElementException("Session $id not found")
    }

    private fun ResultRow.toSession() = Session(
        id = this[Sessions.id].toString(),
        instrumentId = this[Sessions.instrumentId].toString(),
        startTime = this[Sessions.startTime].toString(),
        endTime = this[Sessions.endTime]?.toString(),
        notes = this[Sessions.notes],
        createdAt = this[Sessions.createdAt].toString()
    )
}
