package com.ryanjames.jetpackmobileordering.features.productdetail

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.jetpackmobileordering.TAG
import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.domain.*
import com.ryanjames.jetpackmobileordering.repository.MenuRepository
import com.ryanjames.jetpackmobileordering.toTwoDigitString
import com.ryanjames.jetpackmobileordering.util.LineItemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    menuRepository: MenuRepository
) : ViewModel() {

    private val _productDetailScreenState = MutableStateFlow(ProductDetailScreenState())
    val productDetailScreenState: StateFlow<ProductDetailScreenState>
        get() = _productDetailScreenState

    private val rowDataHolders = mutableListOf<RowDataHolder>()
    private var selectedModifierSummaryId: String = ""

    private lateinit var lineItemManager: LineItemManager

    init {
        Log.d(TAG, "Product Detail init()")

        val productId = savedStateHandle.get<String>("productId")

        if (productId != null) {
            viewModelScope.launch {
                menuRepository.getProductById(productId).collect { resource ->
                    if (resource is Resource.Success) {
                        setupLineItemManager(product = resource.data)
                    } else if (resource is Resource.Error) {
                        resource.throwable.printStackTrace()
                    }
                }
            }
        }
    }

    fun onClickPlusQty() {
        lineItemManager.incrementQuantity()
    }

    fun onClickMinusQty() {
        lineItemManager.decrementQuantity()
    }

    fun onClickModifierSummary(id: String) {
        selectedModifierSummaryId = id
        rowDataHolders.find { it.id == id }?.let {
            _productDetailScreenState.value = _productDetailScreenState.value.copy(
                modifierModalTitle = it.modalTitle,
                modifierModalSubtitle = it.modalSubtitle,
                modifierOptions = it.options
            )
        }
    }

    fun onClickModifierOption(parentId: String, id: String) {
        rowDataHolders.find { it.id == parentId }?.let { dataHolder ->
            if (dataHolder is RowDataHolder.RowSelectMealDataHolder) {
                lineItemManager.setProductBundle(id)
            } else if (dataHolder is RowDataHolder.RowProductGroupDataHolder) {
                if (dataHolder.isSingleSelection) {
                    lineItemManager.setProductSelectionsForProductGroupByIds(dataHolder.productGroup, listOf(id))
                } else {
                    val currentSelectedIds = lineItemManager.getSelectedIdsForProductGroup(dataHolder.productGroup)
                    if (currentSelectedIds.contains(id)) {
                        lineItemManager.setProductSelectionsForProductGroupByIds(dataHolder.productGroup, currentSelectedIds.minus(id))
                    } else {
                        lineItemManager.setProductSelectionsForProductGroupByIds(dataHolder.productGroup, currentSelectedIds.plus(id))
                    }
                }
            } else if (dataHolder is RowDataHolder.RowProductGroupModifierDataHolder) {
                if (dataHolder.isSingleSelection) {
                    lineItemManager.setProductModifiersByIds(dataHolder.product, dataHolder.modifierGroup, listOf(id))
                } else {
                    val currentSelectedIds = lineItemManager.getSelectedIdsForModifierGroup(dataHolder.product, dataHolder.modifierGroup)
                    if (currentSelectedIds.contains(id)) {
                        lineItemManager.setProductModifiersByIds(dataHolder.product, dataHolder.modifierGroup, currentSelectedIds.minus(id))
                    } else {
                        lineItemManager.setProductModifiersByIds(dataHolder.product, dataHolder.modifierGroup, currentSelectedIds.plus(id))
                    }
                }

            }
        }
    }

    private fun setupLineItemManager(product: Product) {
        lineItemManager = LineItemManager(product, listener = { lineItem ->
            updateData(product = product, lineItem)
        }, bagLineItem = null)
    }

    private fun updateData(product: Product, lineItem: LineItem) {
        rowDataHolders.clear()
        rowDataHolders.addAll(createRowDataHolders(lineItem))
        val list =
            rowDataHolders.map {
                ModifierSummaryRowDisplayModel(
                    id = it.id,
                    title = it.header,
                    subtitle = it.subHeader,
                    isProductGroupHeader = it is RowDataHolder.RowProductGroupHeaderDataHolder,
                    hideLineSeparator = it.hideLineSeparator
                )
            }
        val modifierOptions = rowDataHolders.find { it.id == selectedModifierSummaryId }?.options ?: listOf()

        _productDetailScreenState.value =
            _productDetailScreenState.value.copy(
                product = product,
                modifierSummaryRows = list,
                modifierOptions = modifierOptions,
                quantity =  lineItem.quantity.toString(),
                price = "$${lineItem.price.toTwoDigitString()}"
            )
    }

    private fun isLineItemManagerInitialized(): Boolean = this::lineItemManager.isInitialized

    private fun createRowDataHolders(lineItem: LineItem): List<RowDataHolder> {
        val list = mutableListOf<RowDataHolder>()

        lineItem.product.let { product ->

            if (lineItem.product.bundles.isNotEmpty()) {
                list.add(RowDataHolder.RowSelectMealDataHolder(lineItem = lineItem))
            }

            for (modifierGroup in product.modifierGroups) {
                list.add(RowDataHolder.RowProductGroupModifierDataHolder(product, modifierGroup, lineItem))
            }

            lineItem.bundle?.productGroups?.forEach { productGroup ->

                // Hide line separator for the row before the product group header
                val lastItemIndex = list.size - 1
                if (lastItemIndex >= 0) {
                    list[lastItemIndex].hideLineSeparator = true
                }

                list.add(RowDataHolder.RowProductGroupHeaderDataHolder(productGroup))
                list.add(RowDataHolder.RowProductGroupDataHolder(productGroup, lineItem))

                val productSelection = lineItem.productsInBundle[productGroup]
                productSelection?.forEach {
                    it.modifierGroups.forEach { modifierGroup ->
                        list.add(RowDataHolder.RowProductGroupModifierDataHolder(it, modifierGroup, lineItem))
                    }
                }
            }
        }

        return list
    }

    private sealed class RowDataHolder {

        abstract val header: String
        open val subHeader: String = ""
        abstract val id: String
        open val options: List<ModifierOptionDisplayModel> = listOf()
        open val isSingleSelection: Boolean = true
        open val modalTitle: String = ""
        open val modalSubtitle: String = ""
        var hideLineSeparator = false

        class RowSelectMealDataHolder(private val lineItem: LineItem) : RowDataHolder() {

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

        class RowProductGroupDataHolder(val productGroup: ProductGroup, private val lineItem: LineItem) : RowDataHolder() {

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

        class RowProductGroupModifierDataHolder(val product: Product, val modifierGroup: ModifierGroup, private val lineItem: LineItem) : RowDataHolder() {

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

        class RowProductGroupHeaderDataHolder(val productGroup: ProductGroup) : RowDataHolder() {

            override val id: String
                get() = "PGH${productGroup.productGroupId}"

            override val header: String
                get() = productGroup.productGroupName
        }
    }

    override fun onCleared() {
        Log.d(TAG, "Product Detail onCleared()")
    }
}