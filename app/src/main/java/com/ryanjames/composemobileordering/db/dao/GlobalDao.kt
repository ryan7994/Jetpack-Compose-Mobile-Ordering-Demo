package com.ryanjames.composemobileordering.db.dao

import androidx.room.*
import com.ryanjames.composemobileordering.db.model.GlobalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GlobalDao {

    @Query("SELECT * FROM GlobalEntity WHERE id = 0")
    suspend fun getGlobalValues(): GlobalEntity?

    @Query("SELECT * FROM GlobalEntity WHERE id = 0")
    fun getGlobalValuesFlow(): Flow<GlobalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlobalEntity(globalEntity: GlobalEntity)

    @Query("UPDATE GlobalEntity SET currentOrderId = :id WHERE id = 0")
    suspend fun updateCurrentOrderId(id: String)

    @Query("UPDATE GlobalEntity SET currentOrderId = null, currentVenue = null")
    suspend fun clearCurrentOrder()

    @Query("UPDATE GlobalEntity SET currentVenue = :id WHERE id = 0")
    suspend fun updateCurrentVenueId(id: String)

    @Query("UPDATE GlobalEntity SET deliveryAddress = :deliveryAddress WHERE id = 0")
    suspend fun updateDeliveryAddress(deliveryAddress: String?)

    @Transaction
    suspend fun createLocalBagOrderId(orderId: String, venueId: String) {
        val globalEntity = getGlobalValues()
        insertGlobalEntity(
            GlobalEntity(
                id = 0,
                currentOrderId = orderId,
                currentVenue = venueId,
                deliveryAddress = globalEntity?.deliveryAddress
            )
        )
    }
}