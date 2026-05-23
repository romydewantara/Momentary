package com.example.momentary.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.momentary.R
import com.example.momentary.data.local.SessionPreferences
import com.example.momentary.data.local.database.MomentaryDatabase
import com.example.momentary.data.local.entity.toStory
import com.example.momentary.data.model.AuthResponse
import com.example.momentary.data.model.BaseResponse
import com.example.momentary.data.model.Story
import com.example.momentary.data.model.UserSession
import com.example.momentary.data.remote.ApiService
import com.example.momentary.data.remote.mediator.StoryRemoteMediator
import com.example.momentary.ui.common.ResultState
import com.example.momentary.ui.common.UiText
import com.example.momentary.ui.screen.home.model.StoryUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MomentaryRepository @Inject constructor(
    private val api: ApiService,
    private val prefs: SessionPreferences,
    private val database: MomentaryDatabase
) {

    suspend fun login(email: String, password: String): ResultState<AuthResponse> {
        return try {
            val response = api.login(email = email, password = password)
            response.loginResult?.let {
                prefs.saveSession(
                    token = it.token,
                    name = it.name,
                    email = email
                )
            }

            ResultState.Success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()

            val message = when {
                errorBody?.contains("must be a valid email", true) == true ->
                    UiText.StringResource(R.string.error_must_valid_email)

                errorBody?.contains("User not found", true) == true ->
                    UiText.StringResource(R.string.error_user_not_found)

                errorBody?.contains("Invalid password", true) == true ->
                    UiText.StringResource(R.string.error_invalid_password)

                else ->
                    UiText.StringResource(R.string.error_unknown)
            }

            ResultState.Error(message)
        } catch (e: Exception) {
            val errorMessage = e.message

            val message = when {
                errorMessage?.contains("timeout", true) == true ->
                    UiText.StringResource(R.string.error_timeout)

                else ->
                    UiText.StringResource(R.string.error_network)
            }
            ResultState.Error(message)
        }
    }

    suspend fun register(
        email: String,
        name: String,
        password: String
    ): ResultState<BaseResponse> {
        return try {
            val response = api.register(
                email = email,
                name = name,
                password = password
            )

            ResultState.Success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()

            val message = when {
                errorBody?.contains("Email is already taken", true) == true ->
                    UiText.StringResource(R.string.error_email_already_taken)

                errorBody?.contains("must be a valid email", true) == true ->
                    UiText.StringResource(R.string.error_must_valid_email)

                else ->
                    UiText.StringResource(R.string.error_unknown)
            }

            ResultState.Error(message)
        } catch (e: Exception) {
            val errorMessage = e.message

            val message = when {
                errorMessage?.contains("timeout", true) == true ->
                    UiText.StringResource(R.string.error_timeout)

                else ->
                    UiText.StringResource(R.string.error_network)
            }
            ResultState.Error(message)
        }
    }

    suspend fun getStories(
        page: Int? = null,
        size: Int? = null,
        location: Int? = null
    ): ResultState<List<Story>> {
        return try {
            val token = prefs.getToken().first() ?: ""
            val response = api.getStories(
                token = "Bearer $token",
                page = page,
                size = size,
                location = location
            )

            ResultState.Success(response.listStory)
        } catch (_: retrofit2.HttpException) {
            ResultState.Error(UiText.StringResource(R.string.error_unknown))
        } catch (e: Exception) {
            val errorMessage = e.message

            val message = when {
                errorMessage?.contains("timeout", true) == true ->
                    UiText.StringResource(R.string.error_timeout)

                else ->
                    UiText.StringResource(R.string.error_network)
            }
            ResultState.Error(message)
        }
    }

    suspend fun addStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        latBody: RequestBody?,
        lonBody: RequestBody?
    ): ResultState<BaseResponse> {
        return try {
            val token = prefs.getToken().first() ?: ""
            val response = api.addStory(
                token = "Bearer $token",
                photo = photo,
                description = description,
                lat = latBody,
                lon = lonBody
            )
            ResultState.Success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val unauthorizedKeywords = listOf("Unauthorized", "Missing authentication")
            val message = if (
                unauthorizedKeywords.any { errorBody?.contains(it, true) == true }
            ) {
                UiText.StringResource(R.string.error_unauthorized)
            } else {
                UiText.StringResource(R.string.error_unknown)
            }

            ResultState.Error(message)
        } catch (e: Exception) {
            val errorMessage = e.message

            val message = when {
                errorMessage?.contains("timeout", true) == true ->
                    UiText.StringResource(R.string.error_timeout)

                else ->
                    UiText.StringResource(R.string.error_network)
            }

            ResultState.Error(message)
        }
    }

    fun getSession(): Flow<UserSession> {
        return prefs.getSession()
    }

    suspend fun logout() {
        prefs.clear()
    }

    suspend fun isLoggedIn(): Boolean {
        return prefs.getToken().first() != null
    }

    fun isOnboardingCompleted(): Flow<Boolean> {
        return prefs.isOnboardingCompleted()
    }

    suspend fun setOnboardingCompleted() {
        prefs.setOnboardingCompleted()
    }

    suspend fun getStoriesWithLocation(): ResultState<List<Story>> {
        return try {

            val token = prefs.getToken().first() ?: ""

            val response = api.getStories(
                token = "Bearer $token",
                location = 1
            )

            ResultState.Success(response.listStory)

        } catch (_: Exception) {

            ResultState.Error(
                UiText.StringResource(R.string.error_network)
            )
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getPagedStories(
        sessionName: String?
    ): Flow<PagingData<StoryUiModel>> {

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                prefetchDistance = 5,
                enablePlaceholders = true
            ),
            remoteMediator = StoryRemoteMediator(
                database = database,
                api = api,
                tokenProvider = {
                    prefs.getToken().first().orEmpty()
                }
            ),
            pagingSourceFactory = {
                database.storyDao().getPagedStories()
            }
        ).flow.map { pagingData ->

            pagingData.map { entity ->

                val story = entity.toStory()

                val isOwnStory = story.name == sessionName
                val isVerified = isOwnStory

                StoryUiModel(
                    id = story.id,
                    story = story,
                    isOwnStory = isOwnStory,
                    isVerified = isVerified,
                    createdAt = story.createdAt
                )
            }
        }
    }
}
