package com.pedulinegeri.unjukrasa.auth

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(private val dataStoreManager: DataStoreManager) {

    suspend fun saveSignedInStatus(status: Boolean) {
        dataStoreManager.clear()
        dataStoreManager.setSignedInStatus(status)
    }

    suspend fun saveUid(uid: String) {
        dataStoreManager.setUid(uid)
    }

    suspend fun saveName(name: String) {
        dataStoreManager.setName(name)
    }

    fun getSignedInStatus(): Flow<Boolean> {
        return dataStoreManager.signedInStatus
    }

    fun getUid(): Flow<String> {
        return dataStoreManager.uid
    }

    fun getName(): Flow<String> {
        return dataStoreManager.name
    }
}