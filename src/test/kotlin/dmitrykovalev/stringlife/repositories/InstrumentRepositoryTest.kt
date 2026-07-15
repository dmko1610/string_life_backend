package dmitrykovalev.stringlife.repositories

import dmitrykovalev.stringlife.TestDatabase
import dmitrykovalev.stringlife.api.dto.InstrumentRequest
import dmitrykovalev.stringlife.api.dto.SessionRequest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class InstrumentRepositoryTest {

    private val repo = InstrumentRepository()
    private val sessionRepo = SessionRepository()

    companion object {
        @BeforeAll @JvmStatic fun initDb() = TestDatabase.init()
    }

    @BeforeEach fun resetDb() = TestDatabase.reset()

    private fun aRequest(name: String = "Telecaster") =
        InstrumentRequest(name = name, type = "ELECTRIC", stringCount = 6)

    @Test fun `create and findById return the same instrument`() {
        val created = repo.create(aRequest())
        val found = repo.findById(UUID.fromString(created.id))
        assertEquals(created.id, found.id)
        assertEquals("Telecaster", found.name)
        assertEquals("ELECTRIC", found.type)
        assertEquals(6, found.stringCount)
    }

    @Test fun `create normalizes legacy type aliases`() {
        val created = repo.create(InstrumentRequest("Telecaster", "electro", 6))
        assertEquals("ELECTRIC", created.type)
    }

    @Test fun `create throws for unsupported type`() {
        assertThrows<IllegalArgumentException> {
            repo.create(InstrumentRequest("Telecaster", "MANDOLIN", 6))
        }
    }

    @Test fun `findAll returns all active instruments`() {
        repo.create(aRequest("Guitar 1"))
        repo.create(aRequest("Guitar 2"))
        assertEquals(2, repo.findAll().size)
    }

    @Test fun `findAll excludes soft-deleted instruments`() {
        val created = repo.create(aRequest())
        repo.delete(UUID.fromString(created.id))
        assertEquals(0, repo.findAll().size)
    }

    @Test fun `update modifies instrument fields`() {
        val created = repo.create(aRequest())
        val updated = repo.update(
            UUID.fromString(created.id),
            InstrumentRequest("Les Paul", "BASS", 4, notes = "Vintage")
        )
        assertEquals("Les Paul", updated.name)
        assertEquals("BASS", updated.type)
        assertEquals(4, updated.stringCount)
        assertEquals("Vintage", updated.notes)
    }

    @Test fun `update throws for unknown id`() {
        assertThrows<NoSuchElementException> {
            repo.update(UUID.randomUUID(), aRequest())
        }
    }

    @Test fun `delete soft-deletes so findById throws`() {
        val created = repo.create(aRequest())
        repo.delete(UUID.fromString(created.id))
        assertThrows<NoSuchElementException> {
            repo.findById(UUID.fromString(created.id))
        }
    }

    @Test fun `delete throws for unknown id`() {
        assertThrows<NoSuchElementException> {
            repo.delete(UUID.randomUUID())
        }
    }

    @Test fun `delete cascades soft-delete to instrument sessions`() {
        val instrument = repo.create(aRequest())
        val session = sessionRepo.create(
            SessionRequest(instrumentId = instrument.id, startTime = "2026-01-01T10:00:00Z")
        )
        repo.delete(UUID.fromString(instrument.id))
        assertThrows<NoSuchElementException> {
            sessionRepo.findById(UUID.fromString(session.id))
        }
    }

    @Test fun `create stores lastStringChangeDate when provided`() {
        val created = repo.create(aRequest().copy(lastStringChangeDate = "2026-01-15"))
        assertNotNull(created.lastStringChangeDate)
        assertEquals("2026-01-15", created.lastStringChangeDate)
    }
}
