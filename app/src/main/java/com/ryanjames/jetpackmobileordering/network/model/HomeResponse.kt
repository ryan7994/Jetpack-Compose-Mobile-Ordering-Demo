package com.ryanjames.jetpackmobileordering.network.model

data class HomeResponse(
    val featuredStores: List<VenueResponse>,
    val restaurants: List<VenueResponse>
)