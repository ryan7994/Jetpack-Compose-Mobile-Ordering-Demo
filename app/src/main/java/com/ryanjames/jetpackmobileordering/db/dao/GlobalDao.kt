package com.ryanjames.jetpackmobileordering.db.dao

import androidx.room.*
import com.ryanjames.jetpackmobileordering.db.model.GlobalEntity
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

    @Transaction
    suspend fun createLocalBagOrderId(orderId: String, venueId: String) {
//        deleteGlobalEntity()
//        val globalEntity = getGlobalValues()
//        if (globalEntity == null) {
            insertGlobalEntity(
                GlobalEntity(
                    id = 0,
                    currentOrderId = orderId,
                    currentVenue = venueId
                )
            )
//        }
    }
}