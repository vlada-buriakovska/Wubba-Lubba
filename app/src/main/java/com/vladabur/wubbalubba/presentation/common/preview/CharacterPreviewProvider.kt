package com.vladabur.wubbalubba.presentation.common.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.domain.models.Location
import java.util.Date

class CharacterPreviewProvider : PreviewParameterProvider<Character> {
    override val values: Sequence<Character>
        get() = sequenceOf(
            Character(
                id = 1,
                name = "Rick Sanchez",
                created = Date(),
                image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                status = CharacterStatus.ALIVE,
                species = CharacterSpecies.HUMAN,
                gender = CharacterGender.MALE,
                origin = Location("Earth"),
                location = Location("Citadel of Ricks"),
                type = null,
                episode = listOf(
                    "https://rickandmortyapi.com/api/episode/1",
                    "https://rickandmortyapi.com/api/episode/2",
                    "https://rickandmortyapi.com/api/episode/3",
                    "https://rickandmortyapi.com/api/episode/4",
                    "https://rickandmortyapi.com/api/episode/5",
                    "https://rickandmortyapi.com/api/episode/6",
                )
            )
        )
}