package com.ryanjames.jetpackmobileordering.db.dao

import androidx.room.*
import com.ryanjames.jetpackmobileordering.db.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItemEntity(vararg lineItemEntity: LineItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItemProductEntity(vararg productEntity: LineItemProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItemModifierGroupEntity(vararg modifierGroupEntity: LineItemModifierGroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItemModifierInfoEntity(vararg modifierInfoEntity: LineItemModifierInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentOrderEntity(orderEntity: CurrentOrderEntity)

    // If function returns a Flow, the function should not be suspendable
    @Query("SELECT * FROM LineItemEntity")
    suspend fun getAllLineItems(): List<LineItemEntityWithProducts>

    @Query("SELECT * FROM CurrentOrderEntity WHERE id = 0")
    suspend fun getCurrentOrder(): CurrentOrderEntity

    @Query("SELECT * FROM CurrentOrderEntity WHERE id = 0")
    fun getCurrentOrderFlow(): Flow<CurrentOrderEntityWithLineItems?>


    @Query("DELETE FROM LineItemEntity")
    suspend fun deleteLineItems()

    @Query("DELETE FROM LineItemProductEntity")
    suspend fun deleteLineItemProducts()

    @Query("DELETE FROM LineItemModifierGroupEntity")
    suspend fun deleteModifierGroups()

    @Query("DELETE FROM LineItemModifierInfoEntity")
    suspend fun deleteModifierInfo()

    @Query("DELETE FROM CurrentOrderEntity")
    suspend fun deleteOrderEntity()


    @Transaction
    suspend fun clearLocalBag() {
        deleteOrderEntity()
        deleteLineItems()
        deleteLineItemProducts()
        deleteModifierGroups()
        deleteModifierInfo()
    }

    @Transaction
    suspend fun updateLocalBag(currentOrder: CurrentOrderEntityWithLineItems) {
        clearLocalBag()

        insertCurrentOrderEntity(currentOrder.order)
        currentOrder.lineItems.forEach { lineItemEntity ->
            insertLineItemEntity(lineItemEntity.lineItem)
            lineItemEntity.products.forEach { productEntity ->
                insertLineItemProductEntity(productEntity.product)
                productEntity.modifiers.forEach { modifierGroupEntity ->
                    insertLineItemModifierGroupEntity(modifierGroupEntity.modifierGroup)
                    for (modifierEntity in modifierGroupEntity.modifierIds) {
                        insertLineItemModifierInfoEntity(modifierEntity)
                    }
                }
            }
        }

    }

}