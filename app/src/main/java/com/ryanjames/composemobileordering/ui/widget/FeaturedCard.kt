package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.features.home.FeaturedRestaurantCardState
import com.ryanjames.composemobileordering.ui.theme.*
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun FeaturedCard(
    featuredRestaurantCardState: FeaturedRestaurantCardState,
    onClickCard: (String) -> Unit = {}
) {
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .clip(shape)
            .clickable {
                onClickCard.invoke(featuredRestaurantCardState.venueId)
            }
    ) {

        GlideImage(
            imageModel = featuredRestaurantCardState.imageUrl ?: "",
            modifier = Modifier
                .height(270.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(AppTheme.colors.placeholderColor),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )

        FeaturedRestaurantCard(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            featuredRestaurantCardState = featuredRestaurantCardState
        )
    }

}

@Composable
fun FeaturedRestaurantCard(
    featuredRestaurantCardState: FeaturedRestaurantCardState,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        modifier = modifier,
    ) {
        ConstraintLayout(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            val (btnHeart, tvName, tvSubtitle, tvPrice, divider, rating, deliveryTime) = createRefs()
            Image(
                painter = painterResource(id = R.drawable.heart_filled),
                contentDescription = "Favorited",
                modifier = Modifier
                    .size(20.dp)
                    .constrainAs(btnHeart) {
                        top.linkTo(parent.top)
                        bottom.linkTo(divider.bottom)
                    }
            )

            TypeScaledTextView(label = featuredRestaurantCardState.venueName,
                maxLines = 1,
                typeScale = TypeScaleCategory.H6,
                modifier = Modifier
                    .constrainAs(tvName) {
                        top.linkTo(parent.top)
                        start.linkTo(btnHeart.end, margin = 8.dp)
                        end.linkTo(tvPrice.start, margin = 8.dp)
                        width = Dimension.fillToConstraints
                    }
            )

            TypeScaledTextView(
                label = featuredRestaurantCardState.venueCategories,
                maxLines = 1,
                typeScale = TypeScaleCategory.Subtitle2,
                color = TextColor.LightTextColor,
                modifier = Modifier
                    .constrainAs(tvSubtitle) {
                        top.linkTo(tvName.bottom)
                        start.linkTo(tvName.start)
                        end.linkTo(tvPrice.start, margin = 8.dp)
                        width = Dimension.fillToConstraints
                    })


            TypeScaledTextView(
                label = featuredRestaurantCardState.priceLevel,
                typeScale = TypeScaleCategory.Subtitle2,
                modifier = Modifier.constrainAs(tvPrice) {
                    top.linkTo(tvName.top)
                    bottom.linkTo(tvName.bottom)
                    end.linkTo(parent.end)
                })

            Box(modifier = Modifier.constrainAs(divider) {
                top.linkTo(tvSubtitle.bottom, margin = 4.dp)
            }) {
                Divider(
                    color = AppTheme.colors.lightTextColor,
                    startIndent = 0.dp
                )
            }

            StarRatingWithReviews(
                modifier = Modifier.constrainAs(rating) {
                    top.linkTo(divider.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                },
                rating = featuredRestaurantCardState.rating,
                numberOfRatings = featuredRestaurantCardState.numberOfRatings
            )

            TypeScaledTextView(
                label = featuredRestaurantCardState.deliveryTime,
                typeScale = TypeScaleCategory.Subtitle2,
                modifier = Modifier.constrainAs(deliveryTime) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(rating.top)
                })

        }

    }
}

val featuredCard = FeaturedRestaurantCardState(
    "",
    "Sisig Hooray",
    "Filipino (Traditional)",
    "4.50",
    "(100)",
    "$$",
    "10-15 mins",
    ""
)

@Preview
@Composable
fun PreviewFeaturedCard() {
    Column {
        FeaturedCard(featuredCard)
        Spacer(modifier = Modifier.size(8.dp))
        MyComposeAppTheme(darkTheme = true) {
            Box(modifier = Modifier.background(AppTheme.colors.materialColors.background)) {
                FeaturedCard(featuredCard)
            }

        }
    }

}