package com.vladabur.wubbalubba.data.repositories

import com.vladabur.wubbalubba.data.extensions.mapToApiErrors
import com.vladabur.wubbalubba.data.network.models.CharacterResponse
import com.vladabur.wubbalubba.data.network.models.CharactersListResponse
import com.vladabur.wubbalubba.data.network.models.InfoResponse
import com.vladabur.wubbalubba.data.network.services.CharacterService
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.domain.models.CharactersList
import com.vladabur.wubbalubba.domain.repositories.CharacterRepository
import javax.inject.Inject


class CharacterRepositoryImpl @Inject constructor(private val characterService: CharacterService) :
    CharacterRepository {

    override suspend fun getCharactersList(
        page: Int,
        name: String?,
        statuses: List<CharacterStatus>?,
        species: List<CharacterSpecies>?,
        genders: List<CharacterGender>?,
    ): CharactersList {
        return try {
            val response =
                if (statuses.isNullOrEmpty() && genders.isNullOrEmpty() && species.isNullOrEmpty()) {
                    characterService.getCharacters(page, name)
                } else {
                    val result = mutableListOf<CharacterResponse>()
                    var total = 0
                    val statusesToUse = if (statuses.isNullOrEmpty()) listOf(null) else statuses
                    val speciesToUse = if (species.isNullOrEmpty()) listOf(null) else species
                    val gendersToUse = if (genders.isNullOrEmpty()) listOf(null) else genders

                    for (status in statusesToUse) {
                        for (type in speciesToUse) {
                            for (gender in gendersToUse) {
                                val statusString = status?.let { CharacterStatus.toValue(it) }
                                val speciesString = type?.let { CharacterSpecies.toValue(it) }
                                val genderString = gender?.let { CharacterGender.toValue(it) }
                                val response = characterService.getCharacters(
                                    page = page,
                                    name = name,
                                    status = statusString,
                                    species = speciesString,
                                    gender = genderString
                                )
                                val charactersList =
                                    response.results?.filter {
                                        (if (statusString != null) it.status == statusString else true)
                                                && (if (speciesString != null) it.species == speciesString else true)
                                                && (if (genderString != null) it.gender == genderString else true)
                                    }
                                result.addAll(charactersList ?: emptyList())
                                total += response.info?.count ?: 0
                            }
                        }
                    }
                    result.sortBy { it.name }
                    CharactersListResponse(info = InfoResponse(count = total), results = result)
                }
            CharactersListResponse.map(response)
        } catch (e: Exception) {
            throw e.mapToApiErrors()
        }
    }

    override suspend fun getCharacter(id: Int): Character {
        return try {
            val response = characterService.getCharacter(id)
            CharacterResponse.map(response)
        } catch (e: Exception) {
            throw e.mapToApiErrors()
        }
    }
}