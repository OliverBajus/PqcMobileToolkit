package com.example.pqcdemoapp.domain

import com.aheaditec.architecture.domain.interactor.BaseUseCase
import com.example.pqcdemoapp.domain.model.AlgChoice
import com.example.pqcdemoapp.domain.model.PqcLibrary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSupportedKemAlgorithms @Inject constructor(
    private val router: PqcRepositoryRouter
) : BaseUseCase<PqcLibrary, List<AlgChoice> >() {

    override suspend fun execute(input: PqcLibrary): List<AlgChoice> {
        val repo = router.repoFor(input)
        return repo.supportedKems()
    }
}