package com.ryanjames.composemobileordering.repository

import android.util.Log
import com.ryanjames.composemobileordering.TAG
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.core.flatMapLatest
import com.ryanjames.composemobileordering.db.AppDatabase
import com.ryanjames.composemobileordering.network.LoginService
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.network.model.request.EnrollRequest
import com.ryanjames.composemobileordering.network.model.response.LoginResponse
import com.ryanjames.composemobileordering.network.networkOnlyResourceFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

interface AccountRepository {
    fun enrollAndLogin(enrollRequest: EnrollRequest): Flow<Resource<LoginResponse>>
}

class AccountRepositoryImpl(
    private val mobilePosApi: MobilePosApi,
    private val roomDb: AppDatabase,
    private val loginService: LoginService
) : AccountRepository {


    @OptIn(FlowPreview::class)
    override fun enrollAndLogin(enrollRequest: EnrollRequest): Flow<Resource<LoginResponse>> {
        return networkOnlyResourceFlow {
            mobilePosApi.enroll(enrollRequest)
        }.flatMapLatest { enrollResponse ->
            loginService.authenticate(enrollResponse.username, enrollRequest.password)
        }
    }

}