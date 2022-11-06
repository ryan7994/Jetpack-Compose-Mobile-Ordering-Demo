package com.ryanjames.composemobileordering.repository

import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.db.AppDatabase
import com.ryanjames.composemobileordering.db.model.BasicMenuWithCategories
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.network.networkBoundResource
import com.ryanjames.composemobileordering.network.networkResource
import com.ryanjames.composemobileordering.util.toDomain
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