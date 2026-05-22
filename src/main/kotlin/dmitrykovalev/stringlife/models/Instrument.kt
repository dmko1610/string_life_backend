package dmitrykovalev.stringlife.models

import kotlinx.serialization.Serializable

@Serializable
data class Instrument(
    val id: String,
    val name: String,
    val type: String,
    val stringCount: Int,
    val lastStringChangeDate: String? = null,
    val notes: String? = null,
    val createdAt: String,
    val updatedAt: Long
)

@Serializable
data class InstrumentRequest(
    val name: String,
    val type: String,
    val stringCount: Int,
    val lastStringChangeDate: String? = null,
    val notes: String? = null
)
