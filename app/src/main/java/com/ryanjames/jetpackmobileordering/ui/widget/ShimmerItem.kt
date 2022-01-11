package com.ryanjames.jetpackmobileordering.ui.widget

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.ryanjames.jetpackmobileordering.ui.theme.ShimmerColorShadesDarkMode
import com.ryanjames.jetpackmobileordering.ui.theme.ShimmerColorShadesLightMode

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


@Composable
fun ShimmerAnimation() {

    val transition = rememberInfiniteTransition()
    val translateAnimX by transition.animateFloat(

        initialValue = 0f,
        targetValue = 3000f,
        animationSpec = infiniteRepeatable(

            // Tween Animates between values over specified [durationMillis]
            tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
            RepeatMode.Restart
        )
    )

    val translateAnimY by transition.animateFloat(

        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(

            tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            RepeatMode.Reverse
        )
    )

    val brush = Brush.linearGradient(
        colors = if (isSystemInDarkTheme()) ShimmerColorShadesDarkMode else ShimmerColorShadesLightMode,
        start = Offset(0f, 0f),
        end = Offset(translateAnimX, translateAnimY)
    )

    Column {

        Column(modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 16.dp)) {
            ShimmerItem(
                brush = brush,
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(20.dp))

            ShimmerItem(
                brush = brush, modifier = Modifier
                    .height(270.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(20.dp))

            ShimmerItem(
                brush = brush,
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth()
            )

        }

        Spacer(modifier = Modifier.size(20.dp))

        Column(modifier = Modifier.padding(start = 16.dp)) {
            ShimmerItem(
                brush = brush,
                modifier = Modifier
                    .height(34.dp)
                    .width(120.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))

            Row() {
                ShimmerItem(
                    brush = brush,
                    modifier = Modifier
                        .height(248.dp)
                        .width(284.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                ShimmerItem(
                    brush = brush,
                    modifier = Modifier
                        .height(248.dp)
                        .width(284.dp),
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                )
            }

        }
    }


}