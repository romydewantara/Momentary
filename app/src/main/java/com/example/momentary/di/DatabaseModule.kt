package com.example.momentary.di

import android.content.Context
import androidx.room.Room
import com.example.momentary.data.local.dao.RemoteKeysDao
import com.example.momentary.data.local.dao.StoryDao
import com.example.momentary.data.local.database.MomentaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MomentaryDatabase {
        return Room.databaseBuilder(
            context,
            MomentaryDatabase::class.java,
            "momentary.db"
        ).build()
    }

    @Provides
    fun provideStoryDao(
        database: MomentaryDatabase
    ): StoryDao {
        return database.storyDao()
    }

    @Provides
    fun provideRemoteKeysDao(
        database: MomentaryDatabase
    ): RemoteKeysDao {
        return database.remoteKeysDao()
    }
}
