package com.ryanjames.jetpackmobileordering.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.ryanjames.jetpackmobileordering.core.LoginManager
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
}