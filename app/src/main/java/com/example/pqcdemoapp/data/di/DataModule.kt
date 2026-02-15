package com.example.pqcdemoapp.data.di

import com.example.pqcdemoapp.data.bc.BcPqcRepository
import com.example.pqcdemoapp.data.liboqs.OqsPqcRepository
import com.example.pqcdemoapp.domain.PqcRepository
import com.example.pqcdemoapp.domain.di.BcRepo
import com.example.pqcdemoapp.domain.di.OqsRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.security.SecureRandom
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    @OqsRepo
    abstract fun bindOqsRepo(impl: OqsPqcRepository): PqcRepository

    @Binds
    @Singleton
    @BcRepo
    abstract fun bindBcRepo(impl: BcPqcRepository): PqcRepository
}