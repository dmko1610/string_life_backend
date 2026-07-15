package dmitrykovalev.stringlife

import dmitrykovalev.stringlife.plugins.configureCors
import dmitrykovalev.stringlife.plugins.configureDatabase
import dmitrykovalev.stringlife.plugins.configureOpenApi
import dmitrykovalev.stringlife.plugins.configureRouting
import dmitrykovalev.stringlife.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureDatabase()
    configureSerialization()
    configureCors()
    configureRouting()
    configureOpenApi()
}
