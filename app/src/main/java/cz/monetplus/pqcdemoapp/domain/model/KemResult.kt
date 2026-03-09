package cz.monetplus.pqcdemoapp.domain.model

data class KemResult(
    val keygenNs: Long,
    val encapsNs: Long,
    val decapsNs: Long,
    val ok: Boolean,
)