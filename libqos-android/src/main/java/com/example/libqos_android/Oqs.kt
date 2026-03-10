package com.example.libqos_android

import com.example.libqos_android.api.model.KemAlgorithm
import com.example.libqos_android.api.kem.KemManager
import com.example.libqos_android.api.kem.KemTimingManager
import com.example.libqos_android.api.sig.SignatureManager
import com.example.libqos_android.api.sig.SignatureTimingManager
import com.example.libqos_android.api.model.SignatureAlgorithm
import com.example.libqos_android.kem.provideKemManager
import com.example.libqos_android.kem.provideKemTimingManager
import com.example.libqos_android.sig.provideSignatureManager
import com.example.libqos_android.sig.provideSignatureTimingManager
import com.example.libqos_android.utils.loadNativeLibrary

/**
 * Main entry point for the liboqs-android library.
 *
 * This singleton object loads the native `oqs-jni` library and provides
 * factory methods for creating KEM and digital-signature managers backed
 * by the Open Quantum Safe (liboqs) C library.
 *
 * Usage example:
 * ```kotlin
 * val kem = Oqs.createKemManager(PqcAlgorithm.Kem.MlKem3)
 * kem.use { manager ->
 *     val keypair = manager.generateKeyPair()
 *     val encaps  = manager.encapsulate(keypair.public)
 *     val ss      = manager.decapsulate(encaps.kemCiphertext)
 * }
 * ```
 *
 * @see KEMs
 * @see Sigs
 */
object Oqs {
    internal const val LIB_NAME = "oqs-jni"

    init {
        loadNativeLibrary()
    }

    /**
     * Creates a [KemManager] for the given KEM algorithm.
     *
     * The returned manager must be [closed][AutoCloseable.close] after use
     * to release native resources and wipe secret-key material from memory.
     *
     * @param kemAlgorithm the KEM algorithm to instantiate
     * @return a new [KemManager] instance
     * @throws MechanismNotEnabledError if the algorithm is compiled but disabled
     * @throws MechanismNotSupportedError if the algorithm is not recognized
     */
    fun createKemManager(kemAlgorithm: KemAlgorithm): KemManager =
        provideKemManager(kemAlgorithm)

    /**
     * Creates a [KemTimingManager] for the given KEM algorithm.
     *
     * In addition to the standard [KemManager] operations, the returned
     * manager exposes methods that measure individual operation latencies
     * in nanoseconds via the native timing API.
     *
     * @param kemAlgorithm the KEM algorithm to instantiate
     * @return a new [KemTimingManager] instance
     * @throws MechanismNotEnabledError if the algorithm is compiled but disabled
     * @throws MechanismNotSupportedError if the algorithm is not recognized
     */
    fun createKemTimingManager(kemAlgorithm: KemAlgorithm): KemTimingManager =
        provideKemTimingManager(kemAlgorithm)

    /**
     * Creates a [SignatureManager] for the given digital-signature algorithm.
     *
     * The returned manager must be [closed][AutoCloseable.close] after use
     * to release native resources and wipe secret-key material from memory.
     *
     * @param sigAlgorithm the signature algorithm to instantiate
     * @return a new [SignatureManager] instance
     * @throws MechanismNotEnabledError if the algorithm is compiled but disabled
     * @throws MechanismNotSupportedError if the algorithm is not recognized
     */
    fun createSignatureManager(sigAlgorithm: SignatureAlgorithm): SignatureManager =
        provideSignatureManager(sigAlgorithm)

    /**
     * Creates a [SignatureTimingManager] for the given digital-signature algorithm.
     *
     * In addition to the standard [SignatureManager] operations, the returned
     * manager exposes methods that measure individual operation latencies
     * in nanoseconds via the native timing API.
     *
     * @param sigAlgorithm the signature algorithm to instantiate
     * @return a new [SignatureTimingManager] instance
     * @throws MechanismNotEnabledError if the algorithm is compiled but disabled
     * @throws MechanismNotSupportedError if the algorithm is not recognized
     */
    fun createSignatureTimingManager(sigAlgorithm: SignatureAlgorithm): SignatureTimingManager =
        provideSignatureTimingManager(sigAlgorithm)
}