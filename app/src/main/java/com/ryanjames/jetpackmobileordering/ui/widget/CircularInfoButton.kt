package com.ryanjames.jetpackmobileordering.ui.widget

import android.content.res.Configuration
import android.widget.Space
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.ui.theme.AppTheme
import com.ryanjames.jetpackmobileordering.ui.theme.MyComposeAppTheme
import com.ryanjames.jetpackmobileordering.ui.theme.TypeScaledTextView

@Composable
fun CircularInfoButton(
    label: String,
    @DrawableRes resId: Int,
    onClick: () -> Unit
) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(80.dp),
            backgroundColor = AppTheme.colors.materialColors.surface
        ) {
            Image(
                painter = painterResource(id = resId), contentDescription = "star",
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.size(4.dp))
        TypeScaledTextView(label = label)
    }
}

@Preview
@Composable
fun PreviewCircularInfoButton() {
    CircularInfoButton(label = "Call", R.drawable.ic_star) {}
}

@Preview
@Composable
fun PreviewDarkCircularInfoButton() {
    MyComposeAppTheme(darkTheme = true) {
        CircularInfoButton(label = "Call", R.drawable.ic_star) {}
    }
}