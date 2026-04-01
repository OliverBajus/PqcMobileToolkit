package io.github.oliverbajus.liboqs_android.api.kem.model

/** Wrapper around a raw KEM public key byte array. */
class KemPublicKey(val bytes: ByteArray)

/** Wrapper around a raw KEM private (secret) key byte array. */
class KemPrivateKey(val bytes: ByteArray)

/**
 * A KEM key pair consisting of a [public] key and an `internal` [private] key.
 *
 * The private key is `internal` to prevent accidental leakage outside the library.
 */
data class KemKeypair(val public: KemPublicKey, internal val private: KemPrivateKey)
