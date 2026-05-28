package com.example.momentary.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.momentary.data.local.database.MomentaryDatabase
import com.example.momentary.data.local.entity.RemoteKeysEntity
import com.example.momentary.data.local.entity.StoryEntity
import com.example.momentary.data.local.entity.toEntity
import com.example.momentary.data.remote.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: MomentaryDatabase,
    private val api: ApiService,
    private val tokenProvider: suspend () -> String
) : RemoteMediator<Int, StoryEntity>() {

    private val storyDao = database.storyDao()
    private val remoteKeysDao = database.remoteKeysDao()

    private companion object {
        const val INITIAL_PAGE = 1
        const val PAGE_SIZE = 10
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {

        return try {

            val page = when (loadType) {

                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )

                    prevKey
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )

                    nextKey
                }
            }

            val response = api.getStories(
                token = "Bearer ${tokenProvider()}",
                page = page,
                size = PAGE_SIZE,
                location = 0
            )

            val stories = response.listStory
            val endOfPaginationReached = stories.isEmpty()

            database.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    storyDao.clearStories()
                }

                val keys = stories.map { story ->
                    RemoteKeysEntity(
                        id = story.id,
                        prevKey = if (page == INITIAL_PAGE) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                }

                remoteKeysDao.insertAll(keys)
                storyDao.insertStories(stories.map { it.toEntity() })
            }

            MediatorResult.Success(endOfPaginationReached)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, StoryEntity>
    ): RemoteKeysEntity? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data
            ?.lastOrNull()
            ?.let { story ->
                remoteKeysDao.getRemoteKeyById(story.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, StoryEntity>
    ): RemoteKeysEntity? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data
            ?.firstOrNull()
            ?.let { story ->
                remoteKeysDao.getRemoteKeyById(story.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, StoryEntity>
    ): RemoteKeysEntity? {
        return state.anchorPosition
            ?.let { position ->
                state.closestItemToPosition(position)?.id
            }
            ?.let { id ->
                remoteKeysDao.getRemoteKeyById(id)
            }
    }
}
