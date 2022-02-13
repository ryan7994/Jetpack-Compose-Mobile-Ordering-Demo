package com.ryanjames.composemobileordering.db

import androidx.room.*


@Entity(tableName = "VenueEntity")
data class VenueEntity (
    @PrimaryKey
    val venueId: String,
    val name: String,
    val address: String?,
    val lat: Float,
    val longitude: Float,
    val rating: Float,
    val numberOfRatings: Int,
    val deliveryTimeInMinsLow: Int,
    val deliveryTimeInMinsHigh: Int,
    val priceIndicator: String,
    val featuredImage: String?,
    val type: String?
)

object VenueEntityType {
    const val HOME_FEATURED = "home_featured"
    const val HOME_RESTAURANT_LIST = "home_restaurant_list"
}


@Entity
data class VenueCategoryEntity(
    @PrimaryKey
    val categoryName: String
)

@Entity(primaryKeys = ["venueId", "categoryName"])
data class VenueCategoryCrossRef(
    val venueId: String,
    val categoryName: String
)

data class VenueWithCategories(
    @Embedded val venue: VenueEntity,
    @Relation(
        parentColumn = "venueId",
        entityColumn = "categoryName",
        associateBy = Junction(VenueCategoryCrossRef::class)
    )
    val categories: List<VenueCategoryEntity>
)