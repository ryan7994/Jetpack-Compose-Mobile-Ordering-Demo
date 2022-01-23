package com.ryanjames.jetpackmobileordering.db.model

import androidx.room.Embedded
import androidx.room.Relation

data class LineItemWithProducts(
    @Embedded
    val lineItem: LineItemEntity,
    @Relation(
        parentColumn = "lineItemId",
        entityColumn = "lineItemId",
        entity = LineItemProductEntity::class
    )
    val products: List<LineItemProductWithModifiers>
)

data class LineItemProductWithModifiers(
    @Embedded
    val product: LineItemProductEntity,
    @Relation(
        parentColumn = "productItemId",
        entityColumn = "productItemId",
        entity = LineItemModifierGroupEntity::class
    )
    val modifiers: List<LineItemModifierGroupWithModifiers>
)


data class LineItemModifierGroupWithModifiers(
    @Embedded
    val modifierGroup: LineItemModifierGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "modifierGroupId"
    )
    val modifierIds: List<LineItemModifierInfoEntity>,
)


