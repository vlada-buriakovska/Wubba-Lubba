package com.vladabur.wubbalubba.presentation.feature.library

import androidx.lifecycle.viewModelScope
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharactersList
import com.vladabur.wubbalubba.domain.usecases.GetCharactersUseCase
import com.vladabur.wubbalubba.domain.usecases.GetCharactersUseCase.Params
import com.vladabur.wubbalubba.domain.usecases.base.ResultCallbacks
import com.vladabur.wubbalubba.presentation.common.BaseViewModel
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Consume
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.LoadMore
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.OnSearchQueryChanged
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Refresh
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Retry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CharactersLibraryVM @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
) :
    BaseViewModel() {

    companion object {
        const val DEFAULT_LIST_PAGE_SIZE = 20
        const val FIRST_PAGE_INDEX = 0
        const val EMPTY_LIST_ERROR = "There is nothing here"
        private const val SEARCH_DEBOUNCE_TIME_IN_MILLIS = 300L
    }

    private val managerUiState = MutableStateFlow(CharactersLibraryUiState())
    val uiState: StateFlow<CharactersLibraryUiState> = managerUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        _searchQuery
            .debounce(SEARCH_DEBOUNCE_TIME_IN_MILLIS)
            .onEach { query ->
                getCharacters(name = query)
            }
            .launchIn(viewModelScope)
        getCharacters()
    }

    fun onEvent(charactersLibraryUiEvent: CharactersLibraryUiEvent) {
        when (charactersLibraryUiEvent) {
            Consume -> consumeError()
            Retry -> retry()
            Refresh -> {
                getCharacters(isRefreshing = true)
            }

            is LoadMore -> {
                getCharacters(page = charactersLibraryUiEvent.page, name = _searchQuery.value)
            }

            is OnSearchQueryChanged -> {
                managerUiState.update {
                    it.copy(searchQuery = charactersLibraryUiEvent.searchQuery)
                }
                _searchQuery.value = charactersLibraryUiEvent.searchQuery
            }
        }
    }

    private fun getCharacters(
        page: Int = FIRST_PAGE_INDEX,
        name: String? = null,
        isRefreshing: Boolean = false
    ) {
        getCharactersUseCase(
            coroutineScope = viewModelScope,
            params = Params(page = page, name = name),
            result = ResultCallbacks(
                onSuccess = { result ->
                    handleCharactersListResult(
                        page = page,
                        total = result.total ?: 0,
                        newList = getNewList(page, isRefreshing, result),
                        isRefreshing = isRefreshing,
                    )
                },
                onLoading = { isLoading ->
                    handleLoading(page = page, isRefreshing = isRefreshing, isLoading = isLoading)
                },
                onError = {
                    if (it.error == EMPTY_LIST_ERROR) {
                        handleCharactersListResult(
                            page = 0,
                            total = 0,
                            newList = emptyList(),
                            isRefreshing = isRefreshing,
                        )
                    } else {
                        handleOnError(it)
                    }
                },
                onUnexpectedError = ::handleOnUnexpectedError,
                onConnectionError = {
                    handleOnConnectionError { getCharacters(page) }
                }
            )
        )
    }

    private fun getNewList(
        page: Int,
        isRefreshing: Boolean,
        result: CharactersList
    ): List<Character>? {
        return if (page == FIRST_PAGE_INDEX || isRefreshing) {
            result.characters
        } else {
            val finalList =
                managerUiState.value.charactersList?.plus(
                    result.characters ?: emptyList()
                )
            finalList
        }
    }

    private fun handleCharactersListResult(
        page: Int,
        total: Int,
        newList: List<Character>?,
        isRefreshing: Boolean
    ) {
        val searchQuery = if (isRefreshing) {
            null
        } else {
            managerUiState.value.searchQuery
        }
        managerUiState.update {
            it.copy(
                currentPage = page,
                charactersListTotal = total,
                charactersList = newList,
                searchQuery = searchQuery
            )
        }
    }

    private fun handleLoading(page: Int, isRefreshing: Boolean, isLoading: Boolean) {
        if (isRefreshing) {
            managerUiState.update {
                it.copy(isRefreshing = isLoading)
            }
        } else if (page == 0) {
            handleOnLoading(isLoading)
        } else {
            managerUiState.update {
                it.copy(isLoadingMore = isLoading)
            }
        }
    }
}

sealed class CharactersLibraryUiEvent {
    data object Retry : CharactersLibraryUiEvent()
    data object Consume : CharactersLibraryUiEvent()
    data object Refresh : CharactersLibraryUiEvent()
    data class LoadMore(val page: Int) : CharactersLibraryUiEvent()
    data class OnSearchQueryChanged(val searchQuery: String) : CharactersLibraryUiEvent()
}

data class CharactersLibraryUiState(
    val currentPage: Int? = null,
    val charactersListTotal: Int? = null,
    val charactersList: List<Character>? = null,
    val isRefreshing: Boolean? = null,
    val isLoadingMore: Boolean? = null,
    val searchQuery: String? = null
)