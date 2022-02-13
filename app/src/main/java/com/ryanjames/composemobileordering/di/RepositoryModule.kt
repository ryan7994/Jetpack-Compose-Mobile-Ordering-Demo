package com.ryanjames.composemobileordering.di

import com.ryanjames.composemobileordering.db.AppDatabase
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class RepositoryModule {

    @Singleton
    @Provides
    fun provideAbsMenuRepository(mobilePosApi: MobilePosApi, roomDb: AppDatabase): AbsMenuRepository {
        return MenuRepository(mobilePosApi = mobilePosApi, roomDb = roomDb)
    }

    @Provides
    fun provideAbsOrderRepository(mobilePosApi: MobilePosApi, roomDb: AppDatabase): AbsOrderRepository {
        return OrderRepository(mobilePosApi = mobilePosApi, roomDb = roomDb)
    }

    @Provides
    fun provideAbsVenueRepository(mobilePosApi: MobilePosApi, roomDb: AppDatabase): AbsVenueRepository {
        return VenueRepository(mobilePosApi = mobilePosApi, roomDb = roomDb)
    }
}