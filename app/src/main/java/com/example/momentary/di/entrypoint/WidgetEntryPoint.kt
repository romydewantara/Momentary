package com.example.momentary.di.entrypoint

import com.example.momentary.data.repository.MomentaryRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun repository(): MomentaryRepository
}
