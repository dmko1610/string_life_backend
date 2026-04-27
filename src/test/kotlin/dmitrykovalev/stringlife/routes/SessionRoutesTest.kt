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
import kotlin.test.assertNotNull

class SessionRoutesTest {

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

    private suspend fun ApplicationTestBuilder.createInstrumentId(): String {
        val response = jsonClient().post("/api/instruments") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Guitar","type":"Electric","stringCount":6}""")
        }
        return Json.parseToJsonElement(response.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content
    }

    private suspend fun ApplicationTestBuilder.createSession(instrumentId: String): JsonObject {
        val response = jsonClient().post("/api/sessions") {
            contentType(ContentType.Application.Json)
            setBody("""{"instrumentId":"$instrumentId","startTime":"2026-01-01T10:00:00Z"}""")
        }
        return Json.parseToJsonElement(response.bodyAsText()).jsonObject
    }

    @Test fun `GET api sessions returns empty list initially`() = testApp {
        val response = client.get("/api/sessions")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText().trim())
    }

    @Test fun `POST api sessions creates session and returns 201`() = testApp {
        val instrumentId = createInstrumentId()
        val response = jsonClient().post("/api/sessions") {
            contentType(ContentType.Application.Json)
            setBody("""{"instrumentId":"$instrumentId","startTime":"2026-01-01T10:00:00Z"}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(instrumentId, body["instrumentId"]?.jsonPrimitive?.content)
        assertNotNull(body["id"]?.jsonPrimitive?.content)
    }

    @Test fun `GET api sessions id returns created session`() = testApp {
        val instrumentId = createInstrumentId()
        val created = createSession(instrumentId)
        val id = created["id"]!!.jsonPrimitive.content
        val response = client.get("/api/sessions/$id")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test fun `GET api sessions unknown id returns 404`() = testApp {
        val response = client.get("/api/sessions/00000000-0000-0000-0000-000000000000")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test fun `GET api sessions filtered by instrumentId returns only matching sessions`() = testApp {
        val i1 = createInstrumentId()
        val i2 = createInstrumentId()
        createSession(i1)
        createSession(i2)
        val response = client.get("/api/sessions?instrumentId=$i1")
        assertEquals(HttpStatusCode.OK, response.status)
        val sessions = Json.parseToJsonElement(response.bodyAsText()).jsonArray
        assertEquals(1, sessions.size)
        assertEquals(i1, sessions[0].jsonObject["instrumentId"]?.jsonPrimitive?.content)
    }

    @Test fun `PUT api sessions id updates endTime and notes`() = testApp {
        val instrumentId = createInstrumentId()
        val created = createSession(instrumentId)
        val id = created["id"]!!.jsonPrimitive.content
        val response = jsonClient().put("/api/sessions/$id") {
            contentType(ContentType.Application.Json)
            setBody("""{"endTime":"2026-01-01T11:00:00Z","notes":"Great session"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertNotNull(body["endTime"]?.jsonPrimitive?.content)
        assertEquals("Great session", body["notes"]?.jsonPrimitive?.content)
    }

    @Test fun `DELETE api sessions id returns 204 then GET returns 404`() = testApp {
        val instrumentId = createInstrumentId()
        val created = createSession(instrumentId)
        val id = created["id"]!!.jsonPrimitive.content
        val deleteResponse = client.delete("/api/sessions/$id")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)
        val getResponse = client.get("/api/sessions/$id")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test fun `DELETE instrument cascades to its sessions`() = testApp {
        val instrumentId = createInstrumentId()
        val created = createSession(instrumentId)
        val sessionId = created["id"]!!.jsonPrimitive.content
        client.delete("/api/instruments/$instrumentId")
        val response = client.get("/api/sessions/$sessionId")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
