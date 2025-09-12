package com.vladabur.wubbalubba.domain.usecases

import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.domain.models.CharactersList
import com.vladabur.wubbalubba.domain.repositories.CharacterRepository
import com.vladabur.wubbalubba.domain.usecases.base.BaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GetCharactersUseCase @Inject constructor(private val characterRepository: CharacterRepository) :
    BaseUseCase<GetCharactersUseCase.Params, CharactersList>() {

    override suspend fun remoteWork(params: Params?): CharactersList {
        return withContext(Dispatchers.IO) {
            //FIXME just to sow longer loading
            delay(1000)
            characterRepository.getCharactersList(
                params!!.page,
                params.name,
                params.statuses,
                params.species,
                params.genders
            )
        }
    }

    class Params(
        val page: Int,
        val name: String? = null,
        val statuses: List<CharacterStatus>? = null,
        val species: List<CharacterSpecies>? = null,
        val genders: List<CharacterGender>? = null,
    )
}