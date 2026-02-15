package com.example.pqcdemoapp.domain

import com.example.pqcdemoapp.domain.di.BcRepo
import com.example.pqcdemoapp.domain.di.OqsRepo
import com.example.pqcdemoapp.domain.model.PqcLibrary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PqcRepositoryRouter @Inject constructor(
    @OqsRepo private val oqs: PqcRepository,
    @BcRepo private val bc: PqcRepository
) {
    fun repoFor(lib: PqcLibrary): PqcRepository = when (lib) {
        PqcLibrary.OQS -> oqs
        PqcLibrary.BC  -> bc
    }
}