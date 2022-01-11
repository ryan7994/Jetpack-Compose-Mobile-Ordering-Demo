package com.ryanjames.jetpackmobileordering.features.venuedetail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.jetpackmobileordering.TAG
import com.ryanjames.jetpackmobileordering.domain.EmptyVenue
import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.repository.MenuRepository
import com.ryanjames.jetpackmobileordering.repository.VenueRepository
import com.ryanjames.jetpackmobileordering.ui.toCategoryViewStateList
import com.ryanjames.jetpackmobileordering.ui.toRestaurantHeaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VenueDetailViewModel @Inject constructor(
    private val venueRepository: VenueRepository,
    private val menuRepository: MenuRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _venueDetailScreenState = MutableStateFlow(VenueDetailScreenState(null, Resource.Loading))
    val venueDetailScreenState: StateFlow<VenueDetailScreenState>
        get() = _venueDetailScreenState

    init {

        Log.d(TAG, "Venue Detail Screen init()")

        val venueId = savedStateHandle.get<String>("venueId")
        val venueName = savedStateHandle.get<String>("venueName")

        val initialVenue = EmptyVenue.copy(name = venueName ?: "")
        _venueDetailScreenState.value = VenueDetailScreenState(initialVenue.toVenueDetailHeader(), Resource.Loading)

        if (venueId != null) {
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
                                    header = venue.toRestaurantHeaderState(),
                                    phoneUri = phoneUri,
                                    addressUri = addressUri,
                                    email = email
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
                            _venueDetailScreenState.value = _venueDetailScreenState.value.copy(menuCategoriesResource = Resource.Success(basicMenu?.toCategoryViewStateList() ?: listOf()))
                        } else if (resource is Resource.Error) {
                            resource.throwable.printStackTrace()
                            _venueDetailScreenState.value = _venueDetailScreenState.value.copy(menuCategoriesResource = Resource.Error(resource.throwable))
                        }


                    }
                })
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "Venue Detail Screen onCleared()")
    }
}