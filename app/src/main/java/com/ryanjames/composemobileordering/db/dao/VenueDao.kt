package com.ryanjames.composemobileordering.db.dao

import androidx.room.*
import com.ryanjames.composemobileordering.db.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VenueDao {

    @Transaction
    @Query("SELECT * FROM VenueEntity WHERE type= '${VenueEntityType.HOME_RESTAURANT_LIST}' OR type= '${VenueEntityType.HOME_FEATURED}'")
    fun getHomeVenues(): Flow<List<VenueWithCategories>>

    @Transaction
    @Query("SELECT * FROM VenueEntity")
    fun getAllVenues(): Flow<List<VenueWithCategories>>

    @Transaction
    @Query(
        "SELECT * From VenueEntity " +
                "JOIN StoreHoursEntity " +
                "ON VenueEntity.venueId = StoreHoursEntity.venueId"
    )
    fun getVenueAndStoreHours(): Flow<Map<VenueEntity, List<StoreHoursEntity>>>

    @Transaction
    @Query("SELECT  * FROM VenueEntity WHERE venueId= :id")
    fun getVenueById(id: String): Flow<VenueWithCategories?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenueEntity(vararg venues: VenueEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategoryEntity(vararg categories: VenueCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(join: VenueCategoryCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStoreHoursEntity(vararg storeHoursEntity: StoreHoursEntity)

    @Delete
    suspend fun delete(venue: VenueEntity)

    @Transaction
    suspend fun insertVenues(venues: List<Pair<VenueEntity, List<VenueCategoryEntity>>>) {
        venues.map {
            val venueEntity = it.first
            val categoryEntities = it.second
            insertVenueEntity(venueEntity)
            categoryEntities.map { categoryEntity ->
                insertCategoryEntity(categoryEntity)
                insert(VenueCategoryCrossRef(venueEntity.venueId, categoryEntity.categoryName))
            }
        }
    }

}