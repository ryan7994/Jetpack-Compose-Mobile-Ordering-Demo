package com.ryanjames.jetpackmobileordering.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LineItemEntity")
data class LineItemEntity(
    @PrimaryKey
    val lineItemId: String,
    val productId: String,
    val bundleId: String?,
    val quantity: Int,
)

@Entity(
    tableName = "LineItemProductEntity",
//    foreignKeys = [
//        ForeignKey(
//            entity = LineItemEntity::class,
//            parentColumns = ["lineItemId"],
//            childColumns = ["lineItemId"],
//            onDelete = CASCADE
//        )
//    ]
)
data class LineItemProductEntity(
    @PrimaryKey
    val productItemId: String,
    val productId: String,
    val productGroupId: String,
    val lineItemId: String
)

@Entity(
    tableName = "LineItemModifierGroupEntity",
//    foreignKeys = [
//        ForeignKey(
//            entity = LineItemProductEntity::class,
//            parentColumns = ["productItemId"],
//            childColumns = ["productItemId"],
//            onDelete = CASCADE,
//        )
//    ]
)
data class LineItemModifierGroupEntity(
    @PrimaryKey()
    val id: String,
    val modifierGroupId: String,
    val productItemId: String
)

@Entity(
    tableName = "LineItemModifierInfoEntity",
//    foreignKeys = [
//        ForeignKey(
//            entity = LineItemModifierGroupEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["modifierGroupId"],
//            onDelete = CASCADE
//        )
//    ]
)
data class LineItemModifierInfoEntity(
    @PrimaryKey
    val id: String,
    val modifierId: String,
    val modifierGroupId: String
)

