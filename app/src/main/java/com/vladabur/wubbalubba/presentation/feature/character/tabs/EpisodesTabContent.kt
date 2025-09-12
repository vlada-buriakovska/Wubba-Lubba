package com.vladabur.wubbalubba.presentation.feature.character.tabs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.presentation.common.preview.CharacterPreviewProvider
import com.vladabur.wubbalubba.presentation.ui.kit.Chip
import com.vladabur.wubbalubba.presentation.ui.components.NonlazyGrid

@Composable
fun EpisodesTabContent(character: Character) {
    NonlazyGrid(
        columns = 6,
        itemCount = character.episodes?.size ?: 0,
        gap = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        character.episodes?.get(it)?.let { episode ->
            Chip(
                modifier = Modifier.fillMaxWidth(),
                label = episode
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