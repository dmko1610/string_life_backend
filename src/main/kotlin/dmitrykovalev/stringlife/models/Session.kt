package dmitrykovalev.stringlife.models

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: String,
    val instrumentId: String,
    val startTime: String,
    val endTime: String? = null,
    val notes: String? = null,
    val createdAt: String
)

@Serializable
data class SessionRequest(
    val instrumentId: String,
    val startTime: String,
    val notes: String? = null
)

@Serializable
data class SessionUpdateRequest(
    val endTime: String? = null,
    val notes: String? = null
)
