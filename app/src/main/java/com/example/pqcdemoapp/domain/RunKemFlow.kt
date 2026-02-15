package com.example.pqcdemoapp.domain

import com.aheaditec.architecture.domain.error.Failure
import com.aheaditec.architecture.domain.interactor.BaseUseCase
import com.aheaditec.functional.Either
import com.example.pqcdemoapp.domain.model.PqcLibrary
import com.example.pqcdemoapp.domain.model.AlgChoice
import com.example.pqcdemoapp.domain.model.KemResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunKemFlow @Inject constructor(private val router: PqcRepositoryRouter) : BaseUseCase<RunKemFlow.Params, Either<Failure, KemResult>>() {

    override suspend fun execute(input: Params): Either<Failure, KemResult> =
        router.repoFor(input.lib).runKemFlow(input.alg)

    data class Params(
        val lib: PqcLibrary,
        val alg: AlgChoice,
    )
}