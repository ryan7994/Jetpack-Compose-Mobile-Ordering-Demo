package com.ryanjames.composemobileordering.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.ryanjames.composemobileordering.core.LoginManager
import com.ryanjames.composemobileordering.core.SnackbarManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class ApplicationModule {

    @Singleton
    @Provides
    open fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("Preference File", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    open fun provideLoginManager(sharedPreferences: SharedPreferences): LoginManager {
        return LoginManager(sharedPreferences)
    }

    @Singleton
    @Provides
    open fun provideShowSnackbarUseCase(): SnackbarManager {
        return SnackbarManager()
    }
}