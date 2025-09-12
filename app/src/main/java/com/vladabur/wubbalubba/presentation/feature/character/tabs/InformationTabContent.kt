package com.vladabur.wubbalubba.presentation.feature.character.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.vladabur.wubbalubba.R
import com.vladabur.wubbalubba.domain.models.Character
import com.vladabur.wubbalubba.domain.models.CharacterGender
import com.vladabur.wubbalubba.domain.models.CharacterSpecies
import com.vladabur.wubbalubba.domain.models.CharacterStatus
import com.vladabur.wubbalubba.presentation.common.preview.CharacterPreviewProvider
import com.vladabur.wubbalubba.presentation.ui.components.ChipsFlowRow
import com.vladabur.wubbalubba.presentation.ui.theme.AppTypography

@Composable
fun InformationTabContent(character: Character) {
    Column(modifier = Modifier.padding(16.dp)) {
        val itemsList =
            listOfNotNull(
                character.status?.let { CharacterStatus.toValue(it) },
                character.species?.let { CharacterSpecies.toValue(it) },
                character.type,
                character.gender?.let { CharacterGender.toValue(it) }
            )

        ChipsFlowRow(itemsList = itemsList)
        Spacer(modifier = Modifier.height(24.dp))

        character.origin?.name?.let {
            Text(
                text = stringResource(R.string.character_details_origin_title),
                style = AppTypography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = AppTypography.bodyLarge,
            )
        }
        character.location?.name?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.character_details_location_title),
                style = AppTypography.headlineSmall,

                )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = AppTypography.bodyLarge,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InformationTabContentPreview(
    @PreviewParameter(CharacterPreviewProvider::class)
    character: Character
) {
    InformationTabContent(character)
}