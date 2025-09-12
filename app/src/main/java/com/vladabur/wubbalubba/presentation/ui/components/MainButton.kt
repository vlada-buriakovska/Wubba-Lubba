package com.vladabur.wubbalubba.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vladabur.wubbalubba.presentation.ui.theme.AppTypography


@Composable
fun MainButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(20),
        onClick = {
            onClick()
        }
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            text = label,
            style = AppTypography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainButtonPreview() {
    MainButton(
        label = "Apply",
        onClick = {}
    )
}