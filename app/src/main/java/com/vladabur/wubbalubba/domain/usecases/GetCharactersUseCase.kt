package com.vladabur.wubbalubba.domain.usecases

import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.repositories.CharacterRepository
import com.vladabur.wubbalubba.domain.usecases.base.BaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GetCharactersUseCase @Inject constructor(private val characterRepository: CharacterRepository) :
    BaseUseCase<GetCharactersUseCase.Params, List<Character>>() {

    override suspend fun remoteWork(params: Params?): List<Character> {
        return withContext(Dispatchers.IO) {
            characterRepository.getCharactersList(params!!.page)
        }
    }

    class Params(
        val page: Int
    )
}