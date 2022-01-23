package com.ryanjames.jetpackmobileordering.features.productdetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.TAG
import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.core.StringResource
import com.ryanjames.jetpackmobileordering.domain.LineItem
import com.ryanjames.jetpackmobileordering.domain.Product
import com.ryanjames.jetpackmobileordering.repository.MenuRepository
import com.ryanjames.jetpackmobileordering.repository.OrderRepository
import com.ryanjames.jetpackmobileordering.toTwoDigitString
import com.ryanjames.jetpackmobileordering.ui.core.LoadingDialogState
import com.ryanjames.jetpackmobileordering.util.LineItemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val menuRepository: MenuRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _productDetailScreenState = MutableStateFlow(ProductDetailScreenState())
    val productDetailScreenState: StateFlow<ProductDetailScreenState>
        get() = _productDetailScreenState

    private val _onSuccessfulAddOrUpdate = MutableStateFlow(false)
    val onSuccessfulAddOrUpdate: StateFlow<Boolean>
        get() = _onSuccessfulAddOrUpdate

    private val rowDataHolders = mutableListOf<ProductDetailRowData>()
    private var selectedModifierSummaryId: String = ""
    private var venueId = ""

    private lateinit var lineItemManager: LineItemManager

    init {
        Log.d(TAG, "Product Detail init()")

        val productId = savedStateHandle.get<String>("productId")
        venueId = savedStateHandle.get<String>("venueId") ?: ""

        if (productId != null) {
            viewModelScope.launch {
                menuRepository.getProductById(productId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            setupLineItemManager(product = resource.data)
                        }
                        is Resource.Error -> {
                            resource.throwable.printStackTrace()
                        }
                        is Resource.Loading -> {
                            _productDetailScreenState.value = _productDetailScreenState.value.copy(loadingProductDetail = true)
                        }
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

    fun onClickAddToBag() {
        viewModelScope.launch {
            orderRepository.addOrUpdateLineItem(lineItemManager.getLineItem(), venueId).collect {
                if (it is Resource.Loading) {
                    _productDetailScreenState.value = _productDetailScreenState.value.copy(dialogState = LoadingDialogState(StringResource(R.string.adding_item_to_bag)))
                } else if (it is Resource.Success) {
                    _productDetailScreenState.value = _productDetailScreenState.value.copy(dialogState = null)
                    _onSuccessfulAddOrUpdate.value = true
                }

            }
        }
    }

    fun onClickModifierOption(parentId: String, id: String) {
        rowDataHolders.find { it.id == parentId }?.let { dataHolder ->
            if (dataHolder is ProductDetailRowData.RowSelectMealDataHolder) {
                lineItemManager.setProductBundle(id)
            } else if (dataHolder is ProductDetailRowData.RowProductGroupDataHolder) {
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
            } else if (dataHolder is ProductDetailRowData.RowProductGroupModifierDataHolder) {
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
                    isProductGroupHeader = it is ProductDetailRowData.RowProductGroupHeaderDataHolder,
                    hideLineSeparator = it.hideLineSeparator
                )
            }
        val modifierOptions = rowDataHolders.find { it.id == selectedModifierSummaryId }?.options ?: listOf()

        _productDetailScreenState.value =
            _productDetailScreenState.value.copy(
                product = product,
                modifierSummaryRows = list,
                modifierOptions = modifierOptions,
                quantity = lineItem.quantity.toString(),
                price = "$${lineItem.price.toTwoDigitString()}",
                loadingProductDetail = false
            )
    }

    private fun createRowDataHolders(lineItem: LineItem): List<ProductDetailRowData> {
        val list = mutableListOf<ProductDetailRowData>()

        lineItem.product.let { product ->

            if (lineItem.product.bundles.isNotEmpty()) {
                list.add(ProductDetailRowData.RowSelectMealDataHolder(lineItem = lineItem))
            }

            for (modifierGroup in product.modifierGroups) {
                list.add(ProductDetailRowData.RowProductGroupModifierDataHolder(product, modifierGroup, lineItem))
            }

            lineItem.bundle?.productGroups?.forEach { productGroup ->

                // Hide line separator for the row before the product group header
                val lastItemIndex = list.size - 1
                if (lastItemIndex >= 0) {
                    list[lastItemIndex].hideLineSeparator = true
                }

                list.add(ProductDetailRowData.RowProductGroupHeaderDataHolder(productGroup))
                list.add(ProductDetailRowData.RowProductGroupDataHolder(productGroup, lineItem))

                val productSelection = lineItem.productsInBundle[productGroup]
                productSelection?.forEach {
                    it.modifierGroups.forEach { modifierGroup ->
                        list.add(ProductDetailRowData.RowProductGroupModifierDataHolder(it, modifierGroup, lineItem))
                    }
                }
            }
        }

        return list
    }

    override fun onCleared() {
        Log.d(TAG, "Product Detail onCleared()")
    }
}