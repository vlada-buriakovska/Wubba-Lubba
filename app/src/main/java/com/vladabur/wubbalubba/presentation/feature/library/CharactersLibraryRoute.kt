package com.vladabur.wubbalubba.presentation.feature.library

import PaginatedLazyColumn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vladabur.wubbalubba.R
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.presentation.common.BaseUiState
import com.vladabur.wubbalubba.presentation.extensions.shimmerEffect
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.LoadMore
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.OnSearchQueryChanged
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryUiEvent.Refresh
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryVM.Companion.DEFAULT_LIST_PAGE_SIZE
import com.vladabur.wubbalubba.presentation.ui.theme.AppTypography

@Composable
fun CharactersLibraryRoute(
    viewModel: CharactersLibraryVM = hiltViewModel(), onCharacterClicked: ((Character) -> Unit)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersLibraryScreen(
    uiState: CharactersLibraryUiState,
    baseUiState: BaseUiState,
    onEvent: (CharactersLibraryUiEvent) -> Unit,
    onCharacterClicked: ((Character) -> Unit)
) {
    val lazyListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
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
            expanded = false,
            onExpandedChange = {},
            content = {},
        )
        Box(modifier = Modifier.fillMaxSize()) {
            val isListEmpty = uiState.charactersList?.isEmpty() == true
            this@Column.AnimatedVisibility(isListEmpty && !baseUiState.isLoading) {
                EmptyListPlaceholder()
            }
            this@Column.AnimatedVisibility(!isListEmpty || baseUiState.isLoading) {
                PaginatedLazyColumn(
                    listState = lazyListState,
                    items = uiState.charactersList ?: emptyList(),
                    itemKey = {
                        it.id
                    },
                    isLoading = baseUiState.isLoading,
                    isLoadingMore = uiState.isLoadingMore == true,
                    isRefreshing = uiState.isRefreshing == true,
                    pageSize = DEFAULT_LIST_PAGE_SIZE,
                    total = uiState.charactersListTotal ?: 0,
                    onLoadMore = { page ->
                        onEvent.invoke(LoadMore(page))
                    },
                    onRefresh = {
                        onEvent.invoke(Refresh)
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
            character.species?.let {
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
            painter = painterResource(id = R.drawable.ic_sad_rick),
            contentDescription = stringResource(R.string.empty_list_image_description)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_list_description),
            style = AppTypography.bodyLarge
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun CharactersLibraryScreenPreview() {
    CharactersLibraryScreen(
        uiState = CharactersLibraryUiState(),
        baseUiState = BaseUiState(),
        onEvent = {},
        onCharacterClicked = { },
    )
}