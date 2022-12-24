package com.ryanjames.composemobileordering.repository

import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.db.AppDatabase
import com.ryanjames.composemobileordering.db.model.BasicMenuWithCategories
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.network.networkBoundResourceFlow
import com.ryanjames.composemobileordering.network.networkAndDomainResourceFlow
import com.ryanjames.composemobileordering.util.toDomain
import kotlinx.coroutines.flow.Flow

class MenuRepositoryImpl(
    private val mobilePosApi: MobilePosApi,
    val roomDb: AppDatabase
) : MenuRepository {


    override fun getBasicMenuByVenue(venueId: String): Flow<Resource<BasicMenuWithCategories?>> = networkBoundResourceFlow(
        fetchFromApi = { mobilePosApi.getBasicMenuByVenue(venueId = venueId) },
        queryDb = { roomDb.menuDao().getBasicMenuById(venueId) },
        mapDbToDomainModel = { it },
        saveToDb = { roomDb.menuDao().insertBasicMenu(basicMenuResponse = it, venueId = venueId) },
        shouldFetchFromApi = { it == null },
        onFetchFailed = { it.printStackTrace() }
    )


    override fun getProductById(id: String) = networkAndDomainResourceFlow(
        fetchFromApi = { mobilePosApi.getProductDetails(id) },
        mapToDomainModel = { it.toDomain() }
    )


}