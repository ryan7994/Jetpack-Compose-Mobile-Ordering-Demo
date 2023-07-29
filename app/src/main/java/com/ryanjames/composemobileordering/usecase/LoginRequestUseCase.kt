package com.ryanjames.composemobileordering.usecase

import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.network.LoginService
import com.ryanjames.composemobileordering.network.model.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRequestUseCase @Inject constructor(
    private val loginService: LoginService,
) {

    suspend operator fun invoke(username: String, password: String): Flow<Resource<LoginResponse>> {
        return loginService.authenticate(username, password)
//        val loginRequestBody = LoginRequestBody(username, password)
//        return flow {
//            try {
//                emit(Resource.Loading)
//                val loginResponse = mobilePosApi.login(loginRequestBody)
//                emit(Resource.Success(loginResponse))
//
//                with(sharedPrefs.edit()) {
//                    putString(SharedPrefsKeys.KEY_AUTH_TOKEN, loginResponse.accessToken)
//                    putString(SharedPrefsKeys.KEY_REFRESH_TOKEN, loginResponse.refreshToken)
//                    apply()
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, e.message, e)
//                emit(Resource.Error.Custom(e, error = AppError(userDefinedErrorCode = ERROR_CODE_LOGIN_FAILURE)))
//            }
//        }.flowOn(Dispatchers.IO)
    }

}