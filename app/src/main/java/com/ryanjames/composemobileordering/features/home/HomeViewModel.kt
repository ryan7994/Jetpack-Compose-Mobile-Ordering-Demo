package com.ryanjames.composemobileordering.features.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.composemobileordering.collectResource
import com.ryanjames.composemobileordering.features.bottomnav.BottomNavScreens
import com.ryanjames.composemobileordering.navigation.RouteNavigator
import com.ryanjames.composemobileordering.repository.OrderRepository
import com.ryanjames.composemobileordering.repository.VenueRepository
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
    private val venueRepository: VenueRepository,
    private val orderRepository: OrderRepository,
    private val routeNavigator: RouteNavigator
) : ViewModel(), RouteNavigator by routeNavigator {

    private val _homeViewState = MutableStateFlow(HomeScreenState(listOf(), listOf(), HomeScreenDataState.Loading, ""))
    val homeViewState = _homeViewState.asStateFlow()

    init {
        viewModelScope.launch {
            awaitAll(async { getVenues() },
                async { getDeliveryAddress() })
        }
    }

    private suspend fun getVenues() {
        venueRepository.getFeaturedVenues().collectResource(
            onLoading = {},
            onSuccess = { (featuredVenues, restaurantList) ->
                _homeViewState.update { oldState -> oldState.copy(
                        featuredList = featuredVenues.map { it.toFeaturedRestaurantCardState() },
                        restaurantList = restaurantList.map { it.toRestaurantCardState() },
                        dataState = HomeScreenDataState.Success
                    )
                }
            },
            onError = {}
        )
    }

    private suspend fun getDeliveryAddress() {
        orderRepository.getDeliveryAddressFlow().collect { deliveryAddress ->
            _homeViewState.update { it.copy(deliveryAddress = deliveryAddress) }
        }
    }

    fun onClickCard(venueId: String) {
        routeNavigator.navigateToRoute(BottomNavScreens.VenueDetail.routeWithArgs(venueId))
    }
}