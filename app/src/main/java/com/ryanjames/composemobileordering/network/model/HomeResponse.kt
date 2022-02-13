package com.ryanjames.composemobileordering.network.model

data class HomeResponse(
    val featuredStores: List<VenueResponse>,
    val restaurants: List<VenueResponse>
)