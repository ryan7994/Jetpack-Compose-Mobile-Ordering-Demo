package com.ryanjames.composemobileordering.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.features.home.RestaurantCardState
import com.ryanjames.composemobileordering.ui.widget.StarRatingWithReviews
import com.skydoves.landscapist.glide.GlideImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantCard(state: RestaurantCardState, onClickCard: (id: String) -> Unit = {}) {

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(282.dp),
        onClick = {
            onClickCard.invoke(state.venueId)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {


            GlideImage(
                imageModel = state.imageUrl ?: "",
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.colors.placeholderColor),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = state.venueName,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                style = Typography.titleMedium
            )

            Spacer(modifier = Modifier.size(2.dp))

            Text(
                text = state.venueCategories,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                style = Typography.bodyMedium,
                color = AppTheme.colors.lightTextColor
            )

            Spacer(modifier = Modifier.size(2.dp))

            StarRatingWithReviews(rating = state.rating, numberOfRatings = state.numberOfRatings)
        }
    }
}

val venue1 = RestaurantCardState(
    "SSS",
    "Sisig Hooray",
    "Filipino (Traditional)",
    "4.50",
    "(100)",
    ""
)

val venue2 = RestaurantCardState(
    "",
    "JOL",
    "Jollibee",
    "Fast Food",
    "4.75",
    "(360)"
)

@Preview
@Composable
fun PreviewRestaurantCardLightMode() {
    RestaurantCard(
        venue1
    )
}

@Preview()
@Composable
fun PreviewRestaurantCardDarkMode() {
    MyComposeAppTheme(darkTheme = true) {
        RestaurantCard(
            venue1
        )
    }
}