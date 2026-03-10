package com.example.libqos_android.api.kem

import com.example.libqos_android.api.kem.model.KemCiphertext
import com.example.libqos_android.api.kem.model.KemPublicKey

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
     * @param publicKey the recipient's public key
     * @return elapsed time of encapsulation in nanoseconds
     * @throws RuntimeException if native timing fails or the key length is invalid
     */
    fun timeEncapsNs(publicKey: KemPublicKey): Long

    /**
     * Decapsulates a ciphertext and returns the elapsed time in nanoseconds.
     *
     * A key pair must have been generated before calling this method.
     *
     * @param ciphertext the ciphertext to decapsulate
     * @return elapsed time of decapsulation in nanoseconds
     * @throws RuntimeException if native timing fails or lengths are invalid
     */
    fun timeDecapsNs(ciphertext: KemCiphertext): Long
}