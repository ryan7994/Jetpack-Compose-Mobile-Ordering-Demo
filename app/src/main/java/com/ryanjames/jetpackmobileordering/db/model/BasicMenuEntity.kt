package com.ryanjames.jetpackmobileordering.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "BasicMenuEntity")
data class BasicMenuEntity (
    @PrimaryKey
    val venueId: String
)

@Entity(tableName = "BasicMenuCategoryEntity")
data class BasicMenuCategoryEntity(
    @PrimaryKey
    val categoryId: String,
    val categoryName: String,
    val venueId: String
)

@Entity(tableName = "BasicMenuProductEntity")
data class BasicMenuProductEntity(
    @PrimaryKey
    val productId: String,
    val productName: String,
    val price: Float,
    val imageUrl: String?,
    val categoryId: String
)

data class BasicCategoryWithProducts(
    @Embedded val category: BasicMenuCategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val products: List<BasicMenuProductEntity>
)

data class BasicMenuWithCategories(
    @Embedded val basicMenu: BasicMenuEntity,
    @Relation(
        entity = BasicMenuCategoryEntity::class,
        parentColumn = "venueId",
        entityColumn = "venueId"
    )
    val categories: List<BasicCategoryWithProducts>
)