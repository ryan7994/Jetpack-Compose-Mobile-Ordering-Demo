package com.ryanjames.jetpackmobileordering.repository


import com.ryanjames.jetpackmobileordering.db.AppDatabase
import com.ryanjames.jetpackmobileordering.db.VenueEntity
import com.ryanjames.jetpackmobileordering.db.VenueEntityType
import com.ryanjames.jetpackmobileordering.network.MobilePosApi
import com.ryanjames.jetpackmobileordering.network.model.HomeResponse
import com.ryanjames.jetpackmobileordering.network.model.VenueResponse
import com.ryanjames.jetpackmobileordering.network.networkBoundResource

import com.ryanjames.jetpackmobileordering.network.networkResource
import com.ryanjames.jetpackmobileordering.ui.toDomain
import com.ryanjames.jetpackmobileordering.ui.toEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi

class VenueRepository(
    private val mobilePosApi: MobilePosApi,
    private val roomDb: AppDatabase
) {

    @ExperimentalCoroutinesApi
    fun getFeaturedVenues() = networkResource(
        fetchFromApi = { mobilePosApi.getFeaturedVenues() },
        onFetchFailed = { it.printStackTrace() },
        mapToDomainModel = { list -> list.toDomain() }
    )

//    @ExperimentalCoroutinesApi
//    fun getFeaturedVenues() = networkBoundResource(
//        fetchFromApi = { mobilePosApi.getFeaturedVenues() },
//        queryDb = { roomDb.venueDao().getHomeVenues() },
//        savetoDb = { homeResponse -> roomDb.venueDao().insertVenues(homeResponse.toEntity()) },
//        shouldFetchFromApi = { it.isEmpty() },
//        onFetchFailed = { it.printStackTrace() },
//        mapToDomainModel = { list ->
//            val featuredList = list.filter { it.venue.type == VenueEntityType.HOME_FEATURED }.map { it.toDomain() }
//            val restaurantList = list.filter { it.venue.type == VenueEntityType.HOME_RESTAURANT_LIST }.map { it.toDomain() }
//            Pair(featuredList, restaurantList)
//        }
//    )

    fun getVenueById(id: String) = networkBoundResource(
        fetchFromApi = { mobilePosApi.getVenueById(id) },
        queryDb = { roomDb.venueDao().getVenueById(id) },
        savetoDb = { roomDb.venueDao().insertVenues(listOf(it.toEntity(""))) },
        shouldFetchFromApi = { it == null },
        onFetchFailed = { it.printStackTrace() },
        mapToDomainModel = { it?.toDomain() }
    )

}