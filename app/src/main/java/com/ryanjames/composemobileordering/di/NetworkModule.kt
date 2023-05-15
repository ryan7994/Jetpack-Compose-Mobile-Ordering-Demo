package com.ryanjames.composemobileordering.di

import android.content.SharedPreferences
import com.ryanjames.composemobileordering.BuildConfig
import com.ryanjames.composemobileordering.constants.NETWORK_READ_TIMEOUT_IN_SEC
import com.ryanjames.composemobileordering.constants.NETWORK_WRITE_TIMEOUT_IN_SEC
import com.ryanjames.composemobileordering.core.LoginManager
import com.ryanjames.composemobileordering.network.LoginService
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.network.authenticator.TokenAuthenticator
import com.ryanjames.composemobileordering.network.interceptors.AuthTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class NetworkModule {

    @Singleton
    @Provides
    open fun provideApiService(sharedPreferences: SharedPreferences, mobilePosApi: MobilePosApi): LoginService {
        return LoginService(sharedPreferences, mobilePosApi)
    }

    @Singleton
    @Provides
    open fun provideRetrofitBuilder(): Retrofit.Builder {
        val apiBaseUrl = "https://spring-boot-mobile-pos-production.up.railway.app/"
//        val apiBaseUrl = "http://192.168.254.132:3000/"
//        val apiBaseUrl = "http://10.0.2.2:5000/v1/auth/"
        return Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    open fun provideTokenAuthenticator(sharedPreferences: SharedPreferences, retrofit: Retrofit.Builder, loginManager: LoginManager): TokenAuthenticator {
        return TokenAuthenticator(sharedPreferences, retrofit, loginManager)
    }

    @Singleton
    @Provides
    open fun provideAuthTokenInterceptor(sharedPreferences: SharedPreferences): AuthTokenInterceptor {
        return AuthTokenInterceptor(sharedPreferences)
    }

    @Singleton
    @Provides
    open fun provideMobilePosApi(
        okHttpClientBuilder: OkHttpClient.Builder,
        authTokenInterceptor: AuthTokenInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        retrofitBuilder: Retrofit.Builder
    ): MobilePosApi {
        val httpClientBuilder = okHttpClientBuilder.connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(NETWORK_READ_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_WRITE_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
            .addInterceptor(authTokenInterceptor)
            .authenticator(tokenAuthenticator)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClientBuilder.addInterceptor(loggingInterceptor)
        }

        val client = httpClientBuilder.build()

        val retrofit = retrofitBuilder.client(client).build()
        return retrofit.create(MobilePosApi::class.java)
    }

    @Singleton
    @Provides
    open fun provideOkHttpBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
    }

}