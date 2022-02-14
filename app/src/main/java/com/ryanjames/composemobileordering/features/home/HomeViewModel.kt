package com.ryanjames.composemobileordering.features.home


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.composemobileordering.TAG
import com.ryanjames.composemobileordering.clearAndAddAll
import com.ryanjames.composemobileordering.domain.Venue
import com.ryanjames.composemobileordering.repository.AbsOrderRepository
import com.ryanjames.composemobileordering.repository.AbsVenueRepository
import com.ryanjames.composemobileordering.ui.toFeaturedRestaurantCardState
import com.ryanjames.composemobileordering.ui.toRestaurantCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val venueRepository: AbsVenueRepository,
    private val orderRepository: AbsOrderRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _homeViewState =
        MutableStateFlow(HomeScreenState(listOf(), listOf(), HomeScreenDataState.Loading, ""))
    val homeViewState = _homeViewState.asStateFlow()

    private val featuredList = mutableListOf<Venue>()
    private val restaurantList = mutableListOf<Venue>()

    init {
        Log.d(TAG, "Home Screen init()")

        viewModelScope.launch {
            awaitAll(async { getVenues() },
                async { getDeliveryAddress() })

        }
    }

    private suspend fun getVenues() {
        venueRepository.getFeaturedVenues().collect { resource ->
            resource.mapIfSuccess { pair ->
                featuredList.clearAndAddAll(pair.first)
                restaurantList.clearAndAddAll(pair.second)
                _homeViewState.update {
                    _homeViewState.value.copy(
                        featuredList = featuredList.map { it.toFeaturedRestaurantCardState() },
                        restaurantList = restaurantList.map { it.toRestaurantCardState() },
                        dataState = HomeScreenDataState.Success
                    )
                }
            }
        }
    }

    private suspend fun getDeliveryAddress() {
        _homeViewState.update { it.copy(deliveryAddressInput = orderRepository.getDeliveryAddressFlow().first() ?: "") }
        orderRepository.getDeliveryAddressFlow().collect { deliveryAddress ->
            _homeViewState.update { it.copy(deliveryAddress = deliveryAddress, deliveryAddressInput = deliveryAddress ?: "") }
        }
    }

    fun onDeliveryAddressInputChange(newValue: String) {
        _homeViewState.value = _homeViewState.value.copy(deliveryAddressInput = newValue)
    }

    fun updateDeliveryAddress() {
        viewModelScope.launch {
            orderRepository.updateDeliveryAddress(_homeViewState.value.deliveryAddressInput)
        }
    }

    override fun onCleared() {
        Log.d(TAG, "Home Screen onCleared()")
    }
}