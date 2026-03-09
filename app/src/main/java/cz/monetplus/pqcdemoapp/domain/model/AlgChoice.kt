package cz.monetplus.pqcdemoapp.domain.model

data class AlgChoice(val id: String, val name: String)

data class AlgCatalog(
    val kems: List<AlgChoice>,
    val sigs: List<AlgChoice>
)