package com.ryanjames.jetpackmobileordering.di

import com.ryanjames.jetpackmobileordering.db.AppDatabase
import com.ryanjames.jetpackmobileordering.network.MobilePosApi
import com.ryanjames.jetpackmobileordering.repository.*
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
    fun provideVenueRepository(mobilePosApi: MobilePosApi, roomDb: AppDatabase): VenueRepository {
        return VenueRepository(mobilePosApi = mobilePosApi, roomDb = roomDb)
    }

    @Singleton
    @Provides
    fun provideMenuRepository(mobilePosApi: MobilePosApi, roomDb: AppDatabase): MenuRepository {
        return MenuRepository(mobilePosApi = mobilePosApi, roomDb = roomDb)
    }

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

    @Singleton
    @Provides
    fun provideOrderRepository(mobilePosApi: MobilePosApi, roomDb: AppDatabase): OrderRepository {
        return OrderRepository(mobilePosApi = mobilePosApi, roomDb = roomDb)
    }
}