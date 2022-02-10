package com.ryanjames.jetpackmobileordering.features.bag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.core.StringResource
import com.ryanjames.jetpackmobileordering.network.model.Event
import com.ryanjames.jetpackmobileordering.repository.AbsOrderRepository
import com.ryanjames.jetpackmobileordering.repository.AbsVenueRepository
import com.ryanjames.jetpackmobileordering.toTwoDigitString
import com.ryanjames.jetpackmobileordering.ui.core.LoadingDialogState
import com.ryanjames.jetpackmobileordering.ui.toDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class BagViewModel @Inject constructor(
    private val orderRepository: AbsOrderRepository,
    private val venueRepository: AbsVenueRepository
) : ViewModel() {

    private val _bagItemScreenState = MutableStateFlow(
        BagScreenState(
            bagItems = listOf(),
            venueId = null,
            venueName = null,
            btnRemoveState = ButtonState(true, true),
            btnCancelState = ButtonState(true, false),
            btnRemoveSelectedState = ButtonState(false, false),
            isRemoving = false,
            alertDialog = null,
        )
    )
    val bagScreenState: StateFlow<BagScreenState>
        get() = _bagItemScreenState.asStateFlow()

    private val _onItemRemoval = MutableStateFlow(Event(false))
    val onItemRemoval = _onItemRemoval.asStateFlow()

    init {
        viewModelScope.launch {
            awaitAll(
                async { orderRepository.retrieveCurrentOrder().collect { } },
                async { collectCurrentVenueId() },
                async { collectCurrentBagSummary() },
                async { getDeliveryAddress() })

        }
    }

    fun updateDeliveryAddress() {
        viewModelScope.launch {
            orderRepository.updateDeliveryAddress(_bagItemScreenState.value.deliveryAddressInput)
        }
    }

    fun onDeliveryAddressInputChange(newValue: String) {
        _bagItemScreenState.value = _bagItemScreenState.value.copy(deliveryAddressInput = newValue)
    }

    private suspend fun getDeliveryAddress() {
        _bagItemScreenState.value = _bagItemScreenState.value.copy(deliveryAddressInput = orderRepository.getDeliveryAddressFlow().first() ?: "")
        orderRepository.getDeliveryAddressFlow().collect { deliveryAddress ->
            _bagItemScreenState.value = _bagItemScreenState.value.copy(deliveryAddress = deliveryAddress)
        }
    }

    private suspend fun collectCurrentVenueId() {
        venueRepository.getCurrentVenueIdFlow().filterNotNull().flatMapMerge { venueId ->
            venueRepository.getVenueById(venueId)
        }.collect {
            if (it is Resource.Success) {
                val venue = it.data ?: return@collect
                _bagItemScreenState.value = _bagItemScreenState.value.copy(
                    venueId = venue.id,
                    venueName = venue.name,
                    restaurantPosition = LatLng(venue.lat.toDouble(), venue.long.toDouble()),
                    venueAddress = venue.address ?: ""
                )
            }
        }
    }

    private suspend fun collectCurrentBagSummary() {
        orderRepository.getBagSummaryFlow().collect { bagSummary ->
            if (bagSummary != null) {
                val items = bagSummary.lineItems.map { it.toDisplayModel() }
                _bagItemScreenState.value = _bagItemScreenState.value.copy(
                    bagItems = items,
                    subtotal = "$${bagSummary.subtotal().toTwoDigitString()}",
                    total = "$${bagSummary.price.toTwoDigitString()}",
                    tax = "$${bagSummary.tax().toTwoDigitString()}"
                )
            }
        }
    }

    fun onClickRemove() {
        _bagItemScreenState.value = _bagItemScreenState.value.copy(
            btnRemoveState = ButtonState(true, false),
            btnCancelState = ButtonState(true, true),
            btnRemoveSelectedState = ButtonState(false, true),
            isRemoving = true
        )
    }

    @ExperimentalCoroutinesApi
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
                        val newLineItems = it.data.lineItems.map { it.toDisplayModel() }
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