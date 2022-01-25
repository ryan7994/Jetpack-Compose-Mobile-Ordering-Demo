package com.ryanjames.jetpackmobileordering.db.dao

import androidx.room.*
import com.ryanjames.jetpackmobileordering.db.model.GlobalEntity

@Dao
interface GlobalDao {

    @Query("SELECT * FROM GlobalEntity WHERE id = 0")
    suspend fun getGlobalValues(): GlobalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalEntity(globalEntity: GlobalEntity)

    @Query("UPDATE GlobalEntity SET currentOrderId = :id")
    suspend fun updateCurrentOrderId(id: String)

    @Transaction
    suspend fun createLocalBagOrderId(orderId: String, venueId: String) {
        val globalEntity = getGlobalValues()
        if (globalEntity == null) {
            insertGlobalEntity(
                GlobalEntity(
                    id = 0,
                    currentOrderId = orderId,
                    currentVenue = venueId
                )
            )
        } else if (globalEntity.currentOrderId == null) {
            updateCurrentOrderId(orderId)
        }
    }

    @Transaction
    suspend fun getCurrentVenueId(): String? {
        val globalEntity = getGlobalValues()
        return globalEntity?.currentVenue
    }
}