package com.ryanjames.composemobileordering.features.bag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.collectResource
import com.ryanjames.composemobileordering.core.EVENT_SUCCESSFUL_ITEM_REMOVAL
import com.ryanjames.composemobileordering.core.SnackbarContent
import com.ryanjames.composemobileordering.core.SnackbarData
import com.ryanjames.composemobileordering.core.SnackbarManager
import com.ryanjames.composemobileordering.core.StringResource
import com.ryanjames.composemobileordering.core.getOrNull
import com.ryanjames.composemobileordering.features.bottomnav.BottomNavScreens
import com.ryanjames.composemobileordering.features.bottomnav.BottomNavTabs
import com.ryanjames.composemobileordering.navigation.RouteNavigator
import com.ryanjames.composemobileordering.repository.OrderRepository
import com.ryanjames.composemobileordering.repository.VenueRepository
import com.ryanjames.composemobileordering.toTwoDigitString
import com.ryanjames.composemobileordering.ui.core.DialogManager
import com.ryanjames.composemobileordering.ui.core.DismissibleDialogState
import com.ryanjames.composemobileordering.ui.core.LoadingDialogState
import com.ryanjames.composemobileordering.util.toDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BagViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val venueRepository: VenueRepository,
    private val snackbarManager: SnackbarManager,
    private val routeNavigator: RouteNavigator,
    private val dialogManager: DialogManager
) : ViewModel(), RouteNavigator by routeNavigator, DialogManager by dialogManager {

    private val _bagItemScreenState = MutableStateFlow(BagScreenState())
    val bagScreenState: StateFlow<BagScreenState>
        get() = _bagItemScreenState.asStateFlow()

    private val orderMode = MutableStateFlow(OrderModeId.PICKUP)

    init {
        viewModelScope.launch {
            awaitAll(
                async { collectCurrentVenue() },
                async { collectCurrentBagSummary() },
                async { getOrderMode() },
            )
        }
    }

    private suspend fun getOrderMode() {
        combine(orderRepository.getDeliveryAddressFlow(), orderMode, venueRepository.getCurrentVenue()) { deliveryAddress, orderModeId, venue ->
            when (orderModeId) {
                OrderModeId.DELIVERY -> OrderMode.Delivery(deliveryAddress = deliveryAddress)
                OrderModeId.PICKUP -> {
                    val storeVenue = venue.getOrNull()
                    val address = storeVenue?.address.orEmpty()
                    val restaurantPosition = if (storeVenue != null) {
                        LatLng(storeVenue.lat.toDouble(), storeVenue.long.toDouble())
                    } else {
                        LatLng(0.0, 0.0)
                    }
                    OrderMode.Pickup(venueAddress = address, restaurantPosition = restaurantPosition)
                }
            }
        }.collect { orderMode ->
            _bagItemScreenState.update { it.copy(orderMode = orderMode) }
        }
    }

    private suspend fun collectCurrentVenue() {
        venueRepository.getCurrentVenue().collect {
            val venue = it.getOrNull() ?: return@collect
            _bagItemScreenState.update { state ->
                state.copy(
                    venueId = venue.id,
                    venueName = venue.name
                )
            }
        }
    }

    private suspend fun collectCurrentBagSummary() {
        orderRepository.getBagSummaryFlow().collect { bagSummary ->
            if (bagSummary != null && bagSummary.lineItems.isNotEmpty()) {
                val items = bagSummary.lineItems.map { it.toDisplayModel() }
                _bagItemScreenState.update {
                    it.copy(
                        bagItems = items,
                        priceBreakdownState = PriceBreakdownState(
                            subtotal = "$${bagSummary.subtotal.toTwoDigitString()}",
                            total = "$${bagSummary.price.toTwoDigitString()}",
                            tax = "$${bagSummary.tax.toTwoDigitString()}",
                        ),
                        isLoading = false
                    )
                }
            } else {
                _bagItemScreenState.update { it.copy(isLoading = false, bagItems = listOf()) }
            }
        }
    }

    fun onClickRemove() {
        _bagItemScreenState.value = _bagItemScreenState.value.copy(
            bagListMode = BagListMode.REMOVING
        )
    }

    fun onClickRemoveSelected() {
        viewModelScope.launch {
            val venueId = venueRepository.getCurrentVenueId() ?: ""
            val lineItemsToRemove = _bagItemScreenState.value.bagItems.filter { it.forRemoval }.map { it.lineItemId }
            orderRepository.removeLineItems(lineItemsToRemove, venueId).collectResource(
                onLoading = {
                    dialogManager.showDialog(LoadingDialogState(StringResource(R.string.removing_from_bag)))
                },
                onSuccess = {
                    val newLineItems = it.lineItems.map { lineItem -> lineItem.toDisplayModel() }
                    dialogManager.hideDialog()
                    _bagItemScreenState.value = _bagItemScreenState.value.copy(
                        bagItems = newLineItems,
                        bagListMode = BagListMode.VIEWING
                    )
                    snackbarManager.showSnackbar(SnackbarData(EVENT_SUCCESSFUL_ITEM_REMOVAL, SnackbarContent(StringResource(R.string.item_removed))))
                },
                onError = {
                    dialogManager.showDialog(DismissibleDialogState(dialogMessage = StringResource(R.string.removing_from_bag_error)))
                }
            )
        }

    }

    fun onClickCancel() {
        val newBagItems = _bagItemScreenState.value.bagItems.map { it.copy(forRemoval = false) }
        _bagItemScreenState.value = _bagItemScreenState.value.copy(
            bagListMode = BagListMode.VIEWING,
            bagItems = newBagItems
        )
    }

    fun onDeliverOptionSelected(orderModeId: OrderModeId) {
        orderMode.update { orderModeId }
    }

    fun onClickCheckout() {
        val orderMode = _bagItemScreenState.value.orderMode
        if (orderMode is OrderMode.Delivery && orderMode.deliveryAddress.isNullOrEmpty()) {
            dialogManager.showDialog(
                DismissibleDialogState(dialogMessage = StringResource(R.string.missing_delivery_address_error))
            )
            return
        }

        viewModelScope.launch {

            orderRepository.checkoutOrder(orderMode).collectResource(
                onLoading = {
                    dialogManager.showDialog(LoadingDialogState(StringResource(R.string.placing_order)))
                },
                onSuccess = {
                    dialogManager.hideDialog()
                    orderRepository.clearBag()
                },
                onError = {
                    dialogManager.showDialog(
                        DismissibleDialogState(dialogMessage = StringResource(R.string.generic_error_message))
                    )
                }
            )
        }

    }

    fun onClickBrowseRestaurants() {
        routeNavigator.navigateToAnotherTab(BottomNavTabs.BrowseTab, BottomNavScreens.Home.route, BottomNavScreens.Home.route)
    }

    fun onClickLineItem(lineItemId: String) {
        routeNavigator.navigateToRoute(BottomNavScreens.ProductDetailFromBag.routeWithArgs(lineItemId = lineItemId))
    }

    fun onClickAddMoreItems() {
        bagScreenState.value.venueId?.let {
            routeNavigator.navigateToAnotherTab(
                BottomNavTabs.BrowseTab,
                BottomNavScreens.Home.route,
                BottomNavScreens.VenueDetail.routeWithArgs(it)
            )
        }
    }

    fun onRemoveCbCheckChanged(checked: Boolean, lineItemId: String) {
        val newBagItems = _bagItemScreenState.value.bagItems.map {
            if (it.lineItemId == lineItemId) {
                it.copy(forRemoval = checked)
            } else {
                it
            }
        }

        _bagItemScreenState.value = _bagItemScreenState.value.copy(
            bagItems = newBagItems
        )
    }
}