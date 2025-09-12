package com.vladabur.wubbalubba.domain.models

enum class CharacterStatus(val value: String?) {
    ALIVE("Alive"),
    DEAD("Dead"),
    UNKNOWN("Unknown");

    companion object {
        fun fromValue(value: String?): CharacterStatus? {
            return CharacterStatus.entries.firstOrNull { it.value == value }
        }

        fun toValue(value: CharacterStatus?): String? {
            return CharacterStatus.entries.firstOrNull { it == value }?.value
        }
    }
}
