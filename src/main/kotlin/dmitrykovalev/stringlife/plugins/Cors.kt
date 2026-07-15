package dmitrykovalev.stringlife.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCors() {
    val productionOrigins =
        this@configureCors.environment.config.propertyOrNull("ktor.cors.allowedOrigins")
            ?.getString()?.split(',')?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()

    install(CORS) {
        allowHost("localhost:3000")
        allowHost("127.0.0.1:3000")

        productionOrigins.forEach { host ->
            allowHost(host, schemes = listOf("https"))
        }

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }
}