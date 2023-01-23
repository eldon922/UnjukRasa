package com.pedulinegeri.unjukrasa.auth.repository

import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl constructor(private val dataStoreManager: DataStoreManager) :
    AuthRepository {

    override suspend fun saveSignedInStatus(status: Boolean) {
        dataStoreManager.clear()
        dataStoreManager.setSignedInStatus(status)
    }

    override suspend fun saveUid(uid: String) {
        dataStoreManager.setUid(uid)
    }

    override suspend fun saveName(name: String) {
        dataStoreManager.setName(name)
    }

    override fun getSignedInStatus(): Flow<Boolean> {
        return dataStoreManager.signedInStatus
    }

    override fun getUid(): Flow<String> {
        return dataStoreManager.uid
    }

    override fun getName(): Flow<String> {
        return dataStoreManager.name
    }
}