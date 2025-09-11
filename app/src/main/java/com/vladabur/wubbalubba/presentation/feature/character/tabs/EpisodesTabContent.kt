package com.vladabur.wubbalubba.presentation.feature.character.tabs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.vladabur.wubbalubba.BuildConfig
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.presentation.common.preview.CharacterPreviewProvider
import com.vladabur.wubbalubba.presentation.ui.components.Chip
import com.vladabur.wubbalubba.presentation.ui.components.NonlazyGrid

@Composable
fun EpisodesTabContent(character: Character) {
    NonlazyGrid(
        columns = 6,
        itemCount = character.episode?.size ?: 0,
        gap = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        character.episode?.get(it)?.let { episode ->
            Chip(
                modifier = Modifier.fillMaxWidth(),
                label = episode.removePrefix(BuildConfig.BASE_URL + "episode/")
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EpisodesTabPreview(
    @PreviewParameter(CharacterPreviewProvider::class)
    character: Character
) {
    EpisodesTabContent(character)
}