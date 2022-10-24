package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.ui.theme.*

@Composable
fun RestaurantHeader(state: RestaurantDisplayModel, onClickUp: () -> Unit, onClickInfo: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Image(
            painter = painterResource(id = R.drawable.placeholder), contentDescription = "",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            alpha = 0.5f
        )

        val gradient = Brush.horizontalGradient(listOf(Color(0X8C202020), Color(0x8C1e2d4e)))
        Box(
            modifier = Modifier
                .background(gradient)
                .matchParentSize()
        )

        Column(modifier = Modifier.padding(16.dp)) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Image(
                    painter = painterResource(id = R.drawable.circle_up), contentDescription = "Back button",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                            onClickUp.invoke()
                        }
                )

                Image(
                    painter = painterResource(id = R.drawable.information), contentDescription = "Info",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                            onClickInfo.invoke()
                        }
                )

            }




            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = state.venueName,
                color = Color.White,
                style = Typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(2.dp))

            Text(
                text = state.venueAddress,
                color = Color.White,
                style = Typography.bodyMedium
            )


            Spacer(modifier = Modifier.size(4.dp))

            FlowRow(crossAxisSpacing = 8.dp, mainAxisSpacing = 8.dp) {
                state.categories.forEach { category ->
                    DisplayChip(label = category, textColor = Color.White, backgroundColor = ChipGray)
                }
            }

            Spacer(modifier = Modifier.size(8.dp))
            StarRatingWithReviews(
                rating = state.rating,
                numberOfRatings = state.noOfReviews,
                ratingColor = Color.White,
                reviewColor = Color.White
            )
        }

    }
}

data class RestaurantDisplayModel(
    val venueName: String,
    val venueAddress: String,
    val categories: List<String>,
    val rating: String,
    val noOfReviews: String
)