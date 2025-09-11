package com.vladabur.wubbalubba.domain.usecases

import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharactersList
import com.vladabur.wubbalubba.domain.repositories.CharacterRepository
import com.vladabur.wubbalubba.domain.usecases.base.BaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GetCharacterUseCase @Inject constructor(private val characterRepository: CharacterRepository) :
    BaseUseCase<GetCharacterUseCase.Params, Character>() {

    override suspend fun remoteWork(params: Params?): Character {
        return withContext(Dispatchers.IO) {
            //FIXME just to sow longer loading
            delay(2000)
            characterRepository.getCharacter(params!!.id)
        }
    }

    class Params(
        val id: Int,
    )
}