package com.ryanjames.jetpackmobileordering.features.productdetail

import com.ryanjames.jetpackmobileordering.domain.Product

data class ProductDetailScreenState(
    val product: Product? = null,
    val modifierSummaryRows: List<ModifierSummaryRowDisplayModel> = listOf(),
    val modifierModalTitle: String = "",
    val modifierModalSubtitle: String = "",
    val modifierOptions: List<ModifierOptionDisplayModel> = listOf(),
    val quantity: String = "",
    val price: String = "",
)

data class ModifierSummaryRowDisplayModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val isProductGroupHeader: Boolean,
    val hideLineSeparator: Boolean = false,
)

data class ModifierOptionDisplayModel(
    val parentId: String,
    val id: String,
    val name: String,
    val selected: Boolean,
    val enabled: Boolean,
    val selectionType: ModifierOptionType
)

sealed class ModifierOptionType {
    object Single : ModifierOptionType()
    object Multi : ModifierOptionType()
}