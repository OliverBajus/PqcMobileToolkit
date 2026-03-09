package cz.monetplus.pqcdemoapp.domain

import com.aheaditec.architecture.domain.error.Failure
import com.aheaditec.architecture.domain.interactor.BaseUseCase
import com.aheaditec.functional.Either
import cz.monetplus.pqcdemoapp.domain.model.AlgChoice
import cz.monetplus.pqcdemoapp.domain.model.PqcLibrary
import cz.monetplus.pqcdemoapp.domain.model.SigResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunSigFlow @Inject constructor(private val router: PqcRepositoryRouter) : BaseUseCase<RunSigFlow.Params, Either<Failure, SigResult>>() {
    override suspend fun execute(input: Params): Either<Failure, SigResult> =
        router.repoFor(input.lib).runSigFlow(input.alg, input.message)

    class Params(
        val lib: PqcLibrary,
        val alg: AlgChoice,
        val message: ByteArray
    )
}