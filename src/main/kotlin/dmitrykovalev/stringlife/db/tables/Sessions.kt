package dmitrykovalev.stringlife.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Sessions : Table("sessions") {
    val id = uuid("id").autoGenerate()
    val instrumentId = uuid("instrument_id").references(Instruments.id)
    val startTime = timestamp("start_time")
    val endTime = timestamp("end_time").nullable()
    val notes = text("notes").nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
