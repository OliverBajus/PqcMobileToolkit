package io.github.oliverbajus.liboqs_android.sig

import io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm
import io.github.oliverbajus.liboqs_android.api.model.SignatureAlgorithm
import io.github.oliverbajus.liboqs_android.api.model.sigFromIdOrNull
import io.github.oliverbajus.liboqs_android.utils.loadNativeLibrary

/**
 * Singleton providing metadata about digital-signature algorithms available
 * in the native liboqs build.
 *
 * Use this object to query which signature algorithms are compiled into
 * ("supported") and activated ("enabled") in the current native library build.
 * The results depend on the liboqs compile-time configuration shipped with the AAR.
 *
 * @see Oqs.createSignatureManager
 */
object Sigs {

    init {
        loadNativeLibrary()
    }

    /** JNI: `OQS_SIG_alg_count()` -- total number of compiled signature algorithms. */
    private external fun maxNumberSigs(): Int

    /** JNI: `OQS_SIG_alg_is_enabled(method_name)`. */
    private external fun isSigEnabled(alg_name: String?): Boolean

    /** JNI: `OQS_SIG_alg_identifier(i)` -- algorithm name by index. */
    private external fun getSigName(alg_id: Long): String?

    /**
     * Returns the liboqs identifier strings of all *supported* (compiled) signature algorithms.
     */
    fun supportedIds(): List<String> =
        (0 until maxNumberSigs()).mapNotNull { i -> getSigName(i.toLong()) }

    /**
     * Returns the liboqs identifier strings of all *enabled* signature algorithms.
     */
    fun enabledIds(): List<String> =
        supportedIds().filter(::isSigEnabled)

    /**
     * Returns typed [SignatureAlgorithm] objects for all *supported* algorithms.
     *
     * @param includeUnknown if `true`, algorithms that have no matching
     *   `data object` in [PqcAlgorithm.Sig] are included as [PqcAlgorithm.Sig.UnknownSig]
     */
    fun supportedAlgorithms(
        includeUnknown: Boolean = false
    ): List<SignatureAlgorithm> =
        supportedIds().mapNotNull { id ->
            sigFromIdOrNull(id) ?: if (includeUnknown) PqcAlgorithm.Sig.UnknownSig(id) else null
        }

    /**
     * Returns typed [SignatureAlgorithm] objects for all *enabled* algorithms.
     *
     * @param includeUnknown if `true`, algorithms that have no matching
     *   `data object` in [PqcAlgorithm.Sig] are included as [PqcAlgorithm.Sig.UnknownSig]
     */
    fun enabledAlgorithms(
        includeUnknown: Boolean = false
    ): List<SignatureAlgorithm> =
        enabledIds().mapNotNull { id ->
            sigFromIdOrNull(id) ?: if (includeUnknown) PqcAlgorithm.Sig.UnknownSig(id) else null
        }

    /** Returns `true` if the algorithm is compiled into the native library. */
    fun isSupported(alg: SignatureAlgorithm): Boolean = supportedIds().contains(alg.id)

    /** Returns `true` if the algorithm is both compiled and enabled at runtime. */
    fun isEnabled(alg: SignatureAlgorithm): Boolean = isSigEnabled(alg.id)
}
