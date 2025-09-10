package com.vladabur.wubbalubba.presentation.feature.library

import PaginatedLazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.presentation.common.BaseUiState
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryVM.Companion.DEFAULT_LIST_PAGE_SIZE
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

@Composable
fun CharactersLibraryScreen(
    uiState: CharactersLibraryUiState,
    baseUiState: BaseUiState,
    onEvent: (CharactersLibraryUiEvent) -> Unit,
    onCharacterClicked: ((Character) -> Unit)
) {
    val lazyListState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize()) {
        PaginatedLazyColumn(
            listState = lazyListState,
            items = uiState.charactersList ?: emptyList(),
            itemKey = {
                it.id
            },
            isLoading = uiState.isLoadingMore == true,
            isRefreshing = uiState.isRefreshing == true,
            pageSize = DEFAULT_LIST_PAGE_SIZE,
            total = uiState.charactersListTotal ?: 0,
            onLoadMore = { page ->
                onEvent.invoke(CharactersLibraryUiEvent.LoadMore(page))
            },
            onRefresh = {
                onEvent.invoke(CharactersLibraryUiEvent.Refresh)
            },
        ) { character ->
            CharacterListItem(character) {
                onCharacterClicked(character)
            }
        }
        if (baseUiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = LightPrimaryRed
                )
            }
        }
    }
}

@Composable
fun CharacterListItem(character: Character, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = character.image,
            contentDescription = "Image of ${character.name}",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            character.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            character.species?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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