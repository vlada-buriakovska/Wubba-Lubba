package com.vladabur.wubbalubba.domain.repositories

import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharactersList


interface CharacterRepository {

    suspend fun getCharactersList(page: Int): CharactersList
}