package com.ryanjames.composemobileordering.network.authenticator

import android.content.SharedPreferences
import android.util.Log
import com.ryanjames.composemobileordering.constants.SharedPrefsKeys
import com.ryanjames.composemobileordering.core.LoginManager
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.network.interceptors.RefreshAuthTokenInterceptor
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit

class TokenAuthenticator(
    val sharedPreferences: SharedPreferences,
    val retrofit: Retrofit.Builder,
    val loginManager: LoginManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (response.code == 401 && response.request.header("No-Authentication") == null) {

            Log.d("401", response.body.toString())

            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(RefreshAuthTokenInterceptor(sharedPreferences))
                .apply {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(loggingInterceptor)
                }.build()

            val retrofit = retrofit.client(client).build()
            val tokenApiClient = retrofit.create<MobilePosApi>()

            var newBearerToken: String? = null
            runBlocking {
                try {
                    val refreshTokenResponse = tokenApiClient.refresh()
                    val newAccessToken = refreshTokenResponse.accessToken

                    if (newAccessToken != null) {
                        with(sharedPreferences.edit()) {
                            putString(SharedPrefsKeys.KEY_AUTH_TOKEN, newAccessToken)
                            apply()
                        }
                    }

                    newBearerToken = "Bearer $newAccessToken"

                } catch (e: Exception) {
                    if (e is HttpException && (e.code() == 401 || e.code() == 403)) {
                        loginManager.logOut()
                    }
                    e.printStackTrace()
                    null
                }

            }
            return newBearerToken?.let {
                response.request.newBuilder().header("Authorization", it).build()
            }
        }
        return null

    }
}