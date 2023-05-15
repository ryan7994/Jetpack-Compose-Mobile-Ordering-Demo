@file:OptIn(ExperimentalFoundationApi::class)

package com.ryanjames.composemobileordering.features.venuemapfinder

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.ui.widget.FeaturedRestaurantCard
import com.ryanjames.composemobileordering.util.getBitmapDescriptor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Composable
fun VenueFinderScreen(venueFinderViewModel: VenueFinderViewModel) {
    VenueFinderLayout(
        venueFinderScreenState = venueFinderViewModel.venueFinderScreenState.collectAsState().value,
        onPagerSwipeChange = venueFinderViewModel::onPagerSwipeChange,
        onClickMarker = venueFinderViewModel::onClickMarker,
        onClickCard = venueFinderViewModel::onClickCard
    )
}

@Composable
private fun VenueFinderLayout(
    venueFinderScreenState: VenueFinderScreenState,
    onPagerSwipeChange: (page: Int) -> Unit,
    onClickMarker: (venueId: String) -> Unit,
    onClickCard: (venueId: String) -> Unit
) {

    val cameraPositionState = rememberCameraPositionState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = venueFinderScreenState.centerCamera, block = {
        scope.launch {
            cameraPositionState.animate(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(venueFinderScreenState.centerCamera, 13f)))
        }
    })

    Box(modifier = Modifier.fillMaxSize()) {
        val venues = venueFinderScreenState.venues
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false
            )
        ) {
            venues.forEach { venue ->
                val icon = if (venue.isSelected) R.drawable.marker else R.drawable.unselected_marker
                val dimension = if (venue.isSelected) 48.dp else 40.dp
                Marker(
                    icon = getBitmapDescriptor(LocalContext.current, icon, dimension, dimension),
                    state = MarkerState(venue.latLng),
                    title = null,
                    onClick = {
                        onClickMarker.invoke(venue.id)
                        false
                    },
                    zIndex = venue.zIndex
                )
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {

            val pagerState = rememberPagerState()

            LaunchedEffect(key1 = venueFinderScreenState.clickedMarkerIndex, block = {
                if (pagerState.canScrollForward || pagerState.canScrollBackward) {
                    pagerState.animateScrollToPage(venueFinderScreenState.clickedMarkerIndex)
                }
            })


            LaunchedEffect(pagerState) {
                // Collect from the pager state a snapshotFlow reading the targetPage
                snapshotFlow { pagerState.targetPage }.distinctUntilChanged().collectLatest {
                    onPagerSwipeChange.invoke(it)
                }
            }


            HorizontalPager(pageCount = venues.size, state = pagerState) { page ->
                Box {
                    FeaturedRestaurantCard(
                        featuredRestaurantCardState = venues[page].cardState,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                onClickCard.invoke(venues[page].id)
                            })
                }

            }

        }
    }

}