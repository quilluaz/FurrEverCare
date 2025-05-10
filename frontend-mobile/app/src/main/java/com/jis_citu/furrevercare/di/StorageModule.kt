package com.jis_citu.furrevercare.di

import android.content.Context
import android.content.SharedPreferences
import com.jis_citu.furrevercare.data.SharedPreferencesTokenManager
import com.jis_citu.furrevercare.data.TokenManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    @Singleton
    abstract fun bindTokenManager(
        sharedPreferencesTokenManager: SharedPreferencesTokenManager
    ): TokenManager
}

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("furrevercare_auth_prefs", Context.MODE_PRIVATE)
    }
}