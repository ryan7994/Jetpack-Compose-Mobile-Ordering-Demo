package com.ryanjames.composemobileordering.features.venuedetail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.composemobileordering.TAG
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.domain.EmptyVenue
import com.ryanjames.composemobileordering.features.bottomnav.BottomNavScreens
import com.ryanjames.composemobileordering.navigation.RouteNavigator
import com.ryanjames.composemobileordering.repository.MenuRepository
import com.ryanjames.composemobileordering.repository.VenueRepository
import com.ryanjames.composemobileordering.util.toCategoryViewStateList
import com.ryanjames.composemobileordering.util.toRestaurantDisplayModel
import com.ryanjames.composemobileordering.util.toStoreInfoDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VenueDetailViewModel @Inject constructor(
    private val venueRepository: VenueRepository,
    private val menuRepository: MenuRepository,
    private val routeNavigator: RouteNavigator,
    savedStateHandle: SavedStateHandle
) : ViewModel(), RouteNavigator by routeNavigator {

    private val _venueDetailScreenState = MutableStateFlow(VenueDetailScreenState(null, Resource.Loading, null))
    val venueDetailScreenState: StateFlow<VenueDetailScreenState>
        get() = _venueDetailScreenState

    init {

        Log.d(TAG, "Venue Detail Screen init()")

        val venueId = savedStateHandle.get<String>("venueId") ?: ""
        val venueName = savedStateHandle.get<String>("venueName")

        val initialVenue = EmptyVenue.copy(name = venueName ?: "")
        _venueDetailScreenState.value = VenueDetailScreenState(initialVenue.toVenueDetailHeader(), Resource.Loading, null, venueId = venueId)

        if (venueId.isNotBlank()) {
            viewModelScope.launch {
                awaitAll(async {
                    venueRepository.getVenueById(id = venueId).collect { resource ->
                        if (resource is Resource.Success) {
                            val venue = resource.data

                            if (venue != null) {
                                val phoneUri = Uri.parse("tel:5000000000")
                                val addressUri = Uri.parse("http://maps.google.com/maps?daddr=${venue.lat},${venue.long}")
                                val email = "sample@mailinator.com"

                                _venueDetailScreenState.value = _venueDetailScreenState.value.copy(
                                    header = venue.toRestaurantDisplayModel(),
                                    phoneUri = phoneUri,
                                    addressUri = addressUri,
                                    email = email,
                                    storeInfoDisplayModel = venue.toStoreInfoDisplayModel()
                                )
                            }
                        } else if (resource is Resource.Error) {
                            resource.throwable.printStackTrace()
                        }
                    }
                }, async {

                    menuRepository.getBasicMenuByVenue(venueId = venueId).collect { resource ->
                        if (resource is Resource.Success) {
                            val basicMenu = resource.data
                            _venueDetailScreenState.value =
                                _venueDetailScreenState.value.copy(menuCategoriesResource = Resource.Success(basicMenu?.toCategoryViewStateList() ?: listOf()))
                        } else if (resource is Resource.Error) {
                            _venueDetailScreenState.value = _venueDetailScreenState.value.copy(menuCategoriesResource = Resource.Error.Generic(resource.throwable))
                        } else if (resource is Resource.Loading) {
                            _venueDetailScreenState.value = _venueDetailScreenState.value.copy(menuCategoriesResource = Resource.Loading)
                        }
                    }
                })
            }
        }
    }

    fun onClickMenuItemCard(productId: String, venueId: String) {
        routeNavigator.navigateToRoute(BottomNavScreens.ProductDetailModal.routeWithArgs(productId, venueId))
    }

    fun onClickUp() {
        routeNavigator.popBackStack()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "Venue Detail Screen onCleared()")
    }
}