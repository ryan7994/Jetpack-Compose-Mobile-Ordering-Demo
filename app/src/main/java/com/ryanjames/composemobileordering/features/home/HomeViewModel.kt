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
import com.ryanjames.composemobileordering.util.toFeaturedRestaurantCardState
import com.ryanjames.composemobileordering.util.toRestaurantCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        orderRepository.getDeliveryAddressFlow().collect { deliveryAddress ->
            _homeViewState.update { it.copy(deliveryAddress = deliveryAddress) }
        }
    }

    override fun onCleared() {
        Log.d(TAG, "Home Screen onCleared()")
    }
}