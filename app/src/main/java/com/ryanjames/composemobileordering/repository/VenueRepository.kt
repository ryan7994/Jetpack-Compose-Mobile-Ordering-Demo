package com.ryanjames.composemobileordering.repository


import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.db.AppDatabase
import com.ryanjames.composemobileordering.db.model.VenueDbModel
import com.ryanjames.composemobileordering.db.model.VenueEntityType
import com.ryanjames.composemobileordering.domain.Venue
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.network.networkBoundResource
import com.ryanjames.composemobileordering.util.toDomain
import com.ryanjames.composemobileordering.util.toEntity
import com.ryanjames.composemobileordering.util.toStoreHoursEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class VenueRepository(
    private val mobilePosApi: MobilePosApi,
    private val roomDb: AppDatabase
) : AbsVenueRepository {

    override fun getFeaturedVenues() = networkBoundResource(
        fetchFromApi = { mobilePosApi.getFeaturedVenues() },
        queryDb = { roomDb.venueDao().getHomeVenues() },
        savetoDb = { homeResponse ->
            roomDb.venueDao().insertVenues(homeResponse.toEntity())
            homeResponse.featuredStores.map { venueResponse ->
                roomDb.venueDao().insertStoreHoursEntity(*(venueResponse.toStoreHoursEntity().toTypedArray()))
            }

            homeResponse.restaurants.map { venueResponse ->
                roomDb.venueDao().insertStoreHoursEntity(*(venueResponse.toStoreHoursEntity().toTypedArray()))
            }
        },
        shouldFetchFromApi = { databaseModel -> databaseModel.isEmpty() },
        onFetchFailed = { },
        mapToDomainModel = { dbList ->
            val featuredList = dbList.filter { it.venue.type == VenueEntityType.HOME_FEATURED }.map { it.toDomain() }
            val restaurantList = dbList.filter { it.venue.type == VenueEntityType.HOME_RESTAURANT_LIST }.map { it.toDomain() }
            Pair(featuredList, restaurantList)
        }
    )

    override fun getVenueById(id: String) = networkBoundResource(
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
        savetoDb = {},
        shouldFetchFromApi = {
            it == null
        },
        onFetchFailed = { it.printStackTrace() },
        mapToDomainModel = { it?.toDomain() }
    )

    override suspend fun getCurrentVenueId() = roomDb.globalDao().getGlobalValues()?.currentVenue

    override fun getCurrentVenueIdFlow(): Flow<String?> {
        return roomDb.globalDao().getGlobalValuesFlow().map { it?.currentVenue }
    }

    override fun getAllVenues(): Flow<Resource<List<Venue>>> = networkBoundResource(
        fetchFromApi = { mobilePosApi.getFeaturedVenues() },
        queryDb = { roomDb.venueDao().getAllVenues() },
        savetoDb = {},
        shouldFetchFromApi = { databaseModel -> databaseModel.isEmpty() },
        onFetchFailed = { },
        mapToDomainModel = { dbList ->
            dbList.map { it.toDomain() }
        })
}