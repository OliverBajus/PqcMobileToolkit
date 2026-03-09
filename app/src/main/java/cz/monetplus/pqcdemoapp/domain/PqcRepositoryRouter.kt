package cz.monetplus.pqcdemoapp.domain

import cz.monetplus.pqcdemoapp.domain.di.BcRepo
import cz.monetplus.pqcdemoapp.domain.di.OqsRepo
import cz.monetplus.pqcdemoapp.domain.model.PqcLibrary
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