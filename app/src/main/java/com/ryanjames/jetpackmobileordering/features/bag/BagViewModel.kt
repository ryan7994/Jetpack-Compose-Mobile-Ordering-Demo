package com.ryanjames.jetpackmobileordering.features.bag

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.jetpackmobileordering.TAG
import com.ryanjames.jetpackmobileordering.repository.OrderRepository
import com.ryanjames.jetpackmobileordering.repository.VenueRepository
import com.ryanjames.jetpackmobileordering.toTwoDigitString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BagViewModel @Inject constructor(
    orderRepository: OrderRepository,
    venueRepository: VenueRepository
) : ViewModel() {

    private val _bagItemScreenState = mutableStateOf(BagScreenState(listOf(), null))
    val bagScreenState: State<BagScreenState>
        get() = _bagItemScreenState

    init {
        Log.d(TAG, "Init")
        viewModelScope.launch {

            val venueId = venueRepository.getCurrentVenue() ?: ""

            orderRepository.getLineItemsFlow().collect { list ->
                val items = list.map {
                    BagItemRowState(
                        qty = it.quantity.toString(),
                        itemName = it.lineItemName,
                        itemModifier = it.modifiersDisplay,
                        price = "$" + it.price.toTwoDigitString()
                    )
                }
                _bagItemScreenState.value = _bagItemScreenState.value.copy(bagItems = items, venueId = venueId)
            }
        }

    }
}