package dmitrykovalev.stringlife.routes

import dmitrykovalev.stringlife.TestDatabase
import dmitrykovalev.stringlife.plugins.configureOpenApi
import dmitrykovalev.stringlife.plugins.configureRouting
import dmitrykovalev.stringlife.plugins.configureSerialization
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OpenApiRoutesTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initDb() = TestDatabase.init()
    }

    private fun testApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            configureSerialization()
            configureRouting()
            configureOpenApi()
        }
        block()
    }

    @Test
    fun `GET openapi returns API schema with main route groups`() = testApp {
        val response = client.get("/openapi")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("/api/instruments"))
        assertTrue(body.contains("/api/sessions"))
    }

    @Test
    fun `GET swagger returns Swagger UI`() = testApp {
        val response = client.get("/swagger")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
