package com.example.momentary.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.momentary.data.model.UserSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("session")

class SessionPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val onboardingKey = booleanPreferencesKey("onboarding")
    private val tokenKey = stringPreferencesKey("token")
    private val nameKey = stringPreferencesKey("name")
    private val emailKey = stringPreferencesKey("email")

    fun isOnboardingCompleted(): Flow<Boolean> {
        return context.dataStore.data.map { it[onboardingKey] ?: false }
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { it[onboardingKey] = true }
    }

    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { it[tokenKey] }
    }

    suspend fun saveSession(
        token: String,
        name: String,
        email: String
    ) {
        context.dataStore.edit {
            it[tokenKey] = token
            it[nameKey] = name
            it[emailKey] = email
        }
    }

    fun getSession(): Flow<UserSession> {
        return context.dataStore.data.map {
            UserSession(
                token = it[tokenKey],
                name = it[nameKey],
                email = it[emailKey]
            )
        }
    }

    suspend fun clear() {
        context.dataStore.edit {
            it.remove(tokenKey)
            it.remove(nameKey)
            it.remove(emailKey)
        }
    }
}
