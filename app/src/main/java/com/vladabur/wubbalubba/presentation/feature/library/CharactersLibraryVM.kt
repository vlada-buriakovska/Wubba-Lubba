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
import com.vladabur.wubbalubba.presentation.extensions.toggleFilterItem
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
                    it.copy(
                        statusFilter = managerUiState.value.statusFilter?.toggleFilterItem(event.filterItem)
                    )
                }
            }


            is OnSpeciesFilterChanged -> {
                managerUiState.update {
                    it.copy(
                        speciesFilter = managerUiState.value.speciesFilter?.toggleFilterItem(event.filterItem)
                    )
                }
            }

            is OnGenderFilterChanged -> {
                managerUiState.update {
                    it.copy(
                        genderFilter = managerUiState.value.genderFilter?.toggleFilterItem(event.filterItem)
                    )
                }
            }

            ResetFilters -> {
                initAllFilterLists()
                getCharacters()
            }

            ApplyFilters -> {
                getCharacters()
            }
        }
    }

    private fun initAllFilterLists() {
        val statusList = CharacterStatus.entries.mapNotNull { it.value }.map { item ->
            FilterItem(item, false)
        }
        val speciesList =
            CharacterSpecies.entries.mapNotNull { it.value }.map { item ->
                FilterItem(item.replaceFirstChar { it.uppercase() }, false)
            }
        val genderList = CharacterGender.entries.mapNotNull { it.value }.map { item ->
            FilterItem(item.replaceFirstChar { it.uppercase() }, false)
        }
        managerUiState.update {
            it.copy(
                statusFilter = statusList,
                speciesFilter = speciesList,
                genderFilter = genderList
            )
        }
    }

    private fun getCharacters(
        page: Int = FIRST_PAGE_INDEX,
        isRefreshing: Boolean = false
    ) {
        getCharactersUseCase(
            coroutineScope = viewModelScope,
            params = Params(
                page = page,
                name = managerUiState.value.searchQuery,
                statuses = managerUiState.value.statusFilter?.mapNotNull {
                    if (it.isEnabled) CharacterStatus.fromValue(
                        it.label
                    ) else null
                },
                species = managerUiState.value.speciesFilter?.mapNotNull {
                    if (it.isEnabled) CharacterSpecies.fromValue(
                        it.label
                    ) else null
                },
                genders = managerUiState.value.genderFilter?.mapNotNull {
                    if (it.isEnabled) CharacterGender.fromValue(
                        it.label
                    ) else null
                }
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
    val statusFilter: List<FilterItem>? = null,
    val speciesFilter: List<FilterItem>? = null,
    val genderFilter: List<FilterItem>? = null
)