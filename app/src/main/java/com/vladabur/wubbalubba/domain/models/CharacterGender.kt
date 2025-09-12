package com.vladabur.wubbalubba.domain.models


enum class CharacterGender(val value: String?) {
    FEMALE("Female"),
    MALE("Male"),
    GENDERLESS("Genderless"),
    UNKNOWN("unknown");

    companion object {
        fun fromValue(value: String?): CharacterGender? {
            return CharacterGender.entries.firstOrNull { it.value == value }
        }

        fun toValue(value: CharacterGender?): String? {
            return CharacterGender.entries.firstOrNull { it == value }?.value?.replaceFirstChar { it.uppercase() }
        }
    }
}