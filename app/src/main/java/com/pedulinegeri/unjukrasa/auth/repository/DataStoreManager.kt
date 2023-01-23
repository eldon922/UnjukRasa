package com.pedulinegeri.unjukrasa.auth.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore("auth")

class DataStoreManager(appContext: Context) {

    private val settingsDataStore = appContext.dataStore
    private val SIGNED_IN = booleanPreferencesKey("signed_in")
    private val UID = stringPreferencesKey("uid")
    private val NAME = stringPreferencesKey("name")

    suspend fun setSignedInStatus(status: Boolean) {
        settingsDataStore.edit { auth ->
            auth[SIGNED_IN] = status
        }
    }

    suspend fun setUid(uid: String) {
        settingsDataStore.edit { auth ->
            auth[UID] = uid
        }
    }

    suspend fun setName(name: String) {
        settingsDataStore.edit { auth ->
            auth[NAME] = name
        }
    }

    val signedInStatus = settingsDataStore.data.map { preferences ->
        preferences[SIGNED_IN] ?: false
    }

    val uid = settingsDataStore.data.map { preferences ->
        preferences[UID] ?: ""
    }

    val name = settingsDataStore.data.map { preferences ->
        preferences[NAME] ?: ""
    }

    suspend fun clear() {
        settingsDataStore.edit {
            it.clear()
        }
    }
}