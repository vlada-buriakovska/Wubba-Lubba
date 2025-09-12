package com.vladabur.wubbalubba.domain.repositories

import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.domain.models.CharactersList


interface CharacterRepository {

    suspend fun getCharactersList(
        isFromLocal: Boolean,
        isForceReload: Boolean,
        page: Int,
        name: String?,
        status: CharacterStatus?,
        species: CharacterSpecies?,
        gender: CharacterGender?,
    ): CharactersList

    suspend fun getCharacter(
        isFromLocal: Boolean,
        isForceReload: Boolean,
        id: Int
    ): Character?
}