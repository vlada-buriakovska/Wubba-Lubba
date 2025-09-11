package com.vladabur.wubbalubba.domain.repositories

import com.vladabur.wubbalubba.domain.models.CharactersList


interface CharacterRepository {

    suspend fun getCharactersList(page: Int, name: String?): CharactersList
}