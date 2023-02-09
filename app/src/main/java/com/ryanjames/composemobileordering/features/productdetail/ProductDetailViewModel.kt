package com.ryanjames.composemobileordering.features.productdetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.TAG
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.core.StringResource
import com.ryanjames.composemobileordering.domain.OrderSummaryLineItem
import com.ryanjames.composemobileordering.domain.LineItem
import com.ryanjames.composemobileordering.domain.Product
import com.ryanjames.composemobileordering.network.model.Event
import com.ryanjames.composemobileordering.repository.MenuRepository
import com.ryanjames.composemobileordering.repository.OrderRepository
import com.ryanjames.composemobileordering.repository.VenueRepository
import com.ryanjames.composemobileordering.toTwoDigitString
import com.ryanjames.composemobileordering.ui.core.AlertDialogState
import com.ryanjames.composemobileordering.ui.core.LoadingDialogState
import com.ryanjames.composemobileordering.ui.core.TwoButtonsDialogState
import com.ryanjames.composemobileordering.util.LineItemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val menuRepository: MenuRepository,
    private val orderRepository: OrderRepository,
    private val venueRepository: VenueRepository
) : ViewModel() {

    private var isModifying = false

    private val _productDetailScreenState = MutableStateFlow(ProductDetailScreenState())
    val productDetailScreenState = _productDetailScreenState.asStateFlow()

    private val _onSuccessfulAddOrUpdate = MutableStateFlow(Event(false))
    val onSuccessfulAddOrUpdate = _onSuccessfulAddOrUpdate.asStateFlow()

    private val _onLoadingFail = MutableStateFlow(Event(false))
    val onLoadingFail = _onLoadingFail.asStateFlow()

    private val rowDataHolders = mutableListOf<ProductDetailRowData>()
    private var selectedModifierSummaryId: String = ""
    private var venueId = ""
    private var lineItemId: String? = null

    private lateinit var lineItemManager: LineItemManager

    init {
        var orderSummaryLineItem: OrderSummaryLineItem?
        lineItemId = savedStateHandle.get<String>("lineItemId")
        isModifying = lineItemId != null

        val btnLabel = if (isModifying) R.string.update_item else R.string.add_to_bag
        val addOrUpdateSuccessMessage = if (isModifying) R.string.item_updated else R.string.item_added
        _productDetailScreenState.update {
            _productDetailScreenState.value.copy(
                btnLabel = StringResource(btnLabel),
                addOrUpdateSuccessMessage = StringResource(addOrUpdateSuccessMessage)
            )
        }

        viewModelScope.launch {

            orderSummaryLineItem = orderRepository.getLineItems().find { it.lineItemId == lineItemId }
            val productId = savedStateHandle.get<String>("productId") ?: orderSummaryLineItem?.productId
            venueId = savedStateHandle.get<String>("venueId") ?: venueRepository.getCurrentVenueId() ?: ""

            productId?.let { id ->
                menuRepository.getProductById(id).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            setupLineItemManager(product = resource.data, orderSummaryLineItem)
                        }
                        is Resource.Error -> {
                            _productDetailScreenState.update {
                                _productDetailScreenState.value.copy(
                                    dialogState = AlertDialogState(
                                        message = StringResource(id = R.string.generic_error_message),
                                        onDismiss = {
                                            dismissDialog()
                                            _onLoadingFail.update { Event(true) }
                                        }
                                    )
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _productDetailScreenState.value = _productDetailScreenState.value.copy(loadingProductDetail = true)
                        }
                    }
                }
            }
        }
    }

    private fun dismissDialog() {
        _productDetailScreenState.update { _productDetailScreenState.value.copy(dialogState = null) }
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

            val currentOrderVenueId = venueRepository.getCurrentVenueId()
            if (currentOrderVenueId != null && currentOrderVenueId != venueId) {
                val dialog = TwoButtonsDialogState(
                    title = StringResource(R.string.other_items_title),
                    message = StringResource(R.string.other_items_message),
                    positiveButton = StringResource(R.string.yes),
                    negativeButton = StringResource(R.string.no),
                    onClickNegativeBtn = { dismissDialog() },
                    onClickPositiveBtn = {
                        viewModelScope.launch {
                            orderRepository.clearBag()
                            addOrUpdateLineItem()
                        }
                    }
                )
                _productDetailScreenState.value = _productDetailScreenState.value.copy(dialogState = dialog)
            } else {
                addOrUpdateLineItem()
            }
        }
    }

    private suspend fun addOrUpdateLineItem() {
        orderRepository.addOrUpdateLineItem(lineItemManager.getLineItem(), venueId).collect {
            when (it) {
                is Resource.Loading -> {
                    val loadingDialogLabel = if (isModifying) R.string.updating_item else R.string.adding_item_to_bag
                    _productDetailScreenState.value = _productDetailScreenState.value.copy(dialogState = LoadingDialogState(StringResource(loadingDialogLabel)))
                }
                is Resource.Success -> {
                    _productDetailScreenState.value = _productDetailScreenState.value.copy(dialogState = null)
                    _onSuccessfulAddOrUpdate.value = Event(true)
                }
                is Resource.Error -> {
                    _productDetailScreenState.update {
                        _productDetailScreenState.value.copy(
                            dialogState = AlertDialogState(
                                message = StringResource(id = R.string.generic_error_message),
                                onDismiss = { dismissDialog() }
                            )
                        )
                    }
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

    private fun setupLineItemManager(product: Product, orderSummaryLineItem: OrderSummaryLineItem?) {
        lineItemManager = LineItemManager(product, listener = { lineItem ->
            updateData(product = product, lineItem)
        }, orderSummaryLineItem = orderSummaryLineItem)
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