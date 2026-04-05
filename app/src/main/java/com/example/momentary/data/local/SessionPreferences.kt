package com.example.momentary.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("session")

class SessionPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val tokenKey = stringPreferencesKey("token")

    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { it[tokenKey] }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[tokenKey] = token
        }
    }

    suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
