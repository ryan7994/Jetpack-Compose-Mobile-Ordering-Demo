package com.ryanjames.composemobileordering.features.venuemapfinder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ryanjames.composemobileordering.TAG
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.domain.Venue
import com.ryanjames.composemobileordering.domain.getLatLng
import com.ryanjames.composemobileordering.repository.AbsVenueRepository
import com.ryanjames.composemobileordering.util.toFeaturedRestaurantCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VenueFinderViewModel @Inject constructor(
    private val venueRepository: AbsVenueRepository,
) : ViewModel() {

    private val _venueFinderScreenState = MutableStateFlow(VenueFinderScreenState())
    val venueFinderScreenState: StateFlow<VenueFinderScreenState>
        get() = _venueFinderScreenState.asStateFlow()

    private var venues: List<Venue> = listOf()
    private var selectedVenue: Venue? = null

    private val centerLatLng: LatLng
        get() = selectedVenue?.getLatLng() ?: LatLng(0.0, 0.0)

    private val venueMarkers: List<VenueMarker>
        get() = venues.map { VenueMarker(it.id, it.name, LatLng(it.lat.toDouble(), it.long.toDouble()), selectedVenue?.id == it.id, it.toFeaturedRestaurantCardState()) }

    init {
        getVenues()
    }

    private fun getVenues() {
        viewModelScope.launch {
            venueRepository.getAllVenues().collect { resource ->
                if (resource is Resource.Success) {
                    venues = resource.data
                    selectedVenue = venues.getOrNull(0)
                    updateMap()
                }
            }
        }
    }

    private fun updateMap(selectedIndex: Int = -1) {
        _venueFinderScreenState.value = _venueFinderScreenState.value.copy(venues = venueMarkers, centerCamera = centerLatLng, clickedMarkerIndex = selectedIndex)
    }

    fun onPagerSwipeChange(index: Int) {
        if (index == -1 ) return
        Log.d(TAG, "onPagerSwipeChange: $index")
        selectedVenue = venues.getOrNull(index)
        updateMap(index)
    }

    fun onClickMarker(venueId: String) {
        val index = venues.indexOfFirst { it.id == venueId }
        Log.d(TAG, "onClickMarker: $index")
        selectedVenue = venues.getOrNull(index) ?: selectedVenue
        updateMap(index)
    }

}