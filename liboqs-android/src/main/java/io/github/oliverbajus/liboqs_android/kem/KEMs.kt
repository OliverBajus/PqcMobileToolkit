package io.github.oliverbajus.liboqs_android.kem

import io.github.oliverbajus.liboqs_android.api.model.KemAlgorithm
import io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm
import io.github.oliverbajus.liboqs_android.api.model.kemFromIdOrNull
import io.github.oliverbajus.liboqs_android.utils.loadNativeLibrary

/**
 * Singleton providing metadata about KEM algorithms available in the
 * native liboqs build.
 *
 * Use this object to query which KEM algorithms are compiled into ("supported")
 * and activated ("enabled") in the current native library build. The results
 * depend on the liboqs compile-time configuration shipped with the AAR.
 *
 * @see Oqs.createKemManager
 */
object KEMs {

    init {
        loadNativeLibrary()
    }

    /** JNI: `OQS_KEM_alg_count()` -- total number of compiled KEM algorithms. */
    private external fun maxNumberKEMs(): Int

    /** JNI: `OQS_KEM_alg_is_enabled(method_name)`. */
    private external fun isKemEnabled(alg_name: String?): Boolean

    /** JNI: `OQS_KEM_alg_identifier(i)` -- algorithm name by index. */
    private external fun getKemName(alg_id: Long): String?

    /**
     * Returns the liboqs identifier strings of all *supported* (compiled) KEM algorithms.
     */
    fun supportedIds(): List<String> =
        (0 until maxNumberKEMs()).mapNotNull { i -> getKemName(i.toLong()) }

    /**
     * Returns the liboqs identifier strings of all *enabled* KEM algorithms.
     */
    fun enabledIds(): List<String> =
        supportedIds().filter(::isKemEnabled)

    /**
     * Returns typed [KemAlgorithm] objects for all *supported* algorithms.
     *
     * @param includeUnknown if `true`, algorithms that have no matching
     *   `data object` in [PqcAlgorithm.Kem] are included as [PqcAlgorithm.Kem.UnknownKem]
     */
    fun supportedAlgorithms(
        includeUnknown: Boolean = false
    ): List<KemAlgorithm> =
        supportedIds().mapNotNull { id ->
            kemFromIdOrNull(id) ?: if (includeUnknown) PqcAlgorithm.Kem.UnknownKem(id) else null
        }

    /**
     * Returns typed [KemAlgorithm] objects for all *enabled* algorithms.
     *
     * @param includeUnknown if `true`, algorithms that have no matching
     *   `data object` in [PqcAlgorithm.Kem] are included as [PqcAlgorithm.Kem.UnknownKem]
     */
    fun enabledAlgorithms(
        includeUnknown: Boolean = false
    ): List<KemAlgorithm> =
        enabledIds().mapNotNull { id ->
            kemFromIdOrNull(id) ?: if (includeUnknown) PqcAlgorithm.Kem.UnknownKem(id) else null
        }

    /** Returns `true` if the algorithm is compiled into the native library. */
    fun isSupported(alg: KemAlgorithm): Boolean = supportedIds().contains(alg.id)

    /** Returns `true` if the algorithm is both compiled and enabled at runtime. */
    fun isEnabled(alg: KemAlgorithm): Boolean = isKemEnabled(alg.id)
}
