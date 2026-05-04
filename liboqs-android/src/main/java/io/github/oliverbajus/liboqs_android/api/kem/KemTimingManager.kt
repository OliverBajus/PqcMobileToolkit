package io.github.oliverbajus.liboqs_android.api.kem

import io.github.oliverbajus.liboqs_android.api.kem.model.KemCiphertext
import io.github.oliverbajus.liboqs_android.api.kem.model.KemPrivateKey
import io.github.oliverbajus.liboqs_android.api.kem.model.KemPublicKey

/**
 * Extended [KemManager] that exposes native-level timing measurements
 * for individual KEM operations.
 *
 * Each `time*` method executes the corresponding cryptographic operation
 * inside native code and returns the elapsed wall-clock time in nanoseconds.
 * This is useful for benchmarking and TVLA-style performance analysis.
 */
interface KemTimingManager : KemManager {

    /**
     * Generates a key pair and returns the elapsed time in nanoseconds.
     *
     * @return elapsed time of key generation in nanoseconds
     * @throws RuntimeException if native timing fails
     */
    fun timeKeygenNs(): Long

    /**
     * Encapsulates a shared secret and returns the elapsed time in nanoseconds.
     *
     * @param publicKey the recipient's public key !! NOTE: send copy of the key bytes
     * @return elapsed time of encapsulation in nanoseconds
     * @throws RuntimeException if native timing fails or the key length is invalid
     */
    fun timeEncapsNs(publicKey: KemPublicKey): Long

    /**
     * Decapsulates a ciphertext and returns the elapsed time in nanoseconds.
     *
     * @param ciphertext the ciphertext to decapsulate
     * @param privateKey the privateKey !! NOTE: send copy of the key bytes
     * @return elapsed time of decapsulation in nanoseconds
     * @throws RuntimeException if native timing fails or lengths are invalid
     */
    fun timeDecapsNs(ciphertext: KemCiphertext, privateKey: KemPrivateKey): Long
}