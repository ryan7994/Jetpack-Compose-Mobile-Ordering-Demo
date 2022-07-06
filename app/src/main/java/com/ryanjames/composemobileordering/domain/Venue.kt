package com.ryanjames.composemobileordering.domain

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.time.LocalTime

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
    val featuredImage: String?,
    val storeHours: List<StoreHours>
) : Parcelable

val EmptyVenue = Venue("", "", null, 0f, 0f, 0f, 0, 0, 0, "", listOf(), null, listOf())

fun Venue.getLatLng(): LatLng {
    return LatLng(lat.toDouble(), long.toDouble())
}

@Parcelize
data class StoreHours(
    val day: Day,
    val storeStatus: StoreStatus
) : Parcelable


sealed class StoreStatus : Parcelable {

    @Parcelize
    data class Open(
        val openingTime: LocalTime,
        val closingTime: LocalTime
    ) : StoreStatus()

    @Parcelize
    object Closed : StoreStatus()
}

enum class Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, UNKNOWN
}

fun getDay(code: String): Day {
    return when (code) {
        "MON" -> Day.MONDAY
        "TUE" -> Day.TUESDAY
        "WED" -> Day.WEDNESDAY
        "THU" -> Day.THURSDAY
        "FRI" -> Day.FRIDAY
        "SAT" -> Day.SATURDAY
        "SUN" -> Day.SUNDAY
        else -> Day.UNKNOWN
    }
}
