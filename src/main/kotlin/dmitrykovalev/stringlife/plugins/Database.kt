package dmitrykovalev.stringlife.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dmitrykovalev.stringlife.db.tables.Instruments
import dmitrykovalev.stringlife.db.tables.Sessions
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val config = HikariConfig().apply {
        jdbcUrl = environment.config.property("database.url").getString()
        username = environment.config.property("database.user").getString()
        password = environment.config.property("database.password").getString()
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = 5
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    Database.connect(HikariDataSource(config))

    transaction {
        SchemaUtils.createMissingTablesAndColumns(Instruments, Sessions)
    }
}
