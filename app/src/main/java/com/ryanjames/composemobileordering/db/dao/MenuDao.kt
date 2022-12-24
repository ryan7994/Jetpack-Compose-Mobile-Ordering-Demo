package com.ryanjames.composemobileordering.db.dao

import androidx.room.*
import com.ryanjames.composemobileordering.db.model.BasicMenuCategoryEntity
import com.ryanjames.composemobileordering.db.model.BasicMenuEntity
import com.ryanjames.composemobileordering.db.model.BasicMenuProductEntity
import com.ryanjames.composemobileordering.db.model.BasicMenuWithCategories
import com.ryanjames.composemobileordering.network.model.response.BasicMenuResponse
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