package com.vladabur.wubbalubba.data.network.models

import com.google.gson.annotations.SerializedName
import com.vladabur.wubbalubba.data.database.entities.CharacterEntity
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.domain.models.ModelMapper
import java.util.Date

data class CharacterResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("species")
    val species: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("origin")
    val origin: LocationResponse?,
    @SerializedName("location")
    val location: LocationResponse?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("episode")
    val episode: List<String>?,
    @SerializedName("created")
    val created: Date?
) {
     object CharacterMapper: ModelMapper<CharacterResponse, Character> {
        override fun map(model: CharacterResponse): Character = Character(
            id = model.id,
            name = model.name,
            status = model.status?.let { CharacterStatus.fromValue(it) },
            species = model.species?.let { CharacterSpecies.fromValue(it) },
            type = model.type,
            gender = model.gender?.let { CharacterGender.fromValue(it) },
            origin = model.origin?.let { LocationResponse.map(it) },
            location = model.location?.let { LocationResponse.map(it) },
            image = model.image,
            episode = model.episode,
            created = model.created,
        )
    }
    
     object CharacterEntityMapper: ModelMapper<CharacterResponse, CharacterEntity> {
        override fun map(model: CharacterResponse): CharacterEntity = CharacterEntity(
            id = model.id,
            name = model.name,
            status = model.status,
            species = model.species,
            type = model.type,
            gender = model.gender,
            origin = model.origin?.name,
            location = model.location?.name,
            image = model.image,
            episode = model.episode,
            created = model.created,
        )
    }
}
