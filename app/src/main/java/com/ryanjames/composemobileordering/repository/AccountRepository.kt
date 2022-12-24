package com.ryanjames.composemobileordering.repository

import com.google.gson.Gson
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.db.AppDatabase
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.network.model.request.EnrollRequest
import com.ryanjames.composemobileordering.network.model.response.ApiErrorResponse
import com.ryanjames.composemobileordering.network.model.response.EnrollResponse
import com.ryanjames.composemobileordering.network.networkOnlyResourceFlow
import com.ryanjames.composemobileordering.util.toDomain
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.HttpException

interface AccountRepository {
    fun enroll(enrollRequest: EnrollRequest): Flow<Resource<EnrollResponse>>
}

class AccountRepositoryImpl(
    private val mobilePosApi: MobilePosApi,
    private val roomDb: AppDatabase
) : AccountRepository {


    override fun enroll(enrollRequest: EnrollRequest) = networkOnlyResourceFlow(apiCall = { mobilePosApi.enroll(enrollRequest) })


}