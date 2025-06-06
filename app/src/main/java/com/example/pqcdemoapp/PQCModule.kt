package com.example.pqcdemoapp

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PQCModule {

    @Provides
    @Singleton
    fun provideMLKEMService(): MLKEMService = MLKEMService()
}