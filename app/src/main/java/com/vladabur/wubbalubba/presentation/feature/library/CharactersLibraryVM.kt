package com.vladabur.wubbalubba.presentation.feature.library

import androidx.lifecycle.viewModelScope
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.usecases.GetCharactersUseCase
import com.vladabur.wubbalubba.domain.usecases.GetCharactersUseCase.Params
import com.vladabur.wubbalubba.domain.usecases.base.ResultCallbacks
import com.vladabur.wubbalubba.presentation.common.BaseViewModel
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Consume
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.LoadMore
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Refresh
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Retry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CharactersLibraryVM @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) :
    BaseViewModel() {

    private val managerUiState = MutableStateFlow(CharactersLibraryUiState())
    val uiState: StateFlow<CharactersLibraryUiState> = managerUiState.asStateFlow()

    init {
        getCharacters()
    }

    fun onEvent(charactersLibraryUiEvent: CharactersLibraryUiEvent) {
        when (charactersLibraryUiEvent) {
            Consume -> consumeError()
            Retry -> retry()
            is LoadMore -> {
                getCharacters(page = charactersLibraryUiEvent.page)
            }

            Refresh -> {
                getCharacters()
            }
        }
    }

    fun getCharacters(page: Int = 0) {
        getCharactersUseCase(
            coroutineScope = viewModelScope,
            params = Params(page = page),
            result = ResultCallbacks(
                onSuccess = { result ->
                    managerUiState.update {
                        it.copy(charactersList = result)
                    }
                },
                onLoading = ::handleOnLoading,
                onError = ::handleOnError,
                onConnectionError = {
                    handleOnConnectionError { getCharacters(page) }
                }
            )
        )
    }
}

sealed class CharactersLibraryUiEvent {
    data object Retry : CharactersLibraryUiEvent()
    data object Consume : CharactersLibraryUiEvent()
    data object Refresh : CharactersLibraryUiEvent()
    data class LoadMore(val page: Int) : CharactersLibraryUiEvent()
}

data class CharactersLibraryUiState(
    val charactersList: List<Character>? = null
)