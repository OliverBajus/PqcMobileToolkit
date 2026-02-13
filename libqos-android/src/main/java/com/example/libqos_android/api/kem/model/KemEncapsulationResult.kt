package com.example.libqos_android.api.kem.model

class KemCiphertext(val bytes: ByteArray)
class KemSharedSecret(val bytes: ByteArray)

data class KemEncapsulationResult(
    val kemCiphertext: KemCiphertext,
    val kemSharedSecret: KemSharedSecret,
)