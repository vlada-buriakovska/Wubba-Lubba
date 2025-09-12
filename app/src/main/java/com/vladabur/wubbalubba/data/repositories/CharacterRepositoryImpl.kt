package com.vladabur.wubbalubba.data.repositories

import androidx.room.withTransaction
import com.vladabur.wubbalubba.data.database.AppDatabase
import com.vladabur.wubbalubba.data.database.dao.CharacterDao
import com.vladabur.wubbalubba.data.database.entities.CharacterEntity
import com.vladabur.wubbalubba.data.extensions.mapToApiErrors
import com.vladabur.wubbalubba.data.network.models.CharacterResponse.CharacterEntityMapper
import com.vladabur.wubbalubba.data.network.models.CharacterResponse.CharacterMapper
import com.vladabur.wubbalubba.data.network.services.CharacterService
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.domain.models.CharactersList
import com.vladabur.wubbalubba.domain.repositories.CharacterRepository
import javax.inject.Inject


class CharacterRepositoryImpl @Inject constructor(
    private val characterService: CharacterService,
    private val database: AppDatabase,
    private val characterDao: CharacterDao,
) :
    CharacterRepository {

    override suspend fun getCharactersList(
        isFromLocal: Boolean,
        isForceReload: Boolean,
        page: Int,
        name: String?,
        status: CharacterStatus?,
        species: CharacterSpecies?,
        gender: CharacterGender?
    ): CharactersList {
        return try {
            val statusString = status?.let { CharacterStatus.toValue(it) }
            val speciesString = species?.let { CharacterSpecies.toValue(it) }
            val genderString = gender?.let { CharacterGender.toValue(it) }
            val (entities, total) = if (!isFromLocal || isForceReload) {
                val response = characterService.getCharacters(
                    page,
                    name,
                    statusString,
                    speciesString,
                    genderString
                )
                val result = response?.results?.map { CharacterEntityMapper.map(it) } ?: emptyList()

                database.withTransaction {
                    if (isForceReload) {
                        characterDao.clearAll()
                    }
                    characterDao.insertAll(result)
                }
                Pair(result, response?.info?.count ?: 0)
            } else {
                val result = characterDao.getCharacters(
                    page,
                    name,
                    statusString,
                    speciesString,
                    genderString
                )
                val total = characterDao.countCharacters(
                    name,
                    statusString,
                    speciesString,
                    genderString
                )
                Pair(result, total)
            }

            CharactersList(
                total = total,
                characters = entities.map { CharacterEntity.map(it) }
            )
        } catch (e: Exception) {
            throw e.mapToApiErrors()
        }
    }

    override suspend fun getCharacter(
        isFromLocal: Boolean,
        isForceReload: Boolean,
        id: Int
    ): Character? {
        return try {
            if (!isFromLocal || isForceReload) {
                val response = characterService.getCharacter(id)
                response?.let { CharacterMapper.map(it) }
            } else {
                val entity = characterDao.getCharacterById(id)
                entity?.let { CharacterEntity.map(entity) }
            }
        } catch (e: Exception) {
            throw e.mapToApiErrors()
        }
    }
}