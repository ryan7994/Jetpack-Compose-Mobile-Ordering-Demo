package com.ryanjames.jetpackmobileordering.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ryanjames.jetpackmobileordering.ui.theme.SunYellow
import com.ryanjames.jetpackmobileordering.ui.theme.TextColor
import com.ryanjames.jetpackmobileordering.ui.theme.TypeScaledTextView

@Composable
fun CustomSnackbar(
    data: SnackbarData
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SunYellow)
            .padding(16.dp)
    ) {
        TypeScaledTextView(label = data.message, color = TextColor.StaticColor(Color.Black))
    }

}