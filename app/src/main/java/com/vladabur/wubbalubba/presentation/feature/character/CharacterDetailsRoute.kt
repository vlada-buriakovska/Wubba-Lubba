package com.vladabur.wubbalubba.presentation.feature.character

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons.AutoMirrored
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vladabur.wubbalubba.R
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.presentation.common.base.BaseUiState
import com.vladabur.wubbalubba.presentation.common.preview.CharacterPreviewProvider
import com.vladabur.wubbalubba.presentation.extensions.getFullDate
import com.vladabur.wubbalubba.presentation.extensions.shimmerEffect
import com.vladabur.wubbalubba.presentation.feature.character.tabs.EpisodesTabContent
import com.vladabur.wubbalubba.presentation.feature.character.tabs.InformationTabContent
import com.vladabur.wubbalubba.presentation.ui.components.ConnectionError
import com.vladabur.wubbalubba.presentation.ui.components.ErrorSnackBar
import com.vladabur.wubbalubba.presentation.ui.theme.AppTypography


@Composable
fun CharacterDetailsRoute(
    viewModel: CharacterDetailsVM = hiltViewModel(),
    characterId: Int,
    onUpClicked: (() -> Unit)
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val baseState = viewModel.baseUiState.collectAsStateWithLifecycle()
    viewModel.setData(characterId = characterId)
    CharacterDetailsScreen(
        uiState = uiState.value,
        baseUiState = baseState.value,
        onEvent = viewModel::onEvent,
        onUpClicked = onUpClicked
    )
}

@Composable
fun CharacterDetailsScreen(
    uiState: CharacterDetailsUiState,
    baseUiState: BaseUiState,
    onEvent: (CharacterDetailsUiEvent) -> Unit,
    onUpClicked: (() -> Unit)
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Box {
        Crossfade(
            targetState = baseUiState.isLoading == true || baseUiState.isLoading == null,
        ) { isContentLoading ->
            if (isContentLoading) {
                PageLoadingSkeleton()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box {
                        AsyncImage(
                            model = uiState.character?.image,
                            contentDescription = uiState.character?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                        IconButton(
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(shape = CircleShape)
                                .background(color = MaterialTheme.colorScheme.surface),
                            onClick = {
                                onUpClicked()
                            }
                        ) {
                            Icon(
                                imageVector = AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(R.string.all_back),
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        uiState.character?.name?.let {
                            Text(
                                text = it,
                                style = AppTypography.titleLarge
                            )
                        }
                        uiState.character?.created?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = it.getFullDate(),
                                style = AppTypography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    val tabsList = listOf(
                        stringResource(R.string.character_details_title),
                        stringResource(R.string.character_details_episodes_title)
                    )

                    // Tabs
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .padding(1.dp),
                        indicator = {
                            Box { }
                        }
                    ) {
                        tabsList.forEachIndexed { index, text ->
                            val selected = selectedTabIndex == index
                            val backgroundColor = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface
                            val contentColor = if (selected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface

                            Tab(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        backgroundColor
                                    ),
                                selected = selected,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = text, color = contentColor) }
                            )
                        }
                    }
                    uiState.character?.let {
                        when (selectedTabIndex) {
                            0 -> InformationTabContent(it)
                            1 -> EpisodesTabContent(it)
                        }
                    }
                }
            }
        }
        if (baseUiState.isConnectionError == true) {
            ConnectionError(
                onRetry = {
                    onEvent(CharacterDetailsUiEvent.Retry)
                }
            )
        }
        if (baseUiState.error != null) {
            ErrorSnackBar(
                error = baseUiState.error,
                onDismissed = {
                    onEvent(CharacterDetailsUiEvent.Consume)
                }
            )
        }
    }
}

@Composable
private fun PageLoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shimmerEffect()
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
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
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .clip(shape = RoundedCornerShape(32.dp))
                        .weight(1F)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .clip(shape = RoundedCornerShape(32.dp))
                        .weight(1F)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .clip(shape = RoundedCornerShape(32.dp))
                        .weight(1F)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .clip(shape = RoundedCornerShape(32.dp))
                        .weight(1F)
                        .shimmerEffect()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
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
                    .height(16.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(16.dp))
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
                    .height(16.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun CharacterDetailsScreenPreview(
    @PreviewParameter(CharacterPreviewProvider::class)
    character: Character
) {
    CharacterDetailsScreen(
        uiState = CharacterDetailsUiState(
            character = character
        ),
        baseUiState = BaseUiState(isLoading = false),
        onEvent = {},
        onUpClicked = {}
    )
}