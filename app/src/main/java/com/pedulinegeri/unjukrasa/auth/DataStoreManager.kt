package com.pedulinegeri.unjukrasa.auth

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore("auth")

class DataStoreManager(appContext: Context) {

    private val settingsDataStore = appContext.dataStore
    private val SIGNED_IN = booleanPreferencesKey("signed_in")

    suspend fun setSignedInStatus(status: Boolean) {
        settingsDataStore.edit { auth ->
            auth[SIGNED_IN] = status
        }
    }

    val signedInStatus = settingsDataStore.data.map { preferences ->
        preferences[SIGNED_IN] ?: false
    }

    suspend fun clear() {
        settingsDataStore.edit {
            it.clear()
        }
    }
}