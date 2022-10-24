package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.ui.theme.*

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
        Text(
            text = text,
            style = Typography.bodyLarge
        )
    }
}