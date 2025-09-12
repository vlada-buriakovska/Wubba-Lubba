package com.vladabur.wubbalubba.presentation.feature.library

import androidx.lifecycle.viewModelScope
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.domain.models.CharactersList
import com.vladabur.wubbalubba.domain.usecases.GetCharactersUseCase
import com.vladabur.wubbalubba.domain.usecases.GetCharactersUseCase.Params
import com.vladabur.wubbalubba.domain.usecases.base.ResultCallbacks
import com.vladabur.wubbalubba.presentation.FilterItem
import com.vladabur.wubbalubba.presentation.common.base.BaseViewModel
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.ApplyFilters
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Consume
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.LoadMore
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.OnGenderFilterChanged
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.OnSearchQueryChanged
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.OnSpeciesFilterChanged
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.OnStatusFilterChanged
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Refresh
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.ResetFilters
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Retry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
        private const val SEARCH_DEBOUNCE_TIME_IN_MILLIS = 500L
    }

    private val managerUiState = MutableStateFlow(CharactersLibraryUiState())
    val uiState: StateFlow<CharactersLibraryUiState> = managerUiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        initAllFilterLists()
        getCharacters()
    }

    fun onEvent(event: CharactersLibraryUiEvent) {
        when (event) {
            Consume -> consumeError()
            Retry -> retry()
            Refresh -> {
                getCharacters(isRefreshing = true)
            }

            is LoadMore -> {
                getCharacters(page = event.page)
            }

            is OnSearchQueryChanged -> {
                managerUiState.update {
                    it.copy(searchQuery = event.searchQuery)
                }
                searchJob?.cancel()

                searchJob = viewModelScope.launch {
                    delay(SEARCH_DEBOUNCE_TIME_IN_MILLIS)
                    getCharacters()
                }
            }

            is OnStatusFilterChanged -> {
                managerUiState.update {
                    val newItem = if (it.statusFilterItem == event.filterItem) null else event.filterItem
                    it.copy(
                        statusFilterItem = newItem
                    )
                }
            }


            is OnSpeciesFilterChanged -> {
                managerUiState.update {
                    val newItem = if (it.speciesFilterItem == event.filterItem) null else event.filterItem
                    it.copy(
                        speciesFilterItem = newItem
                    )
                }
            }

            is OnGenderFilterChanged -> {
                managerUiState.update {
                    val newItem = if (it.genderFilterItem == event.filterItem) null else event.filterItem
                    it.copy(
                        genderFilterItem = newItem
                    )
                }
            }

            ResetFilters -> {
                resetFilters()
                getCharacters()
            }

            ApplyFilters -> {
                getCharacters()
            }
        }
    }

    private fun initAllFilterLists() {
        val statusList = CharacterStatus.entries.mapNotNull { it.value }.map { item ->
            FilterItem(item)
        }
        val speciesList =
            CharacterSpecies.entries.mapNotNull { it.value }.map { item ->
                FilterItem(item.replaceFirstChar { it.uppercase() })
            }
        val genderList = CharacterGender.entries.mapNotNull { it.value }.map { item ->
            FilterItem(item.replaceFirstChar { it.uppercase() })
        }
        managerUiState.update {
            it.copy(
                statusFilterItems = statusList,
                speciesFilterItems = speciesList,
                genderFilterItems = genderList
            )
        }
    }

    private fun resetFilters() {
        managerUiState.update {
            it.copy(
                statusFilterItem = null,
                speciesFilterItem = null,
                genderFilterItem = null
            )
        }
    }

    private fun getCharacters(
        page: Int = FIRST_PAGE_INDEX,
        isRefreshing: Boolean = false,
        isForceReload: Boolean = false,
        isFromLocal: Boolean = false
    ) {
        getCharactersUseCase(
            coroutineScope = viewModelScope,
            params = Params(
                isFromLocal = isFromLocal || baseUiState.value.isConnectionError == true,
                isForceReload = isForceReload,
                page = page,
                name = managerUiState.value.searchQuery,
                status = CharacterStatus.fromValue(managerUiState.value.statusFilterItem?.label),
                species = CharacterSpecies.fromValue(managerUiState.value.speciesFilterItem?.label),
                gender = CharacterGender.fromValue(managerUiState.value.genderFilterItem?.label),
            ),
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
                    getCharacters(
                        page = page,
                        isRefreshing = isRefreshing,
                        isFromLocal = isFromLocal,
                    )
                    handleOnConnectionError {
                        getCharacters(
                            page = FIRST_PAGE_INDEX,
                            isRefreshing = false,
                            isFromLocal = false,
                            isForceReload = true
                        )
                    }
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
    data class OnStatusFilterChanged(val filterItem: FilterItem) : CharactersLibraryUiEvent()
    data class OnSpeciesFilterChanged(val filterItem: FilterItem) : CharactersLibraryUiEvent()
    data class OnGenderFilterChanged(val filterItem: FilterItem) : CharactersLibraryUiEvent()
    data object ResetFilters : CharactersLibraryUiEvent()
    data object ApplyFilters : CharactersLibraryUiEvent()
}

data class CharactersLibraryUiState(
    val currentPage: Int? = null,
    val charactersListTotal: Int? = null,
    val charactersList: List<Character>? = null,
    val isRefreshing: Boolean? = null,
    val isLoadingMore: Boolean? = null,
    val searchQuery: String? = null,
    val statusFilterItems: List<FilterItem>? = null,
    val speciesFilterItems: List<FilterItem>? = null,
    val genderFilterItems: List<FilterItem>? = null,
    val statusFilterItem: FilterItem? = null,
    val speciesFilterItem: FilterItem? = null,
    val genderFilterItem: FilterItem? = null
)