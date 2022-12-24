package com.ryanjames.composemobileordering.network.model.response

data class HomeResponse(
    val featuredStores: List<VenueResponse>,
    val restaurants: List<VenueResponse>
)