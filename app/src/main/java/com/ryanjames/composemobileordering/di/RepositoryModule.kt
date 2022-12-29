package com.ryanjames.composemobileordering.di

import com.ryanjames.composemobileordering.db.AppDatabase
import com.ryanjames.composemobileordering.network.LoginService
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class RepositoryModule {

    @Singleton
    @Provides
    fun provideAbsMenuRepository(mobilePosApi: MobilePosApi, roomDb: AppDatabase): MenuRepository {
        return MenuRepositoryImpl(mobilePosApi = mobilePosApi, roomDb = roomDb)
    }

    @Provides
    fun provideAbsOrderRepository(mobilePosApi: MobilePosApi, roomDb: AppDatabase): OrderRepository {
        return OrderRepositoryImpl(mobilePosApi = mobilePosApi, roomDb = roomDb)
    }

    @Provides
    fun provideAbsVenueRepository(mobilePosApi: MobilePosApi, roomDb: AppDatabase): VenueRepository {
        return VenueRepositoryImpl(mobilePosApi = mobilePosApi, roomDb = roomDb)
    }

    @Provides
    fun provideAccountRepository(mobilePosApi: MobilePosApi, roomDb: AppDatabase, loginService: LoginService): AccountRepository {
        return AccountRepositoryImpl(mobilePosApi = mobilePosApi, roomDb = roomDb, loginService)
    }
}