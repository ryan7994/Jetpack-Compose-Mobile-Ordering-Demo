package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.ui.theme.CoralRed
import com.ryanjames.composemobileordering.ui.theme.TypeScaleCategory
import com.ryanjames.composemobileordering.ui.theme.TypeScaledTextView

@Composable
fun LoadingSpinnerWithText(text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = CoralRed)
        Spacer(modifier = Modifier.size(16.dp))
        TypeScaledTextView(label = text, typeScale = TypeScaleCategory.Subtitle1)
    }
}