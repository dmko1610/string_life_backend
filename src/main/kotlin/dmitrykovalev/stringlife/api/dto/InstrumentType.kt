package dmitrykovalev.stringlife.api.dto

enum class InstrumentType {
    ELECTRIC,
    ACOUSTIC,
    BASS,
    UKULELE;

    companion object {
        fun from(value: String): InstrumentType =
            when (value.trim().uppercase()) {
                "ELECTRIC", "ELECTRO" -> ELECTRIC
                "ACOUSTIC" -> ACOUSTIC
                "BASS" -> BASS
                "UKULELE" -> UKULELE
                else -> throw IllegalArgumentException(
                    "Unsupported instrument type '$value'. Supported values: ${entries.joinToString()}"
                )
            }
    }
}
