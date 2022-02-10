package com.ryanjames.jetpackmobileordering.repository

import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.db.AppDatabase
import com.ryanjames.jetpackmobileordering.db.model.BasicMenuWithCategories
import com.ryanjames.jetpackmobileordering.network.MobilePosApi
import com.ryanjames.jetpackmobileordering.network.networkBoundResource
import com.ryanjames.jetpackmobileordering.network.networkResource
import com.ryanjames.jetpackmobileordering.ui.toDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class MenuRepository(
    private val mobilePosApi: MobilePosApi,
    val roomDb: AppDatabase
) : AbsMenuRepository {


    override fun getBasicMenuByVenue(venueId: String): Flow<Resource<BasicMenuWithCategories?>> = networkBoundResource(
        fetchFromApi = { mobilePosApi.getBasicMenuByVenue(venueId = venueId) },
        queryDb = { roomDb.menuDao().getBasicMenuById(venueId) },
        mapToDomainModel = { it },
        savetoDb = { roomDb.menuDao().insertBasicMenu(basicMenuResponse = it, venueId = venueId) },
        shouldFetchFromApi = { it == null },
        onFetchFailed = { it.printStackTrace() }
    )


    override fun getProductById(id: String) = networkResource(
        fetchFromApi = { mobilePosApi.getProductDetails(id) },
        mapToDomainModel = { it.toDomain() }
    )


}