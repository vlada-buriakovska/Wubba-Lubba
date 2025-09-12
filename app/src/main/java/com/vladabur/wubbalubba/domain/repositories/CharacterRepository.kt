package com.vladabur.wubbalubba.domain.repositories

import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.domain.models.CharactersList


interface CharacterRepository {

    suspend fun getCharactersList(
        page: Int,
        name: String?,
        statuses: List<CharacterStatus>?,
        species: List<CharacterSpecies>?,
        genders: List<CharacterGender>?,
    ): CharactersList

    suspend fun getCharacter(id: Int): Character
}