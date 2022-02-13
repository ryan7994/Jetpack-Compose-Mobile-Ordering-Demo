package com.ryanjames.composemobileordering.network

import android.content.SharedPreferences
import com.ryanjames.composemobileordering.constants.SharedPrefsKeys
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.network.model.LoginRequestBody
import com.ryanjames.composemobileordering.network.model.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApiService(
    private val sharedPrefs: SharedPreferences,
    private val mobilePosApi: MobilePosApi
) {

    suspend fun authenticate(username: String, password: String): Flow<Resource<LoginResponse>> {
        val loginRequestBody = LoginRequestBody(username, password)
        return flow {
            try {
                emit(Resource.Loading)
                val loginResponse = mobilePosApi.login(loginRequestBody)
                emit(Resource.Success(loginResponse))

                with(sharedPrefs.edit()) {
                    putString(SharedPrefsKeys.KEY_AUTH_TOKEN, loginResponse.accessToken)
                    putString(SharedPrefsKeys.KEY_REFRESH_TOKEN, loginResponse.refreshToken)
                    apply()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e))
            }
        }

    }

}