package com.example.momentary.data.local.entity

import com.example.momentary.data.model.Story

fun Story.toEntity(): StoryEntity {
    return StoryEntity(
        id = id,
        name = name,
        description = description,
        photoUrl = photoUrl,
        createdAt = createdAt,
        lat = lat,
        lon = lon
    )
}
