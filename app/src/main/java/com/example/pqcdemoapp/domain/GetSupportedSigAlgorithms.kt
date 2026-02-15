package com.example.pqcdemoapp.domain

import com.aheaditec.architecture.domain.interactor.BaseUseCase
import com.example.pqcdemoapp.domain.model.PqcLibrary
import com.example.pqcdemoapp.domain.model.AlgChoice
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSupportedSigAlgorithms @Inject constructor(
    private val router: PqcRepositoryRouter
) : BaseUseCase<PqcLibrary, List<AlgChoice> >() {

    override suspend fun execute(input: PqcLibrary): List<AlgChoice> {
        val repo = router.repoFor(input)
        return repo.supportedSigs()
    }
}