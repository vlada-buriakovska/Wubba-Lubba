package com.vladabur.wubbalubba.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.presentation.FilterItem
import com.vladabur.wubbalubba.presentation.common.preview.CharacterPreviewProvider
import com.vladabur.wubbalubba.presentation.ui.kit.Chip
import androidx.compose.material.Chip as MaterialChip

@Composable
fun ChipsFlowRow(
    itemsList: List<String>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            itemsList.forEach { item ->
                if (item.isNotEmpty()) {
                    Chip(
                        label = item,
                        borderColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterChipsFlowRow(
    itemsList: List<FilterItem>,
    modifier: Modifier = Modifier,
    onClick: (FilterItem) -> Unit
) {
    FlowRow(
        modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), content = {
            itemsList.forEach { item ->
                if (item.label.isNotEmpty()) {
                    val backgroundColor = if (item.isEnabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                    val contentColor = if (item.isEnabled) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                    val borderColor = if (item.isEnabled) Color.Transparent
                    else MaterialTheme.colorScheme.secondary

                    MaterialChip(
                        content = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center,
                                color = contentColor
                            )
                        },
                        colors = ChipDefaults.chipColors(
                            backgroundColor = backgroundColor
                        ),
                        border = BorderStroke(
                            width = 1.dp, color = borderColor
                        ),
                        onClick = {
                            onClick(item)
                        }
                    )
                }
            }
        })
}

@Preview(showBackground = true)
@Composable
private fun CharacterDetailsChipsPreview(
    @PreviewParameter(CharacterPreviewProvider::class)
    character: Character
) {
    val itemsList =
        listOfNotNull(
            character.status?.let { CharacterStatus.toValue(it) },
            character.species?.let { CharacterSpecies.toValue(it) },
            character.type,
            character.gender?.let { CharacterGender.toValue(it) }
        )
    val clickableItemsList = itemsList.mapIndexed { index, item ->
        val isEnabled = index % 2 == 0
        FilterItem(item, isEnabled)
    }
    Column {
        ChipsFlowRow(itemsList)
        FilterChipsFlowRow(
            clickableItemsList, onClick = {

            }
        )
    }
}