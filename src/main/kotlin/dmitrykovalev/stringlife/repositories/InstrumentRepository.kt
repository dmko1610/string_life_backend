package dmitrykovalev.stringlife.repositories

import dmitrykovalev.stringlife.db.tables.Instruments
import dmitrykovalev.stringlife.models.Instrument
import dmitrykovalev.stringlife.models.InstrumentRequest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class InstrumentRepository {

    fun findAll(): List<Instrument> = transaction {
        Instruments.selectAll().map { it.toInstrument() }
    }

    fun findById(id: UUID): Instrument = transaction {
        Instruments.selectAll()
            .where { Instruments.id eq id }
            .firstOrNull()
            ?.toInstrument()
            ?: throw NoSuchElementException("Instrument $id not found")
    }

    fun create(request: InstrumentRequest): Instrument = transaction {
        val insertedId = Instruments.insert {
            it[name] = request.name
            it[type] = request.type
            it[stringCount] = request.stringCount
            it[lastStringChangeDate] = request.lastStringChangeDate?.let { d -> LocalDate.parse(d) }
            it[notes] = request.notes
            it[createdAt] = Clock.System.now()
        }[Instruments.id]

        findById(insertedId)
    }

    fun update(id: UUID, request: InstrumentRequest): Instrument = transaction {
        val count = Instruments.update({ Instruments.id eq id }) {
            it[name] = request.name
            it[type] = request.type
            it[stringCount] = request.stringCount
            it[lastStringChangeDate] = request.lastStringChangeDate?.let { d -> LocalDate.parse(d) }
            it[notes] = request.notes
        }
        if (count == 0) throw NoSuchElementException("Instrument $id not found")
        findById(id)
    }

    fun delete(id: UUID): Unit = transaction {
        val count = Instruments.deleteWhere { Instruments.id eq id }
        if (count == 0) throw NoSuchElementException("Instrument $id not found")
    }

    private fun ResultRow.toInstrument() = Instrument(
        id = this[Instruments.id].toString(),
        name = this[Instruments.name],
        type = this[Instruments.type],
        stringCount = this[Instruments.stringCount],
        lastStringChangeDate = this[Instruments.lastStringChangeDate]?.toString(),
        notes = this[Instruments.notes],
        createdAt = this[Instruments.createdAt].toString()
    )
}
