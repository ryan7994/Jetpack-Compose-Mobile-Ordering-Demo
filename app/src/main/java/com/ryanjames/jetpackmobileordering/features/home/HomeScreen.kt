package com.ryanjames.jetpackmobileordering.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.features.home.FeaturedRestaurantCardState
import com.ryanjames.jetpackmobileordering.features.home.HomeScreenDataState
import com.ryanjames.jetpackmobileordering.features.home.HomeViewModel
import com.ryanjames.jetpackmobileordering.features.home.RestaurantCardState
import com.ryanjames.jetpackmobileordering.ui.theme.*
import com.ryanjames.jetpackmobileordering.ui.widget.EditAddress
import com.ryanjames.jetpackmobileordering.ui.widget.FeaturedCard
import com.ryanjames.jetpackmobileordering.ui.widget.ShimmerAnimation
import com.ryanjames.jetpackmobileordering.ui.widget.featuredCard


@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, onClickCard: (id: String) -> Unit = {}) {
    val state = viewModel.homeViewState.collectAsState()
    HomeScreen(
        featuredList = state.value.featuredList,
        restaurantList = state.value.restaurantList,
        dataState = state.value.dataState,
        onClickCard = onClickCard
    )
}

@ExperimentalPagerApi
@Composable
fun HomeScreen(
    featuredList: List<FeaturedRestaurantCardState>,
    restaurantList: List<RestaurantCardState>,
    dataState: HomeScreenDataState,
    onClickCard: (id: String) -> Unit = {}
) {

    Surface(
        color = AppTheme.colors.materialColors.background,
        modifier = Modifier.fillMaxSize()
    ) {

        Crossfade(targetState = dataState, animationSpec = tween(durationMillis = 1000, easing = LinearEasing)) { dataState ->
            when (dataState) {
                HomeScreenDataState.Loading -> {
                    ShimmerAnimation()
                }
                HomeScreenDataState.Error -> {

                }
                HomeScreenDataState.Success -> {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        Box {

                            Image(
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth(),
                                painter = painterResource(id = R.drawable.bg_semi_circle),
                                contentDescription = "",
                                contentScale = ContentScale.FillBounds
                            )

                            Column {
                                EditAddress(modifier = Modifier.padding(horizontal = 32.dp))
                                Spacer(modifier = Modifier.size(16.dp))

                                val pagerState = rememberPagerState(pageCount = featuredList.size)

                                HorizontalPager(pagerState) { page ->
                                    FeaturedCard(featuredRestaurantCardState = featuredList[page], onClickCard)
                                }

                                Spacer(modifier = Modifier.size(16.dp))

                                HorizontalPagerIndicator(
                                    pagerState,
                                    activeColor = CoralRed,
                                    inactiveColor = AppTheme.colors.lightTextColor,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(16.dp)
                                )

                                TypeScaledTextView(label = "Restaurants", modifier = Modifier.padding(16.dp), typeScale = TypeScaleCategory.H6)
                                LazyRow {
                                    items(restaurantList.size) { index ->
                                        if (index == 0) {
                                            Spacer(modifier = Modifier.size(16.dp))
                                        }
                                        RestaurantCard(restaurantList[index], onClickCard = onClickCard)
                                        Spacer(modifier = Modifier.size(if (index == 3) 16.dp else 8.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.size(64.dp))

                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalPagerApi
@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(listOf(featuredCard), listOf(venue1, venue2), HomeScreenDataState.Success)
}

@ExperimentalPagerApi
@Preview
@Composable
fun PreviewHomeScreenDarkMode() {
    MyComposeAppTheme(darkTheme = true) {
        HomeScreen(listOf(featuredCard), listOf(venue1, venue2), HomeScreenDataState.Success)
    }
}