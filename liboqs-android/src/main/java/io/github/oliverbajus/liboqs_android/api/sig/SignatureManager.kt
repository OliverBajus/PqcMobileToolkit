package io.github.oliverbajus.liboqs_android.api.sig

import io.github.oliverbajus.liboqs_android.api.sig.model.SigDetails
import io.github.oliverbajus.liboqs_android.api.sig.model.SigKeypair
import io.github.oliverbajus.liboqs_android.api.sig.model.SigPrivateKey
import io.github.oliverbajus.liboqs_android.api.sig.model.SigPublicKey

/**
 * Manager for post-quantum digital-signature operations.
 *
 * Provides key-pair generation, message signing, and signature verification.
 * Implementations hold native resources and secret-key material that are
 * released and wiped on [close].
 *
 * Typical flow:
 * ```
 * manager.generateKeyPair()
 * val sig   = manager.sign(message)
 * val valid = manager.verify(message, sig, manager.getPublicKey()!!)
 * ```
 */
interface SignatureManager : AutoCloseable {

    /** Metadata about the underlying signature algorithm (key sizes, NIST level, etc.). */
    val signatureDetails: SigDetails

    /**
     * Generates a new signature key pair and stores it internally.
     *
     * @return the generated [SigKeypair]
     * @throws RuntimeException if native key generation fails
     */
    fun generateKeyPair(): SigKeypair

    /**
     * Signs a message using the internally stored secret key.
     *
     * A key pair must have been generated or a secret key must have been
     * supplied at construction time before calling this method.
     *
     * @param message the raw message bytes to sign
     * @return the signature bytes
     * @throws RuntimeException if signing fails or no secret key is available
     */
    fun sign(message: ByteArray): ByteArray

    /**
     * Verifies a signature against a message and a public key.
     *
     * @param message   the original message bytes
     * @param signature the signature bytes to verify
     * @param publicKey the signer's public key
     * @return `true` if the signature is valid, `false` otherwise
     * @throws RuntimeException if verification encounters an error (e.g., invalid key length)
     */
    fun verify(message: ByteArray, signature: ByteArray, publicKey: SigPublicKey): Boolean

    /**
     * Returns the currently stored public key, or `null` if no key pair has been generated.
     */
    fun getPublicKey() : SigPublicKey?

    /**
     * Returns the currently stored private (secret) key, or `null` if unavailable.
     */
    fun getPrivateKey() : SigPrivateKey?
}