package com.ryanjames.composemobileordering.features.productdetail

import com.ryanjames.composemobileordering.domain.*
import java.util.*

sealed class ProductDetailRowData {

    abstract val header: String
    open val subHeader: String = ""
    abstract val id: String
    open val options: List<ModifierOptionDisplayModel> = listOf()
    open val isSingleSelection: Boolean = true
    open val modalTitle: String = ""
    open val modalSubtitle: String = ""
    var hideLineSeparator = false

    class RowSelectMealDataHolder(private val lineItem: LineItem) : ProductDetailRowData() {

        override val id: String
            get() = "bundle"

        override val header: String
            get() = "Meal options"

        override val subHeader: String
            get() = if (lineItem.bundle == null) "Ala Carte" else lineItem.bundle.bundleName

        override val options: List<ModifierOptionDisplayModel>
            get() = lineItem.product.bundles.map {
                ModifierOptionDisplayModel(
                    parentId = id,
                    id = it.bundleId,
                    name = it.bundleName,
                    selected = lineItem.bundle?.bundleId == it.bundleId,
                    enabled = true,
                    selectionType = ModifierOptionType.Single
                )
            }.toMutableList().also {
                it.add(
                    0, ModifierOptionDisplayModel(
                        parentId = id,
                        id = "ALA_CARTE",
                        name = "Ala Carte",
                        selected = lineItem.bundle == null,
                        enabled = true,
                        selectionType = ModifierOptionType.Single
                    )
                )
            }

        override val modalTitle: String
            get() = "Select meal option"
    }

    class RowProductGroupDataHolder(val productGroup: ProductGroup, private val lineItem: LineItem) : ProductDetailRowData() {

        override val id: String
            get() = "PG${productGroup.productGroupId}"

        override val header: String
            get() = "Select"

        override val subHeader: String
            get() {
                val productList = lineItem.productsInBundle[productGroup] ?: listOf()
                return if (productList.isEmpty()) "None selected po" else productList.toText()
            }

        private fun isSelected(product: Product): Boolean {
            return lineItem.productsInBundle.get(productGroup)?.contains(product) ?: false
        }

        override val options: List<ModifierOptionDisplayModel>
            get() {
                return productGroup.options.map {
                    ModifierOptionDisplayModel(
                        parentId = id,
                        id = it.productId,
                        name = it.productName,
                        selected = isSelected(it),
                        enabled = isSingleSelection || isSelected(it) || lineItem.productsInBundle[productGroup]?.size ?: 0 < productGroup.max,
                        selectionType = if (isSingleSelection) ModifierOptionType.Single else ModifierOptionType.Multi
                    )
                }
            }

        override val modalSubtitle: String
            get() {
                return if (!isSingleSelection) {
                    "You can select up to ${productGroup.max} item/s."
                } else {
                    super.modalSubtitle
                }
            }

        override val isSingleSelection: Boolean
            get() = productGroup.min == 1 && productGroup.max == 1

        private fun List<Product>.toText(): String {
            var text = ""
            this.forEachIndexed { index, product ->
                text += product.productName
                if (index != this.size - 1) text += ", "
            }
            return text
        }

        override val modalTitle: String
            get() = "Select ${productGroup.productGroupName.lowercase(Locale.getDefault())}"
    }

    class RowProductGroupModifierDataHolder(val product: Product, val modifierGroup: ModifierGroup, private val lineItem: LineItem) : ProductDetailRowData() {

        override val id: String
            get() = "PGM${product.productId}${modifierGroup.modifierGroupId}"

        override val header: String
            get() = modifierGroup.modifierGroupName

        override val subHeader: String
            get() {
                val modifierList = lineItem.modifiers.get(ProductModifierGroupKey(product, modifierGroup)) ?: listOf()
                return if (modifierList.isEmpty()) "None selected" else modifierList.toText()
            }


        private fun isSelected(modifierInfo: ModifierInfo): Boolean {
            return lineItem.modifiers[ProductModifierGroupKey(product, modifierGroup)]?.contains(modifierInfo) ?: false
        }

        override val modalSubtitle: String
            get() {
                return if (!isSingleSelection) {
                    "You can select up to ${modifierGroup.max} item/s."
                } else {
                    super.modalSubtitle
                }
            }

        override val options: List<ModifierOptionDisplayModel>
            get() {
                return modifierGroup.options.map {
                    ModifierOptionDisplayModel(
                        parentId = id,
                        id = it.modifierId,
                        name = it.modifierName,
                        selected = isSelected(it),
                        enabled = isSelected(it) || lineItem.modifiers[ProductModifierGroupKey(product, modifierGroup)]?.size ?: 0 < modifierGroup.max,
                        selectionType = if (isSingleSelection) ModifierOptionType.Single else ModifierOptionType.Multi
                    )
                }
            }

        override val isSingleSelection: Boolean
            get() = modifierGroup.min == 1 && modifierGroup.max == 1


        private fun List<ModifierInfo>.toText(): String {
            var text = ""
            this.forEachIndexed { index, modifierInfo ->
                text += modifierInfo.modifierName
                if (index != this.size - 1) text += ", "
            }
            return text
        }

        override val modalTitle: String
            get() = "Select ${modifierGroup.modifierGroupName.lowercase(Locale.getDefault())}"
    }

    class RowProductGroupHeaderDataHolder(val productGroup: ProductGroup) : ProductDetailRowData() {

        override val id: String
            get() = "PGH${productGroup.productGroupId}"

        override val header: String
            get() = productGroup.productGroupName
    }
}