package io.github.oliverbajus.liboqs_android.api.sig

import io.github.oliverbajus.liboqs_android.api.sig.model.SigPrivateKey
import io.github.oliverbajus.liboqs_android.api.sig.model.SigPublicKey

/**
 * Extended [SignatureManager] that exposes native-level timing measurements
 * for individual signature operations.
 *
 * Each `time*` method executes the corresponding cryptographic operation
 * inside native code and returns the elapsed wall-clock time in nanoseconds.
 * This is useful for benchmarking and TVLA-style performance analysis.
 */
interface SignatureTimingManager : SignatureManager {

    /**
     * Generates a key pair and returns the elapsed time in nanoseconds.
     *
     * @return elapsed time of key generation in nanoseconds
     * @throws RuntimeException if native timing fails
     */
    fun timeKeygenNs(): Long

    /**
     * Signs a message and returns the elapsed time in nanoseconds.
     *
     * @param message the raw message bytes to sign
     * @param privateKey the private key to sign with. !! NOTE: send copy of the key bytes
     * @return elapsed time of signing in nanoseconds
     * @throws RuntimeException if native timing fails
     */
    fun timeSignNs(message: ByteArray, privateKey: SigPrivateKey): Long

    /**
     * Verifies a signature and returns the elapsed time in nanoseconds.
     *
     * @param message   the original message bytes
     * @param signature the signature bytes to verify
     * @param publicKey the signer's public key !! NOTE: send copy of the key bytes
     * @return elapsed time of verification in nanoseconds
     * @throws RuntimeException if native timing fails or key lengths are invalid
     */
    fun timeVerifyNs(message: ByteArray, signature: ByteArray, publicKey: SigPublicKey): Long
}