package cz.monetplus.pqcdemoapp.data.di

import cz.monetplus.pqcdemoapp.data.bc.BcPqcRepository
import cz.monetplus.pqcdemoapp.data.liboqs.OqsPqcRepository
import cz.monetplus.pqcdemoapp.domain.PqcRepository
import cz.monetplus.pqcdemoapp.domain.di.BcRepo
import cz.monetplus.pqcdemoapp.domain.di.OqsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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