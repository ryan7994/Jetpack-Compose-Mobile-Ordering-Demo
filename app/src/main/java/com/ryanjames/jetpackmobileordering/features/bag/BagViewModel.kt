package com.ryanjames.jetpackmobileordering.features.bag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.core.StringResource
import com.ryanjames.jetpackmobileordering.network.model.Event
import com.ryanjames.jetpackmobileordering.repository.OrderRepository
import com.ryanjames.jetpackmobileordering.repository.VenueRepository
import com.ryanjames.jetpackmobileordering.ui.core.LoadingDialogState
import com.ryanjames.jetpackmobileordering.ui.toDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            btnRemoveState = ButtonState(true, true),
            btnCancelState = ButtonState(true, false),
            btnRemoveSelectedState = ButtonState(false, false),
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
            val venueId = venueRepository.getCurrentVenue() ?: ""
            venueRepository.getVenueById(venueId).collect {
                if (it is Resource.Success) {
                    _bagItemScreenState.value = _bagItemScreenState.value.copy(venueName = it.data?.name)
                }
            }
        }

        viewModelScope.launch {
            val venueId = venueRepository.getCurrentVenue() ?: ""
            orderRepository.getLineItemsFlow().collect { list ->
                val items = list.map { it.toDisplayModel() }
                _bagItemScreenState.value = _bagItemScreenState.value.copy(bagItems = items, venueId = venueId)
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
            val venueId = venueRepository.getCurrentVenue() ?: ""
            val lineItemsToRemove = _bagItemScreenState.value.bagItems.filter { it.forRemoval }.map { it.lineItemId }
            orderRepository.removeLineItems(lineItemsToRemove, venueId).collect {
                when {
                    it is Resource.Loading -> {
                        _bagItemScreenState.value = _bagItemScreenState.value.copy(
                            alertDialog = LoadingDialogState(StringResource(R.string.removing_from_bag))
                        )
                    }
                    it is Resource.Success -> {
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
                    it is Resource.Error -> {
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