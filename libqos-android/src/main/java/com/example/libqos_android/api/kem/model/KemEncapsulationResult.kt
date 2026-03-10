package com.example.libqos_android.api.kem.model

/** Wrapper around a raw KEM ciphertext byte array. */
class KemCiphertext(val bytes: ByteArray)

/** Wrapper around a raw KEM shared secret byte array. */
class KemSharedSecret(val bytes: ByteArray)

/**
 * Result of a KEM encapsulation operation.
 *
 * @property kemCiphertext   the ciphertext to be sent to the decapsulator
 * @property kemSharedSecret the shared secret established by the encapsulator
 */
data class KemEncapsulationResult(
    val kemCiphertext: KemCiphertext,
    val kemSharedSecret: KemSharedSecret,
)