package com.ryanjames.composemobileordering.repository


import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.db.AppDatabase
import com.ryanjames.composemobileordering.db.model.VenueDbModel
import com.ryanjames.composemobileordering.db.model.VenueEntityType
import com.ryanjames.composemobileordering.domain.Venue
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.network.networkBoundResourceFlow
import com.ryanjames.composemobileordering.util.toDomain
import com.ryanjames.composemobileordering.util.toEntity
import com.ryanjames.composemobileordering.util.toStoreHoursEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class VenueRepositoryImpl(
    private val mobilePosApi: MobilePosApi,
    private val roomDb: AppDatabase
) : VenueRepository {

    override fun getFeaturedVenues() = networkBoundResourceFlow(
        fetchFromApi = { mobilePosApi.getFeaturedVenues() },
        queryDb = { roomDb.venueDao().getHomeVenues() },
        saveToDb = { homeResponse ->
            roomDb.venueDao().insertVenues(homeResponse.toEntity())
            homeResponse.featuredStores.map { venueResponse ->
                roomDb.venueDao().insertStoreHoursEntity(*(venueResponse.toStoreHoursEntity().toTypedArray()))
            }

            homeResponse.restaurants.map { venueResponse ->
                roomDb.venueDao().insertStoreHoursEntity(*(venueResponse.toStoreHoursEntity().toTypedArray()))
            }
        },
        shouldFetchFromApi = { databaseModel ->
            val creationTime = databaseModel.getOrNull(0)?.venue?.creationTimeInMills
            if (creationTime != null) {
                databaseModel.isEmpty() || System.currentTimeMillis().minus(creationTime) > CACHE_IN_MILLS
            } else {
                true
            }
        },
        onFetchFailed = { },
        mapDbToDomainModel = { dbList ->
            val featuredList = dbList.filter { it.venue.type == VenueEntityType.HOME_FEATURED }.map { it.toDomain() }
            val restaurantList = dbList.filter { it.venue.type == VenueEntityType.HOME_RESTAURANT_LIST }.map { it.toDomain() }
            Pair(featuredList, restaurantList)
        }
    )

    override fun getVenueById(id: String) = networkBoundResourceFlow(
        fetchFromApi = { mobilePosApi.getVenueById(id) },
        queryDb = {
            val venueWithCategoriesFlow = roomDb.venueDao().getVenueById(id)
            val storeHoursFlow = roomDb.venueDao().getVenueAndStoreHours()

            venueWithCategoriesFlow.combine(storeHoursFlow) { venueWithCategories, storeHours ->

                if (venueWithCategories != null) {
                    val hours = storeHours[venueWithCategories.venue] ?: listOf()
                    VenueDbModel(venueWithCategories.venue, venueWithCategories.categories, hours)
                } else {
                    null
                }
            }

        },
        saveToDb = {},
        shouldFetchFromApi = {
            it == null
        },
        onFetchFailed = { it.printStackTrace() },
        mapDbToDomainModel = { it?.toDomain() }
    )

    override suspend fun getCurrentVenueId() = roomDb.globalDao().getGlobalValues()?.currentVenue

    private fun getCurrentVenueIdFlow(): Flow<String?> {
        return roomDb.globalDao().getGlobalValuesFlow().map { it?.currentVenue }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCurrentVenue(): Flow<Resource<Venue?>> {
        return getCurrentVenueIdFlow().filterNotNull().flatMapLatest { getVenueById(it) }
    }

    override fun getAllVenues(): Flow<Resource<List<Venue>>> = networkBoundResourceFlow(
        fetchFromApi = { mobilePosApi.getFeaturedVenues() },
        queryDb = { roomDb.venueDao().getAllVenues() },
        saveToDb = {},
        shouldFetchFromApi = { databaseModel -> databaseModel.isEmpty() },
        onFetchFailed = { },
        mapDbToDomainModel = { dbList ->
            dbList.map { it.toDomain() }
        })

    companion object {
        private const val CACHE_IN_MILLS = 30000
    }
}