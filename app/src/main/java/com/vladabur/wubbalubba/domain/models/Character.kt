package com.vladabur.wubbalubba.domain.models

import com.vladabur.wubbalubba.BuildConfig
import java.util.Date

data class Character(
    val id: Int,
    val name: String?,
    val status: CharacterStatus?,
    val species: CharacterSpecies?,
    val type: String?,
    val gender: CharacterGender?,
    val origin: Location?,
    val location: Location?,
    val image: String?,
    val created: Date?,
    private val episode: List<String>?,
) {
    companion object {
        private const val EPISODES_ENDPOINT = "episode/"
    }

    val episodes: List<String>? =
        episode?.map { it.removePrefix(BuildConfig.BASE_URL + EPISODES_ENDPOINT) }
}
