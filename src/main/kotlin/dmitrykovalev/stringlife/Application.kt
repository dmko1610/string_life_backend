package dmitrykovalev.stringlife

import dmitrykovalev.stringlife.plugins.configureDatabase
import dmitrykovalev.stringlife.plugins.configureRouting
import dmitrykovalev.stringlife.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureDatabase()
    configureSerialization()
    configureRouting()
}
