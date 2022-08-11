package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.ui.theme.ChipGray
import com.ryanjames.composemobileordering.ui.theme.RubikTypography

@Composable
fun DisplayChip(label: String, textColor: Color, backgroundColor: Color) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
    ) {
        Text(
            text = label,
            style = RubikTypography.titleSmall,
            color = textColor,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        )
    }
}


@Preview
@Composable
fun PreviewDisplayChip() {
    DisplayChip("Filipino", textColor = Color.White, backgroundColor = ChipGray)
}
