package com.vladabur.wubbalubba.domain.repositories

import com.vladabur.wubbalubba.domain.models.Character


interface CharacterRepository {

    suspend fun getCharactersList(page: Int): List<Character>
}