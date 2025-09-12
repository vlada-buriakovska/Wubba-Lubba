package com.vladabur.wubbalubba.presentation.feature.library

import PaginatedLazyColumn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vladabur.wubbalubba.R
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.presentation.common.base.BaseUiState
import com.vladabur.wubbalubba.presentation.extensions.shimmerEffect
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.ApplyFilters
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.LoadMore
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.OnGenderFilterChanged
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.OnSearchQueryChanged
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.OnSpeciesFilterChanged
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.OnStatusFilterChanged
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Refresh
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.ResetFilters
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryVM.Companion.DEFAULT_LIST_PAGE_SIZE
import com.vladabur.wubbalubba.presentation.ui.components.ConnectionError
import com.vladabur.wubbalubba.presentation.ui.components.ErrorSnackBar
import com.vladabur.wubbalubba.presentation.ui.components.FilterChipsFlowRow
import com.vladabur.wubbalubba.presentation.ui.kit.MainButton
import com.vladabur.wubbalubba.presentation.ui.kit.SecondaryButton
import com.vladabur.wubbalubba.presentation.ui.theme.AppTypography
import com.vladabur.wubbalubba.presentation.ui.theme.LightPrimaryRed

@Composable
fun CharactersLibraryRoute(
    viewModel: CharactersLibraryVM = hiltViewModel(),
    onCharacterClicked: ((Character) -> Unit)
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val baseState = viewModel.baseUiState.collectAsStateWithLifecycle()
    CharactersLibraryScreen(
        uiState = uiState.value,
        baseUiState = baseState.value,
        onEvent = viewModel::onEvent,
        onCharacterClicked = onCharacterClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CharactersLibraryScreen(
    uiState: CharactersLibraryUiState,
    baseUiState: BaseUiState,
    onEvent: (CharactersLibraryUiEvent) -> Unit,
    onCharacterClicked: ((Character) -> Unit)
) {
    val lazyListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    Box {
        Column {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal),
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = AppTypography.titleMedium
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            showFilterBottomSheet = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(R.string.all_filter),

                            )
                    }
                }
            )
            AnimatedVisibility(baseUiState.isConnectionError == true) {
                ConnectionError(
                    onRetry = {
                        onEvent(CharactersLibraryUiEvent.Retry)
                    }
                )
            }
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.fillMaxWidth(),
                        query = uiState.searchQuery ?: String(),
                        onQueryChange = {
                            onEvent(OnSearchQueryChanged(searchQuery = it))
                        },
                        onSearch = {
                            keyboardController?.hide()
                            onEvent(OnSearchQueryChanged(searchQuery = it))
                        },
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text(stringResource(R.string.all_search)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search, contentDescription = null
                            )
                        },
                    )
                },
                colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                expanded = false,
                onExpandedChange = {},
                content = {},
                windowInsets = SearchBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal)
            )

            val isListEmpty = uiState.charactersList?.isEmpty() == true
            val shouldShowEmptyListPlaceHolder = isListEmpty && baseUiState.isLoading == false
            Crossfade(
                targetState = shouldShowEmptyListPlaceHolder,
            ) {

                val pullRefreshState = rememberPullRefreshState(
                    refreshing = uiState.isRefreshing == true, onRefresh = {
                        onEvent.invoke(Refresh)
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {
                    if (it) {
                        EmptyListPlaceholder()
                    } else {
                        PaginatedLazyColumn(
                            listState = lazyListState,
                            items = uiState.charactersList ?: emptyList(),
                            itemKey = { item ->
                                item.id
                            },
                            isLoading = baseUiState.isLoading == true,
                            isLoadingMore = uiState.isLoadingMore == true,
                            pageSize = DEFAULT_LIST_PAGE_SIZE,
                            total = uiState.charactersListTotal ?: 0,
                            onLoadMore = { page ->
                                onEvent.invoke(LoadMore(page))
                            },
                            content = { character ->
                                CharacterListItem(character) {
                                    onCharacterClicked(character)
                                }
                            },
                            placeHolder = {
                                CharacterListItemPlaceholder()
                            }
                        )
                    }
                    PullRefreshIndicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        refreshing = uiState.isRefreshing == true,
                        state = pullRefreshState,
                        backgroundColor = White,
                        contentColor = LightPrimaryRed
                    )
                }
            }
        }

        if (showFilterBottomSheet) {
            FilterBottomSheet(
                uiState = uiState,
                onEvent = onEvent,
                onBottomSheetDismissed = {
                    showFilterBottomSheet = false
                }
            )
        }

        if (baseUiState.error != null) {
            ErrorSnackBar(
                error = baseUiState.error,
                onDismissed = {
                    onEvent(CharactersLibraryUiEvent.Consume)
                }
            )
        }
    }
}

@Composable
private fun CharacterListItem(character: Character, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(all = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = character.image,
            contentDescription = character.name,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            character.name?.let {
                Text(
                    text = it, style = AppTypography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            CharacterSpecies.toValue(character.species)?.let {
                Text(
                    text = it,
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CharacterListItemPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
fun EmptyListPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_morty),
            contentDescription = stringResource(R.string.empty_list_image_description)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_list_description),
            style = AppTypography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    uiState: CharactersLibraryUiState,
    onEvent: (CharactersLibraryUiEvent) -> Unit,
    onBottomSheetDismissed: () -> Unit,
) {
    val filterBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = filterBottomSheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = {
            onBottomSheetDismissed()
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.all_filter),
                style = AppTypography.titleLarge,
                textAlign = TextAlign.Center
            )
            uiState.statusFilterItems?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.character_details_status_title),
                    style = AppTypography.titleMedium,
                )
                FilterChipsFlowRow(
                    itemsList = it,
                    chosenItem = uiState.statusFilterItem,
                    onClick = { item ->
                        onEvent(OnStatusFilterChanged(item))
                    },
                )
            }
            uiState.speciesFilterItems?.let {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.character_details_species_title),
                    style = AppTypography.titleMedium,
                )
                FilterChipsFlowRow(
                    itemsList = it,
                    chosenItem = uiState.speciesFilterItem,
                    onClick = { item ->
                        onEvent(OnSpeciesFilterChanged(item))
                    },
                )
            }
            uiState.genderFilterItems?.let {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.character_details_gender_title),
                    style = AppTypography.titleMedium,
                )
                FilterChipsFlowRow(
                    itemsList = it,
                    chosenItem = uiState.genderFilterItem,
                    onClick = { item ->
                        onEvent(OnGenderFilterChanged(item))
                    },
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                SecondaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    label = "Reset All",
                    onClick = {
                        onEvent(ResetFilters)
                        onBottomSheetDismissed()
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                MainButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    label = "Apply",
                    onClick = {
                        onEvent(ApplyFilters)
                        onBottomSheetDismissed()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CharactersLibraryScreenPreview() {
    CharactersLibraryScreen(
        uiState = CharactersLibraryUiState(),
        baseUiState = BaseUiState(),
        onEvent = {},
        onCharacterClicked = { },
    )
}