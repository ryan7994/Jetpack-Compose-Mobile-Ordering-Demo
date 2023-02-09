package com.ryanjames.composemobileordering.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CurrentOrderEntity")
data class CurrentOrderEntity(
    @PrimaryKey
    val id: Int = 0,
    val orderId: String,
    val subtotal: Float,
    val tax: Float,
    val total: Float,
    val storeId: String,
    val storeName: String
)

@Entity(tableName = "LineItemEntity")
data class LineItemEntity(
    @PrimaryKey
    val lineItemId: String,
    val productId: String,
    val bundleId: String?,
    val quantity: Int,
    val lineItemName: String,
    val price: Float,
    val currentOrderId: Int
)

@Entity(tableName = "LineItemProductEntity")
data class LineItemProductEntity(
    @PrimaryKey
    val productItemId: String,
    val productId: String,
    val productGroupId: String,
    val lineItemId: String,
    val productName: String
)

@Entity(tableName = "LineItemModifierGroupEntity")
data class LineItemModifierGroupEntity(
    @PrimaryKey
    val id: String,
    val modifierGroupId: String,
    val productItemId: String
)

@Entity(tableName = "LineItemModifierInfoEntity")
data class LineItemModifierInfoEntity(
    @PrimaryKey
    val id: String,
    val modifierId: String,
    val modifierGroupId: String,
    val modifierName: String
)

