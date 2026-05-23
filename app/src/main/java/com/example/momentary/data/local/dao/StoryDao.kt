package com.example.momentary.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.momentary.data.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Query(
        """
        SELECT * FROM stories
        ORDER BY createdAt DESC
    """
    )
    fun getPagedStories(): PagingSource<Int, StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("DELETE FROM stories")
    suspend fun clearStories()
}
