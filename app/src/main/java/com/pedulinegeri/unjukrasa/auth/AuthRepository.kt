package com.pedulinegeri.unjukrasa.auth

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(private val dataStoreManager: DataStoreManager) {

    suspend fun saveSignedInStatus(status: Boolean) {
        dataStoreManager.clear()
        dataStoreManager.setSignedInStatus(status)
    }

    fun getSignedInStatus(): Flow<Boolean> {
        return dataStoreManager.signedInStatus
    }
}