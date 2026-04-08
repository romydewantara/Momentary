package com.example.momentary.data.repository

import com.example.momentary.R
import com.example.momentary.data.local.SessionPreferences
import com.example.momentary.data.model.AuthResponse
import com.example.momentary.data.model.BaseResponse
import com.example.momentary.data.model.Story
import com.example.momentary.data.model.UserSession
import com.example.momentary.data.remote.ApiService
import com.example.momentary.ui.common.ResultState
import com.example.momentary.ui.common.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MomentaryRepository @Inject constructor(
    private val api: ApiService,
    private val prefs: SessionPreferences
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
        } catch (_: Exception) {
            ResultState.Error(
                UiText.StringResource(R.string.error_network)
            )
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
        } catch (_: Exception) {
            ResultState.Error(
                UiText.StringResource(R.string.error_network)
            )
        }
    }

    suspend fun getStories(): ResultState<List<Story>> {
        return try {
            val token = prefs.getToken().first() ?: ""
            val response = api.getStories(token = "Bearer $token")

            ResultState.Success(response.listStory)
        } catch (_: Exception) {
            ResultState.Error(UiText.StringResource(R.string.error_network))
        }
    }

    suspend fun addStory(
        photo: MultipartBody.Part,
        description: RequestBody
    ): ResultState<BaseResponse> {
        return try {
            val token = prefs.getToken().first() ?: ""
            val response = api.addStory(
                token = "Bearer $token",
                photo = photo,
                description = description
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
                UiText.StringResource(R.string.error_unauthorized)
            }

            ResultState.Error(message)
        } catch (_: Exception) {
            ResultState.Error(UiText.StringResource(R.string.error_network))
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
}
