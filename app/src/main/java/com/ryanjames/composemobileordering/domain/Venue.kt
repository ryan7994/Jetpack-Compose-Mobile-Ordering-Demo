package com.ryanjames.composemobileordering.domain

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
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

fun Venue.getLatLng(): LatLng {
    return LatLng(lat.toDouble(), long.toDouble())
}