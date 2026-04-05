package com.example.momentary.di

import com.example.momentary.data.local.SessionPreferences
import com.example.momentary.data.remote.ApiService
import com.example.momentary.data.repository.MomentaryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        api: ApiService,
        prefs: SessionPreferences
    ): MomentaryRepository {
        return MomentaryRepository(api, prefs)
    }
}
