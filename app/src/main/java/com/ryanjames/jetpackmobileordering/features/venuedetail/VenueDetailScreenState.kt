package com.ryanjames.jetpackmobileordering.features.venuedetail

import android.net.Uri
import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.domain.Venue
import com.ryanjames.jetpackmobileordering.toTwoDigitString
import com.ryanjames.jetpackmobileordering.ui.widget.MenuItemCardDisplayModel
import com.ryanjames.jetpackmobileordering.ui.widget.RestaurantDisplayModel

data class VenueDetailScreenState(
    val header: RestaurantDisplayModel?,
    val menuCategoriesResource: Resource<List<CategoryViewState>>,
    val phoneUri: Uri? = null,
    val email: String? = null,
    val addressUri: Uri? = null,
    val venueId: String = ""
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