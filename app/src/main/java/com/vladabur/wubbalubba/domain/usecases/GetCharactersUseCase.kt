package com.vladabur.wubbalubba.domain.usecases

import com.vladabur.wubbalubba.domain.models.Character
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
            delay(1000)
            characterRepository.getCharactersList(params!!.page)
        }
    }

    class Params(
        val page: Int
    )
}