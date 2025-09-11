package com.vladabur.wubbalubba.domain.models

import com.vladabur.wubbalubba.BuildConfig
import java.util.Date

data class Character(
    val id: Int,
    val name: String?,
    val status: String?,
    val species: String?,
    val type: String?,
    val gender: String?,
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
