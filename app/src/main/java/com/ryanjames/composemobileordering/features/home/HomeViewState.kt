package com.ryanjames.composemobileordering.features.home

data class HomeViewState(
    val featuredList: List<FeaturedRestaurantCardState>,
    val restaurantList: List<RestaurantCardState>,
    val dataState: HomeScreenDataState
)

sealed class HomeScreenDataState {
    object Loading : HomeScreenDataState()
    object Error : HomeScreenDataState()
    object Success : HomeScreenDataState()
}

data class RestaurantCardState(
    val venueId: String,
    val venueName: String,
    val venueCategories: String,
    val rating: String,
    val numberOfRatings: String,
    val imageUrl: String?
)

data class FeaturedRestaurantCardState(
    val venueId: String,
    val venueName: String,
    val venueCategories: String,
    val rating: String,
    val numberOfRatings: String,
    val priceLevel: String,
    val deliveryTime: String,
    val imageUrl: String?
)