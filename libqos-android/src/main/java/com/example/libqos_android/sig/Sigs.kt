package com.example.libqos_android.sig

import com.example.libqos_android.api.model.PqcAlgorithm
import com.example.libqos_android.api.model.SignatureAlgorithm
import com.example.libqos_android.api.model.sigFromIdOrNull
import com.example.libqos_android.utils.loadNativeLibrary

/**
 * \brief Signatures singleton class.
 * Singleton class, contains details about supported/enabled signature mechanisms
 */
object Sigs {

    init {
        loadNativeLibrary()
    }

    /**
     * \brief Wrapper for OQS_API int OQS_SIG_alg_count(void);
     * \return Maximum number of supported signature algorithms
     */
    private external fun maxNumberSigs(): Int

    /**
     * \brief Wrapper for OQS_API int OQS_SIG_alg_is_enabled(const char *method_name);
     * Checks whether the signature algorithm alg_name is enabled
     * \param alg_name Cryptographic algorithm name
     * \return True if the signature algorithm is enabled, false otherwise
     */
    private external fun isSigEnabled(alg_name: String?): Boolean

    /**
     * \brief Wrapper for OQS_API const char *OQS_SIG_alg_identifier(size_t i);
     * \param alg_id Cryptographic algorithm numerical id
     * \return signature algorithm name
     */
    private external fun getSigName(alg_id: Long): String?

    fun supportedIds(): List<String> =
        (0 until maxNumberSigs()).mapNotNull { i -> getSigName(i.toLong()) }

    fun enabledIds(): List<String> =
        supportedIds().filter(::isSigEnabled)


    fun supportedAlgorithms(
        includeUnknown: Boolean = false
    ): List<SignatureAlgorithm> =
        supportedIds().mapNotNull { id ->
            sigFromIdOrNull(id) ?: if (includeUnknown) PqcAlgorithm.Sig.UnknownSig(id) else null
        }

    fun enabledAlgorithms(
        includeUnknown: Boolean = false
    ): List<SignatureAlgorithm> =
        enabledIds().mapNotNull { id ->
            sigFromIdOrNull(id) ?: if (includeUnknown) PqcAlgorithm.Sig.UnknownSig(id) else null
        }

    fun isSupported(alg: SignatureAlgorithm): Boolean = supportedIds().contains(alg.id)
    fun isEnabled(alg: SignatureAlgorithm): Boolean = isSigEnabled(alg.id)
}
