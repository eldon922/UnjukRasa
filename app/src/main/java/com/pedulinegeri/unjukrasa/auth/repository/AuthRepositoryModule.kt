package com.pedulinegeri.unjukrasa.auth.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthRepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(dataStoreManager: DataStoreManager): AuthRepository =
        AuthRepositoryImpl(dataStoreManager)
}