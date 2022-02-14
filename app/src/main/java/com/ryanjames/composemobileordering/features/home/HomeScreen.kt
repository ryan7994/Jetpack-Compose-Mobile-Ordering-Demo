package com.ryanjames.composemobileordering.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.features.home.*
import com.ryanjames.composemobileordering.ui.theme.*
import com.ryanjames.composemobileordering.ui.widget.*
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreenLayout(viewModel: HomeViewModel, onClickCard: (id: String) -> Unit = {}) {
    val state = viewModel.homeViewState.collectAsState()
    HomeScreenLayout(
        homeScreenState = state.value,
        onClickCard = onClickCard,
        onDeliveryAddressValueChange = viewModel::onDeliveryAddressInputChange,
        onClickSaveDeliveryAddress = viewModel::updateDeliveryAddress
    )
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun HomeScreenLayout(
    homeScreenState: HomeScreenState,
    onClickCard: (id: String) -> Unit = {},
    onClickSaveDeliveryAddress: () -> Unit,
    onDeliveryAddressValueChange: (String) -> Unit
) {

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetContent = {
            DeliveryAddressBottomSheetLayout(value = homeScreenState.deliveryAddressInput, onValueChange = onDeliveryAddressValueChange, onClickSave = {
                onClickSaveDeliveryAddress.invoke()
                scope.launch {
                    modalBottomSheetState.hide()
                }
            })
        },
        sheetState = modalBottomSheetState,
        scrimColor = Color.Transparent,
        sheetBackgroundColor = AppTheme.colors.bottomNavBackground,
        sheetShape = RoundedCornerShape(topEnd = 32.dp, topStart = 32.dp),
        sheetElevation = 8.dp,
    ) {
        HomeScreenContent(
            featuredList = homeScreenState.featuredList,
            restaurantList = homeScreenState.restaurantList,
            dataState = homeScreenState.dataState,
            onClickCard = onClickCard,
            deliveryAddress = homeScreenState.deliveryAddress,
            modalBottomSheetState = modalBottomSheetState
        )
    }

    BackHandler {
        if (modalBottomSheetState.isVisible) {
            scope.launch {
                modalBottomSheetState.hide()
            }
        }
    }

}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun HomeScreenContent(
    featuredList: List<FeaturedRestaurantCardState>,
    restaurantList: List<RestaurantCardState>,
    dataState: HomeScreenDataState,
    onClickCard: (id: String) -> Unit = {},
    deliveryAddress: String?,
    modalBottomSheetState: ModalBottomSheetState
) {
    val scope = rememberCoroutineScope()
    Surface(
        color = AppTheme.colors.materialColors.background,
        modifier = Modifier.fillMaxSize()
    ) {

        Crossfade(targetState = dataState, animationSpec = tween(durationMillis = 1000, easing = LinearEasing)) { dataState ->
            when (dataState) {
                HomeScreenDataState.Loading -> {
                    HomeScreenShimmer()
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
                                EditAddress(
                                    modifier = Modifier.padding(horizontal = 32.dp),
                                    address = deliveryAddress,
                                    onClick = {
                                        scope.launch {
                                            modalBottomSheetState.show()
                                        }
                                    }
                                )
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

                                TypeScaledTextView(
                                    label = stringResource(R.string.restaurants),
                                    modifier = Modifier.padding(16.dp),
                                    typeScale = TypeScaleCategory.H6
                                )
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

@Composable
fun HomeScreenShimmer() {

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

            Row {
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

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreenLayout(homeScreenState = HomeScreenState(
        listOf(featuredCard),
        listOf(venue1, venue2),
        HomeScreenDataState.Success,
        ""
    ), {}, {}, {})
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Preview
@Composable
fun PreviewHomeScreenDarkMode() {
    MyComposeAppTheme(darkTheme = true) {
        HomeScreenLayout(homeScreenState = HomeScreenState(
            listOf(featuredCard),
            listOf(venue1, venue2),
            HomeScreenDataState.Success,
            ""
        ), {}, {}, {})
    }
}