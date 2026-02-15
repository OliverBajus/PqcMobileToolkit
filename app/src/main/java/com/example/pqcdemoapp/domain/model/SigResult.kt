package com.example.pqcdemoapp.domain.model

data class SigResult(
    val keygenNs: Long,
    val signNs: Long,
    val verifyNs: Long,
    val ok: Boolean,
)
