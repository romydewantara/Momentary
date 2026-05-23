package com.example.momentary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.momentary.data.model.Story

@Entity(tableName = "stories")
data class StoryEntity(

    @PrimaryKey
    val id: String,

    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)

fun StoryEntity.toStory(): Story {
    return Story(
        id = id,
        name = name,
        description = description,
        photoUrl = photoUrl,
        createdAt = createdAt,
        lat = lat,
        lon = lon
    )
}
