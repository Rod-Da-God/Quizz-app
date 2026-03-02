package com.example.kurs.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val USER_ID_KEY = longPreferencesKey("user_id")

    val userId: Flow<Long?> = context.dataStore.data
        .map { prefs -> prefs[USER_ID_KEY] }

    suspend fun saveUserId(id: Long) {
        context.dataStore.edit { prefs -> prefs[USER_ID_KEY] = id }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}