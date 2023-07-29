package com.ryanjames.composemobileordering.repository

import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.core.flatMapLatest
import com.ryanjames.composemobileordering.db.AppDatabase
import com.ryanjames.composemobileordering.network.LoginService
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.network.model.request.EnrollRequest
import com.ryanjames.composemobileordering.network.model.response.LoginResponse
import com.ryanjames.composemobileordering.network.networkOnlyResourceFlow
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun enrollAndLogin(enrollRequest: EnrollRequest): Flow<Resource<LoginResponse>>
}

class AccountRepositoryImpl(
    private val mobilePosApi: MobilePosApi,
    private val roomDb: AppDatabase,
    private val loginService: LoginService
) : AccountRepository {


    override fun enrollAndLogin(enrollRequest: EnrollRequest): Flow<Resource<LoginResponse>> {
        return networkOnlyResourceFlow {
            mobilePosApi.enroll(enrollRequest)
        }.flatMapLatest { enrollResponse ->
            loginService.authenticate(enrollResponse.username, enrollRequest.password)
        }
    }

}