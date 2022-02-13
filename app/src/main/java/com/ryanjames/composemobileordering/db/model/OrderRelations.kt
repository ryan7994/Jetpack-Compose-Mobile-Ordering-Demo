package com.ryanjames.composemobileordering.db.model

import androidx.room.Embedded
import androidx.room.Relation

data class CurrentOrderEntityWithLineItems(
    @Embedded
    val order: CurrentOrderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "currentOrderId",
        entity = LineItemEntity::class
    )
    val lineItems: List<LineItemEntityWithProducts>
)

data class LineItemEntityWithProducts(
    @Embedded
    val lineItem: LineItemEntity,
    @Relation(
        parentColumn = "lineItemId",
        entityColumn = "lineItemId",
        entity = LineItemProductEntity::class
    )
    val products: List<LineItemProductEntityWithModifiers>
)

data class LineItemProductEntityWithModifiers(
    @Embedded
    val product: LineItemProductEntity,
    @Relation(
        parentColumn = "productItemId",
        entityColumn = "productItemId",
        entity = LineItemModifierGroupEntity::class
    )
    val modifiers: List<LineItemModifierGroupEntityWithModifiers>
)


data class LineItemModifierGroupEntityWithModifiers(
    @Embedded
    val modifierGroup: LineItemModifierGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "modifierGroupId"
    )
    val modifierIds: List<LineItemModifierInfoEntity>
)


