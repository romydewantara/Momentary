package com.example.momentary.data.repository

import com.example.momentary.data.local.SessionPreferences
import com.example.momentary.data.model.AuthResponse
import com.example.momentary.data.model.BaseResponse
import com.example.momentary.data.model.StoryResponse
import com.example.momentary.data.remote.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MomentaryRepository @Inject constructor(
    private val api: ApiService,
    private val prefs: SessionPreferences
) {

    suspend fun login(email: String, password: String): AuthResponse {
        val response = api.login(email = email, password = password)
        response.loginResult?.token?.let {
            prefs.saveToken(it)
        }
        return response
    }

    suspend fun register(email: String, name: String, password: String): BaseResponse {
        return api.register(email = email, name = name, password = password)
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
