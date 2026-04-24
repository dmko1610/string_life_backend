package dmitrykovalev.stringlife.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Instruments : Table("instruments") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    val type = varchar("type", 100)
    val stringCount = integer("string_count")
    val lastStringChangeDate = date("last_string_change_date").nullable()
    val notes = text("notes").nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
