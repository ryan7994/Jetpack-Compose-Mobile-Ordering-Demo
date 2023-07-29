package com.ryanjames.composemobileordering.network

import android.content.SharedPreferences
import android.util.Log
import com.ryanjames.composemobileordering.TAG
import com.ryanjames.composemobileordering.constants.ERROR_CODE_LOGIN_FAILURE
import com.ryanjames.composemobileordering.constants.SharedPrefsKeys
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.domain.AppError
import com.ryanjames.composemobileordering.network.model.request.LoginRequestBody
import com.ryanjames.composemobileordering.network.model.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LoginService(
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
                Log.e(TAG, e.message, e)
                emit(Resource.Error.Custom(e, error = AppError(userDefinedErrorCode = ERROR_CODE_LOGIN_FAILURE)))
            }
        }.flowOn(Dispatchers.IO)
    }

}