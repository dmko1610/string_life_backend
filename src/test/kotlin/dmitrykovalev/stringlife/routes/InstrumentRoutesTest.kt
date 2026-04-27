package dmitrykovalev.stringlife.routes

import dmitrykovalev.stringlife.TestDatabase
import dmitrykovalev.stringlife.plugins.configureRouting
import dmitrykovalev.stringlife.plugins.configureSerialization
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InstrumentRoutesTest {

    companion object {
        @BeforeAll @JvmStatic fun initDb() = TestDatabase.init()
    }

    @BeforeEach fun resetDb() = TestDatabase.reset()

    private fun testApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }
        block()
    }

    private fun ApplicationTestBuilder.jsonClient() = createClient {
        install(ContentNegotiation) { json() }
    }

    private suspend fun ApplicationTestBuilder.createInstrument(
        name: String = "Stratocaster",
        type: String = "Electric",
        stringCount: Int = 6
    ): JsonObject {
        val response = jsonClient().post("/api/instruments") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"$name","type":"$type","stringCount":$stringCount}""")
        }
        return Json.parseToJsonElement(response.bodyAsText()).jsonObject
    }

    @Test fun `GET api instruments returns empty list initially`() = testApp {
        val response = client.get("/api/instruments")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText().trim())
    }

    @Test fun `POST api instruments creates instrument and returns 201`() = testApp {
        val response = jsonClient().post("/api/instruments") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Stratocaster","type":"Electric","stringCount":6}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("Stratocaster", body["name"]?.jsonPrimitive?.content)
        assertTrue(body["id"]?.jsonPrimitive?.content?.isNotEmpty() == true)
    }

    @Test fun `GET api instruments id returns created instrument`() = testApp {
        val created = createInstrument()
        val id = created["id"]!!.jsonPrimitive.content
        val response = client.get("/api/instruments/$id")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(id, body["id"]?.jsonPrimitive?.content)
    }

    @Test fun `GET api instruments unknown id returns 404`() = testApp {
        val response = client.get("/api/instruments/00000000-0000-0000-0000-000000000000")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test fun `PUT api instruments id updates instrument`() = testApp {
        val created = createInstrument()
        val id = created["id"]!!.jsonPrimitive.content
        val response = jsonClient().put("/api/instruments/$id") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Les Paul","type":"Electric","stringCount":6,"notes":"Vintage"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("Les Paul", body["name"]?.jsonPrimitive?.content)
        assertEquals("Vintage", body["notes"]?.jsonPrimitive?.content)
    }

    @Test fun `PUT api instruments unknown id returns 404`() = testApp {
        val response = jsonClient().put("/api/instruments/00000000-0000-0000-0000-000000000000") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Les Paul","type":"Electric","stringCount":6}""")
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test fun `DELETE api instruments id returns 204 then GET returns 404`() = testApp {
        val created = createInstrument()
        val id = created["id"]!!.jsonPrimitive.content
        val deleteResponse = client.delete("/api/instruments/$id")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)
        val getResponse = client.get("/api/instruments/$id")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test fun `DELETE api instruments id removes it from findAll list`() = testApp {
        val created = createInstrument()
        val id = created["id"]!!.jsonPrimitive.content
        client.delete("/api/instruments/$id")
        val response = client.get("/api/instruments")
        val list = Json.parseToJsonElement(response.bodyAsText()).jsonArray
        assertEquals(0, list.size)
    }
}
