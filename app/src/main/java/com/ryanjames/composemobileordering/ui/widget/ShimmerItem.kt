package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerItem(brush: Brush, modifier: Modifier, shape: Shape = RoundedCornerShape(16.dp)) {
    Column {
        Spacer(
            modifier = modifier
                .clip(shape)
                .background(brush = brush)
        )
    }
}