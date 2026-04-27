package dmitrykovalev.stringlife.repositories

import dmitrykovalev.stringlife.TestDatabase
import dmitrykovalev.stringlife.models.InstrumentRequest
import dmitrykovalev.stringlife.models.SessionRequest
import dmitrykovalev.stringlife.models.SessionUpdateRequest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SessionRepositoryTest {

    private val instrumentRepo = InstrumentRepository()
    private val repo = SessionRepository()

    companion object {
        @BeforeAll @JvmStatic fun initDb() = TestDatabase.init()
    }

    @BeforeEach fun resetDb() = TestDatabase.reset()

    private fun createInstrument() =
        instrumentRepo.create(InstrumentRequest("Telecaster", "Electric", 6))

    private fun aSessionRequest(instrumentId: String) =
        SessionRequest(instrumentId = instrumentId, startTime = "2026-01-01T10:00:00Z")

    @Test fun `create and findById return the same session`() {
        val instrument = createInstrument()
        val created = repo.create(aSessionRequest(instrument.id))
        val found = repo.findById(UUID.fromString(created.id))
        assertEquals(created.id, found.id)
        assertEquals(instrument.id, found.instrumentId)
        assertNull(found.endTime)
    }

    @Test fun `findAll returns all active sessions`() {
        val instrument = createInstrument()
        repo.create(aSessionRequest(instrument.id))
        repo.create(aSessionRequest(instrument.id))
        assertEquals(2, repo.findAll().size)
    }

    @Test fun `findAll filtered by instrumentId returns only matching sessions`() {
        val i1 = createInstrument()
        val i2 = createInstrument()
        repo.create(aSessionRequest(i1.id))
        repo.create(aSessionRequest(i2.id))
        val filtered = repo.findAll(UUID.fromString(i1.id))
        assertEquals(1, filtered.size)
        assertEquals(i1.id, filtered[0].instrumentId)
    }

    @Test fun `findAll excludes soft-deleted sessions`() {
        val instrument = createInstrument()
        val created = repo.create(aSessionRequest(instrument.id))
        repo.delete(UUID.fromString(created.id))
        assertEquals(0, repo.findAll().size)
    }

    @Test fun `findAll with filter excludes deleted sessions for that instrument`() {
        val instrument = createInstrument()
        val created = repo.create(aSessionRequest(instrument.id))
        repo.delete(UUID.fromString(created.id))
        assertEquals(0, repo.findAll(UUID.fromString(instrument.id)).size)
    }

    @Test fun `update sets endTime and notes`() {
        val instrument = createInstrument()
        val created = repo.create(aSessionRequest(instrument.id))
        val updated = repo.update(
            UUID.fromString(created.id),
            SessionUpdateRequest(endTime = "2026-01-01T11:00:00Z", notes = "Great session")
        )
        assertNotNull(updated.endTime)
        assertEquals("Great session", updated.notes)
    }

    @Test fun `update throws for unknown id`() {
        assertThrows<NoSuchElementException> {
            repo.update(UUID.randomUUID(), SessionUpdateRequest())
        }
    }

    @Test fun `delete soft-deletes so findById throws`() {
        val instrument = createInstrument()
        val created = repo.create(aSessionRequest(instrument.id))
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
}
