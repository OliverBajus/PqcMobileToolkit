package cz.monetplus.pqcdemoapp.domain

import com.aheaditec.architecture.domain.error.Failure
import com.aheaditec.functional.Either
import cz.monetplus.pqcdemoapp.domain.model.AlgChoice
import cz.monetplus.pqcdemoapp.domain.model.KemResult
import cz.monetplus.pqcdemoapp.domain.model.SigResult

interface PqcRepository {
    fun supportedKems(): List<AlgChoice>
    fun supportedSigs(): List<AlgChoice>

    suspend fun runKemFlow(
        alg: AlgChoice,
    ): Either<Failure, KemResult>

    suspend fun runSigFlow(
        alg: AlgChoice,
        message: ByteArray
    ): Either<Failure, SigResult>
}