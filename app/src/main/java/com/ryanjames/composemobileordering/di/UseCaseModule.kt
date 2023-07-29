package com.ryanjames.composemobileordering.di

import android.content.SharedPreferences
import com.ryanjames.composemobileordering.network.LoginService
import com.ryanjames.composemobileordering.usecase.GetLoginStateUseCase
import com.ryanjames.composemobileordering.usecase.LoginRequestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun provideGetLoginStateUseCase(sharedPreferences: SharedPreferences): GetLoginStateUseCase {
        return GetLoginStateUseCase(sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideLoginRequestUseCase(loginService: LoginService): LoginRequestUseCase {
        return LoginRequestUseCase(loginService)
    }
}
