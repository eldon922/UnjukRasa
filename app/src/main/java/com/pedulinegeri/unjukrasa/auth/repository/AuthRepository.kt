package com.pedulinegeri.unjukrasa.auth.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun saveSignedInStatus(status: Boolean)
    suspend fun saveUid(uid: String)
    suspend fun saveName(name: String)
    fun getSignedInStatus(): Flow<Boolean>
    fun getUid(): Flow<String>
    fun getName(): Flow<String>
}