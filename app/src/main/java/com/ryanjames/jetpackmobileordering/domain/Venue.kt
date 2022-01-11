package com.ryanjames.jetpackmobileordering.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Venue(
    val id: String,
    val name: String,
    val address: String?,
    val lat: Float,
    val long: Float,
    val rating: Float,
    val numberOfRatings: Int,
    val deliveryTimeInMinsLow: Int,
    val deliveryTimeInMinsHigh: Int,
    val priceIndicator: String,
    val categories: List<String>,
    val featuredImage: String?
) : Parcelable

val EmptyVenue = Venue("", "", null, 0f, 0f, 0f, 0, 0, 0, "", listOf(), null)