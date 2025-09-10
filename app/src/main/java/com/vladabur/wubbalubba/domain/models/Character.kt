package com.vladabur.wubbalubba.domain.models

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
    val episode: List<String>?,
    val created: Date?
)
