package com.vladabur.wubbalubba.domain.models


enum class CharacterSpecies(val value: String?) {
    ALIEN("Alien"),
    ANIMAL("Animal"),
    CRONENBERG("Cronenberg"),
    DISEASE("Disease"),
    HUMAN("Human"),
    HUMANOID("Humanoid"),
    POOPYBUTTHOLE("Poopybutthole"),
    MYTHOLOGICAL_CREATURE("Mythological Creature"),
    ROBOT("Robot"),
    UNKNOWN("unknown");

    companion object {
        fun fromValue(value: String?): CharacterSpecies? {
            return CharacterSpecies.entries.firstOrNull { it.value == value }
        }

        fun toValue(value: CharacterSpecies?): String? {
            return CharacterSpecies.entries.firstOrNull { it == value }?.value?.replaceFirstChar { it.uppercase() }
        }
    }
}