package com.vladabur.wubbalubba.data.repositories

import com.vladabur.wubbalubba.data.network.models.CharacterResponse
import com.vladabur.wubbalubba.data.network.models.CharactersListResponse
import com.vladabur.wubbalubba.data.network.services.CharacterService
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharactersList
import com.vladabur.wubbalubba.domain.repositories.CharacterRepository
import javax.inject.Inject


class CharacterRepositoryImpl @Inject constructor(private val characterService: CharacterService) :
    CharacterRepository {

    override suspend fun getCharactersList(page: Int): CharactersList {
        val response = characterService.getCharacters(page)
        return CharactersListResponse.map(response)
    }
}