package com.example.momentary.data.repository

import com.example.momentary.R
import com.example.momentary.data.local.SessionPreferences
import com.example.momentary.data.model.AuthResponse
import com.example.momentary.data.model.BaseResponse
import com.example.momentary.data.model.StoryResponse
import com.example.momentary.data.remote.ApiService
import com.example.momentary.ui.common.ResultState
import com.example.momentary.ui.common.UiText
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
            response.loginResult?.token?.let {
                prefs.saveToken(it)
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

                errorBody?.contains("at least 8 characters", true) == true ->
                    UiText.StringResource(R.string.error_password)

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

    suspend fun getStories(): StoryResponse {
        val token = prefs.getToken().first() ?: ""
        return api.getStories("Bearer $token")
    }

    suspend fun addStory(
        image: MultipartBody.Part,
        description: RequestBody
    ): BaseResponse {
        val token = prefs.getToken().first() ?: ""
        return api.addStory(token = token, image = image, description = description)
    }

    suspend fun logout() {
        prefs.clear()
    }

    suspend fun isLoggedIn(): Boolean {
        return prefs.getToken().first() != null
    }
}
