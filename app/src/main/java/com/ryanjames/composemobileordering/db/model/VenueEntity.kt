package com.ryanjames.composemobileordering.db.model

import androidx.room.*


@Entity(tableName = "VenueEntity")
data class VenueEntity(
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
    val type: String?,
    val creationTimeInMills: Long?
)

@Entity(tableName = "StoreHoursEntity")
data class StoreHoursEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val venueId: String,
    val isClosed: Boolean,
    val day: String,
    val openingTime: String?,
    val closingTime: String?
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

data class VenueDbModel(
    val venueEntity: VenueEntity,
    val categories: List<VenueCategoryEntity>,
    val storeHours: List<StoreHoursEntity>
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