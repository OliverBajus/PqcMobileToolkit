package com.example.libqos_android.api.kem

import com.example.libqos_android.api.kem.model.KemCiphertext
import com.example.libqos_android.api.kem.model.KemDetails
import com.example.libqos_android.api.kem.model.KemEncapsulationResult
import com.example.libqos_android.api.kem.model.KemKeypair
import com.example.libqos_android.api.kem.model.KemPrivateKey
import com.example.libqos_android.api.kem.model.KemPublicKey
import com.example.libqos_android.api.kem.model.KemSharedSecret
import java.lang.AutoCloseable

/**
 * Manager for Key Encapsulation Mechanism (KEM) operations.
 *
 * Provides key-pair generation, encapsulation, and decapsulation using a
 * post-quantum KEM algorithm. Implementations hold native resources and
 * secret-key material that are released and wiped on [close].
 *
 * Typical flow:
 * ```
 * manager.generateKeyPair()
 * val encaps = manager.encapsulate(publicKey)   // sender side
 * val ss     = manager.decapsulate(encaps.kemCiphertext) // receiver side
 * ```
 */
interface KemManager : AutoCloseable {

    /** Metadata about the underlying KEM algorithm (key sizes, NIST level, etc.). */
    val kemDetails: KemDetails

    /**
     * Generates a new KEM key pair and stores it internally.
     *
     * @return the generated [KemKeypair]
     * @throws RuntimeException if native key generation fails
     */
    fun generateKeyPair(): KemKeypair

    /**
     * Encapsulates a shared secret using the given public key (sender side).
     *
     * @param kemPublicKey the recipient's public key
     * @return [KemEncapsulationResult] containing the ciphertext and the shared secret
     * @throws RuntimeException if encapsulation fails or the key length is invalid
     */
    fun encapsulate(kemPublicKey: KemPublicKey): KemEncapsulationResult

    /**
     * Decapsulates a ciphertext to recover the shared secret (receiver side).
     *
     * A key pair must have been generated or a secret key must have been
     * supplied at construction time before calling this method.
     *
     * @param kemCiphertext the ciphertext produced by [encapsulate]
     * @return the recovered [KemSharedSecret]
     * @throws RuntimeException if decapsulation fails or key/ciphertext lengths are invalid
     */
    fun decapsulate(kemCiphertext: KemCiphertext): KemSharedSecret

    /**
     * Returns the currently stored public key, or `null` if no key pair has been generated.
     */
    fun getPublicKey() : KemPublicKey?

    /**
     * Returns the currently stored private (secret) key, or `null` if unavailable.
     */
    fun getPrivateKey() : KemPrivateKey?
}