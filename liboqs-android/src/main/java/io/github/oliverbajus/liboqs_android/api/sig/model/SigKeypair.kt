package io.github.oliverbajus.liboqs_android.api.sig.model

/** Wrapper around a raw signature public key byte array. */
class SigPublicKey(val bytes: ByteArray)

/** Wrapper around a raw signature private (secret) key byte array. */
class SigPrivateKey(val bytes: ByteArray)

/** A digital-signature key pair consisting of a [public] and [private] key. */
data class SigKeypair(val public: SigPublicKey, val private: SigPrivateKey)
