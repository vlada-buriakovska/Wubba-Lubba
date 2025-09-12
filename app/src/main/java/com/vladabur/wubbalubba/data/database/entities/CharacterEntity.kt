package com.vladabur.wubbalubba.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vladabur.wubbalubba.data.database.entities.CharacterEntity.Companion.ENTITY_NAME
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.domain.models.Location
import com.vladabur.wubbalubba.domain.models.ModelMapper
import java.util.Date

@Entity(tableName = ENTITY_NAME)
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String?,
    val status: String?,
    val species: String?,
    val type: String?,
    val gender: String?,
    val image: String?,
    val origin: String?,
    val location: String?,
    val episode: List<String>?,
    val created: Date?
) {
    companion object : ModelMapper<CharacterEntity, Character> {

        const val ENTITY_NAME = "characters"

        override fun map(model: CharacterEntity): Character = Character(
            id = model.id,
            name = model.name,
            status = model.status?.let { CharacterStatus.fromValue(it) },
            species = model.species?.let { CharacterSpecies.fromValue(it) },
            type = model.type,
            gender = model.gender?.let { CharacterGender.fromValue(it) },
            origin = model.origin?.let { Location(it) },
            location = model.location?.let { Location(it) },
            image = model.image,
            episode = model.episode,
            created = model.created,
        )
    }
}