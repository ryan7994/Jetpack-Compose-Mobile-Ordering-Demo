package com.ryanjames.composemobileordering.features.venuemapfinder

import com.google.android.gms.maps.model.LatLng
import com.ryanjames.composemobileordering.features.home.FeaturedRestaurantCardState

data class VenueFinderScreenState(
    val venues: List<VenueMarker> = listOf(),
    val centerCamera: LatLng = LatLng(0.0, 0.0),
    val clickedMarkerIndex: Int = -1
)

data class VenueMarker(
    val id: String,
    val name: String,
    val latLng: LatLng,
    val isSelected: Boolean,
    val cardState: FeaturedRestaurantCardState
)