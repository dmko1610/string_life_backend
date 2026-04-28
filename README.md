# String Life — Backend

REST API for tracking string instruments and practice sessions. Built with Kotlin + Ktor, backed by PostgreSQL.

## Tech Stack

- **Kotlin 2.1** / **Ktor 3.0** (Netty)
- **Exposed ORM** + **HikariCP** connection pool
- **PostgreSQL** (Supabase)
- **Docker** for deployment (Render)

## Prerequisites

- JDK 17+
- PostgreSQL database (or a [Supabase](https://supabase.com) project)

## Local Setup

1. Clone the repo
2. Copy the environment variables and fill in your database credentials:

```
DATABASE_URL=jdbc:postgresql://<host>:<port>/<db>
DATABASE_USER=<user>
DATABASE_PASSWORD=<password>
PORT=8080
```

Create a `.env` file at the project root with the above values.

3. Run the server:

```bash
./gradlew run
```

The server starts on the port defined by `PORT` (defaults to `8080` if not set).

Tables are created automatically on first start via `SchemaUtils.createMissingTablesAndColumns`.

## Running Tests

Tests use an H2 in-memory database — no external database needed.

```bash
./gradlew test
```

## API

Base path: `/api`

### Instruments

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/instruments` | List all instruments |
| `POST` | `/instruments` | Create instrument |
| `GET` | `/instruments/{id}` | Get instrument by ID |
| `PUT` | `/instruments/{id}` | Update instrument |
| `DELETE` | `/instruments/{id}` | Soft-delete instrument |

**Instrument body:**
```json
{
  "name": "Telecaster",
  "type": "Electric",
  "stringCount": 6,
  "lastStringChangeDate": "2026-01-15",
  "notes": "Optional notes"
}
```

### Sessions

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/sessions?instrumentId={id}` | List sessions (optionally filtered by instrument) |
| `POST` | `/sessions` | Create session |
| `GET` | `/sessions/{id}` | Get session by ID |
| `PUT` | `/sessions/{id}` | Update session |
| `DELETE` | `/sessions/{id}` | Soft-delete session |

**Session create body:**
```json
{
  "instrumentId": "uuid",
  "startTime": "2026-01-01T10:00:00Z",
  "notes": "Optional notes"
}
```

**Session update body:**
```json
{
  "endTime": "2026-01-01T11:00:00Z",
  "notes": "Optional notes"
}
```

Deleting an instrument also soft-deletes all of its sessions.

## Deployment

The project includes a `Dockerfile` for deployment on [Render](https://render.com).

Build the fat JAR:

```bash
./gradlew shadowJar
```

The artifact is output to `build/libs/string-life-backend.jar`.
