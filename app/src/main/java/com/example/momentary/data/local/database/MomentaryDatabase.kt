package com.example.momentary.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.momentary.data.local.dao.RemoteKeysDao
import com.example.momentary.data.local.dao.StoryDao
import com.example.momentary.data.local.entity.RemoteKeysEntity
import com.example.momentary.data.local.entity.StoryEntity

@Database(
    entities = [
        StoryEntity::class,
        RemoteKeysEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MomentaryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao

    abstract fun remoteKeysDao(): RemoteKeysDao
}
