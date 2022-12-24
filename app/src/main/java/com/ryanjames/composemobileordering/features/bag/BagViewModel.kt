package com.ryanjames.composemobileordering.features.bag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.core.StringResource
import com.ryanjames.composemobileordering.network.model.Event
import com.ryanjames.composemobileordering.repository.OrderRepository
import com.ryanjames.composemobileordering.repository.VenueRepository
import com.ryanjames.composemobileordering.toTwoDigitString
import com.ryanjames.composemobileordering.ui.core.LoadingDialogState
import com.ryanjames.composemobileordering.util.toDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class BagViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val venueRepository: VenueRepository
) : ViewModel() {

    private val _bagItemScreenState = MutableStateFlow(
        BagScreenState(
            bagItems = listOf(),
            venueId = null,
            venueName = null,
            btnRemoveState = ButtonState(enabled = true, visible = true),
            btnCancelState = ButtonState(enabled = true, visible = false),
            btnRemoveSelectedState = ButtonState(enabled = false, visible = false),
            isRemoving = false,
            alertDialog = null
        )
    )
    val bagScreenState: StateFlow<BagScreenState>
        get() = _bagItemScreenState.asStateFlow()

    private val _onItemRemoval = MutableStateFlow(Event(false))
    val onItemRemoval = _onItemRemoval.asStateFlow()

    init {

        viewModelScope.launch {
            awaitAll(
                async { collectCurrentVenue() },
                async { collectCurrentBagSummary() },
                async { getDeliveryAddress() },
            )

        }
    }


    private suspend fun getDeliveryAddress() {
        orderRepository.getDeliveryAddressFlow().collect { deliveryAddress ->
            _bagItemScreenState.update { it.copy(deliveryAddress = deliveryAddress) }
        }
    }

    private suspend fun collectCurrentVenue() {
        venueRepository.getCurrentVenue().collect {
            if (it is Resource.Success) {
                val venue = it.data ?: return@collect
                _bagItemScreenState.update { state ->
                    state.copy(
                        venueId = venue.id,
                        venueName = venue.name,
                        restaurantPosition = LatLng(venue.lat.toDouble(), venue.long.toDouble()),
                        venueAddress = venue.address ?: ""
                    )
                }
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
                        subtotal = "$${bagSummary.subtotal().toTwoDigitString()}",
                        total = "$${bagSummary.price.toTwoDigitString()}",
                        tax = "$${bagSummary.tax().toTwoDigitString()}",
                        isBagEmpty = false,
                        isLoading = false
                    )
                }
            } else {
                _bagItemScreenState.update { it.copy(isBagEmpty = true, isLoading = false) }
            }
        }
    }

    fun onClickRemove() {
        _bagItemScreenState.value = _bagItemScreenState.value.copy(
            btnRemoveState = ButtonState(enabled = true, visible = false),
            btnCancelState = ButtonState(enabled = true, visible = true),
            btnRemoveSelectedState = ButtonState(enabled = false, visible = true),
            isRemoving = true
        )
    }

    fun onClickRemoveSelected() {
        viewModelScope.launch {
            val venueId = venueRepository.getCurrentVenueId() ?: ""
            val lineItemsToRemove = _bagItemScreenState.value.bagItems.filter { it.forRemoval }.map { it.lineItemId }
            orderRepository.removeLineItems(lineItemsToRemove, venueId).collect {
                when (it) {
                    is Resource.Loading -> {
                        _bagItemScreenState.value = _bagItemScreenState.value.copy(
                            alertDialog = LoadingDialogState(StringResource(R.string.removing_from_bag))
                        )
                    }
                    is Resource.Success -> {
                        val newLineItems = it.data.lineItems.map { lineItem -> lineItem.toDisplayModel() }
                        _bagItemScreenState.value = _bagItemScreenState.value.copy(
                            bagItems = newLineItems,
                            btnRemoveState = ButtonState(true, true),
                            btnCancelState = ButtonState(true, false),
                            btnRemoveSelectedState = ButtonState(false, false),
                            alertDialog = null,
                            isRemoving = false
                        )
                        _onItemRemoval.value = Event(true)
                    }
                    is Resource.Error -> {
                        _bagItemScreenState.value = _bagItemScreenState.value.copy(
                            alertDialog = null
                        )
                    }
                    else -> {}
                }
            }
        }

    }

    fun onClickCancel() {
        val newBagItems = _bagItemScreenState.value.bagItems.map { it.copy(forRemoval = false) }
        _bagItemScreenState.value = _bagItemScreenState.value.copy(bagItems = newBagItems)

        _bagItemScreenState.value = _bagItemScreenState.value.copy(
            btnRemoveState = ButtonState(true, true),
            btnCancelState = ButtonState(true, false),
            btnRemoveSelectedState = ButtonState(false, false),
            isRemoving = false,
            bagItems = newBagItems
        )
    }

    fun onClickPickup() {
        _bagItemScreenState.value = _bagItemScreenState.value.copy(isPickupSelected = true)
    }

    fun onClickDelivery() {
        _bagItemScreenState.value = _bagItemScreenState.value.copy(isPickupSelected = false)
    }

    fun onRemoveCbCheckChanged(checked: Boolean, lineItemId: String) {
        val newBagItems = _bagItemScreenState.value.bagItems.map {
            if (it.lineItemId == lineItemId) {
                it.copy(forRemoval = checked)
            } else {
                it
            }
        }
        val itemsForRemoval = newBagItems.count { it.forRemoval }

        _bagItemScreenState.value = _bagItemScreenState.value.copy(
            bagItems = newBagItems,
            btnRemoveSelectedState = ButtonState(itemsForRemoval > 0, true)
        )
    }
}