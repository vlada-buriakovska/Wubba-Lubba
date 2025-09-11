package com.vladabur.wubbalubba.presentation.feature.character

import androidx.lifecycle.viewModelScope
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.usecases.GetCharacterUseCase
import com.vladabur.wubbalubba.domain.usecases.GetCharacterUseCase.Params
import com.vladabur.wubbalubba.domain.usecases.base.ResultCallbacks
import com.vladabur.wubbalubba.presentation.common.base.BaseViewModel
import com.vladabur.wubbalubba.presentation.feature.character.CharacterDetailsUiEvent.Consume
import com.vladabur.wubbalubba.presentation.feature.character.CharacterDetailsUiEvent.Retry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.properties.Delegates


@HiltViewModel
class CharacterDetailsVM @Inject constructor(
    private val getCharacterUseCase: GetCharacterUseCase
) :
    BaseViewModel() {
    private val managerUiState = MutableStateFlow(CharacterDetailsUiState())
    val uiState: StateFlow<CharacterDetailsUiState> = managerUiState.asStateFlow()

    private var characterId: Int? = null

    fun setData(characterId: Int) {
        if (this.characterId == characterId) return
        this.characterId = characterId
        getCharacter(characterId)
    }

    fun onEvent(event: CharacterDetailsUiEvent) {
        when (event) {
            Consume -> consumeError()
            Retry -> retry()
        }
    }

    private fun getCharacter(characterId: Int) {
        getCharacterUseCase(
            coroutineScope = viewModelScope,
            params = Params(
                id = characterId
            ),
            result = ResultCallbacks(
                onSuccess = { result ->
                    managerUiState.update { 
                        it.copy(character = result)
                    }
                },
                onLoading = ::handleOnLoading,
                onError = ::handleOnError,
                onUnexpectedError = ::handleOnUnexpectedError,
                onConnectionError = {
                    handleOnConnectionError { getCharacter(characterId) }
                }
            )
        )
    }
}

sealed class CharacterDetailsUiEvent {
    data object Retry : CharacterDetailsUiEvent()
    data object Consume : CharacterDetailsUiEvent()
}

data class CharacterDetailsUiState(
    val character: Character? = null
)