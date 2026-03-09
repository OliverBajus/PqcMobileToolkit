package cz.monetplus.pqcdemoapp.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.security.SecureRandom
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PQCModule {
    @Provides
    @Singleton
    fun provideSecureRandom(): SecureRandom = SecureRandom()
}