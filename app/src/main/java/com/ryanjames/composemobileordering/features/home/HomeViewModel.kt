package com.ryanjames.composemobileordering.features.home


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.composemobileordering.TAG
import com.ryanjames.composemobileordering.clearAndAddAll
import com.ryanjames.composemobileordering.domain.Venue
import com.ryanjames.composemobileordering.repository.AbsVenueRepository
import com.ryanjames.composemobileordering.ui.toFeaturedRestaurantCardState
import com.ryanjames.composemobileordering.ui.toRestaurantCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val venueRepository: AbsVenueRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _homeViewState =
        MutableStateFlow(HomeViewState(listOf(), listOf(), HomeScreenDataState.Loading))
    val homeViewState = _homeViewState.asStateFlow()

    private val featuredList = mutableListOf<Venue>()
    private val restaurantList = mutableListOf<Venue>()

    init {
        Log.d(TAG, "Home Screen init()")

        viewModelScope.launch {
            venueRepository.getFeaturedVenues().collect { resource ->
                resource.mapIfSuccess { pair ->
                    featuredList.clearAndAddAll(pair.first)
                    restaurantList.clearAndAddAll(pair.second)
                    _homeViewState.value = HomeViewState(
                        featuredList = featuredList.map { it.toFeaturedRestaurantCardState() },
                        restaurantList = restaurantList.map { it.toRestaurantCardState() },
                        dataState = HomeScreenDataState.Success
                    )
                }
            }
        }
    }

    override fun onCleared() {
        Log.d(TAG, "Home Screen onCleared()")
    }
}