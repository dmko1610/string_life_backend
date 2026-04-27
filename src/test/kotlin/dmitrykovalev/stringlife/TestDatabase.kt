package dmitrykovalev.stringlife

import dmitrykovalev.stringlife.db.tables.Instruments
import dmitrykovalev.stringlife.db.tables.Sessions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object TestDatabase {
    fun init() {
        Database.connect(
            "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Instruments, Sessions)
        }
    }

    fun reset() = transaction {
        SchemaUtils.drop(Sessions, Instruments)
        SchemaUtils.create(Instruments, Sessions)
    }
}
