package com.ryanjames.composemobileordering.features.venuedetail

import android.net.Uri
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.domain.Venue
import com.ryanjames.composemobileordering.toTwoDigitString
import com.ryanjames.composemobileordering.ui.widget.MenuItemCardDisplayModel
import com.ryanjames.composemobileordering.ui.widget.RestaurantDisplayModel
import com.ryanjames.composemobileordering.ui.widget.StoreInfoDisplayModel

data class VenueDetailScreenState(
    val header: RestaurantDisplayModel?,
    val menuCategoriesResource: Resource<List<CategoryViewState>>,
    val storeInfoDisplayModel: StoreInfoDisplayModel?,
    val phoneUri: Uri? = null,
    val email: String? = null,
    val addressUri: Uri? = null,
    val venueId: String = "",
    val isLoadingMenu: Boolean = true
)

data class CategoryViewState(
    val categoryName: String,
    val menuItems: List<MenuItemCardDisplayModel>
)


fun Venue.toVenueDetailHeader() = RestaurantDisplayModel(
    venueName = name,
    venueAddress = address ?: "",
    categories = categories,
    rating = rating.toTwoDigitString(),
    noOfReviews = String.format("(%d rating%s)", numberOfRatings, if (numberOfRatings < 2) "" else "s")
)