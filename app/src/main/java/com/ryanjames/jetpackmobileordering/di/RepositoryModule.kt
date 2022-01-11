package com.ryanjames.jetpackmobileordering.di

import com.ryanjames.jetpackmobileordering.db.AppDatabase
import com.ryanjames.jetpackmobileordering.network.MobilePosApi
import com.ryanjames.jetpackmobileordering.repository.MenuRepository
import com.ryanjames.jetpackmobileordering.repository.VenueRepository
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
}