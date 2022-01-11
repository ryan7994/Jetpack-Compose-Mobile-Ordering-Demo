package com.ryanjames.jetpackmobileordering.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.toTwoDigitString
import com.ryanjames.jetpackmobileordering.ui.theme.*

@Composable
fun StarRatingWithReviews(
    modifier: Modifier = Modifier,
    rating: String,
    numberOfRatings: String,
    ratingColor: TextColor = TextColor.DarkTextColor,
    reviewColor: TextColor = TextColor.LightTextColor
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.ic_star), contentDescription = "Star",
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))

        TypeScaledTextView(label = rating, typeScale = TypeScaleCategory.Subtitle2, color = ratingColor)
        Spacer(
            modifier = Modifier
                .size(2.dp)
                .fillMaxWidth()
        )
        TypeScaledTextView(label = numberOfRatings, typeScale = TypeScaleCategory.Subtitle2, color = reviewColor)
    }
}

@Composable
fun EditAddress(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.edit),
            contentDescription = "Edit Address",
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterVertically)
        )
        Column(modifier = Modifier.padding(8.dp)) {
            TypeScaledTextView(label = "Delivering to", color = TextColor.StaticColor(Color.White), typeScale = TypeScaleCategory.H6)
            TypeScaledTextView(label = "15415 Cantrece Ln Cerritos CA", color = TextColor.StaticColor(Color.White), typeScale = TypeScaleCategory.Subtitle2)
        }
    }
}

@Composable
fun DisplayChip(label: String, textColor: TextColor, backgroundColor: Color) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
    ) {
        TypeScaledTextView(label = label, color = textColor, modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
    }
}


@Preview
@Composable
fun PreviewDisplayChip() {
    DisplayChip("Filipino", textColor = TextColor.StaticColor(Color.White), backgroundColor = ChipGray)
}

@Preview
@Composable
fun PreviewEditAddress() {
    Box(modifier = Modifier.background(DarkBlueGray)) {
        EditAddress()
    }
}

@Preview
@Composable
fun PreviewStarRatingWithReviews() {
    Column {
        StarRatingWithReviews(rating = "4.50", numberOfRatings = "100")

        MyComposeAppTheme(darkTheme = true) {
            Box(modifier = Modifier.background(AppTheme.colors.materialColors.background)) {
                StarRatingWithReviews(rating = "4.50", numberOfRatings = "100")
            }
        }
    }
}