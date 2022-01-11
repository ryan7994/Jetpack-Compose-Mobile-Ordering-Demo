package com.ryanjames.jetpackmobileordering.db.dao

import androidx.room.*
import com.ryanjames.jetpackmobileordering.db.VenueCategoryCrossRef
import com.ryanjames.jetpackmobileordering.db.VenueCategoryEntity
import com.ryanjames.jetpackmobileordering.db.VenueEntity
import com.ryanjames.jetpackmobileordering.db.VenueWithCategories
import com.ryanjames.jetpackmobileordering.db.model.BasicMenuCategoryEntity
import com.ryanjames.jetpackmobileordering.db.model.BasicMenuEntity
import com.ryanjames.jetpackmobileordering.db.model.BasicMenuProductEntity
import com.ryanjames.jetpackmobileordering.db.model.BasicMenuWithCategories
import com.ryanjames.jetpackmobileordering.network.model.BasicMenuResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {

    @Transaction
    @Query("SELECT  * FROM BasicMenuEntity WHERE venueId= :id")
    fun getBasicMenuById(id: String): Flow<BasicMenuWithCategories?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasicMenuEntity(vararg basicMenuEntity: BasicMenuEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasicMenuCategoryEntity(vararg basicMenuCategoryEntity: BasicMenuCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasicProductEntity(vararg basicMenuProductEntity: BasicMenuProductEntity)

    @Transaction
    suspend fun insertBasicMenu(basicMenuResponse: BasicMenuResponse, venueId: String) {
        insertBasicMenuEntity(BasicMenuEntity(venueId = venueId))
        basicMenuResponse.categories.forEach { basicCategoryResponse ->
            if (basicCategoryResponse.categoryId != null) {
                insertBasicMenuCategoryEntity(
                    BasicMenuCategoryEntity(
                        categoryId = basicCategoryResponse.categoryId,
                        categoryName = basicCategoryResponse.categoryName ?: "",
                        venueId = venueId
                    )
                )

                basicCategoryResponse.products?.forEach { basicProductResponse ->
                    if (basicProductResponse.productId != null) {
                        insertBasicProductEntity(
                            BasicMenuProductEntity(
                                productId = basicProductResponse.productId,
                                productName = basicProductResponse.productName ?: "",
                                imageUrl = basicProductResponse.imageUrl,
                                price = basicProductResponse.price ?: 0f,
                                categoryId = basicCategoryResponse.categoryId
                            )
                        )
                    }
                }

            }
        }
    }
}