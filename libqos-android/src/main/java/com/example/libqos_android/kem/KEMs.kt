package com.example.libqos_android.kem

import com.example.libqos_android.api.model.KemAlgorithm
import com.example.libqos_android.api.model.PqcAlgorithm
import com.example.libqos_android.api.model.kemFromIdOrNull
import com.example.libqos_android.utils.loadNativeLibrary

/**
 * \brief Key Encapsulation Mechanisms Singleton class.
 * Contains details about supported/enabled key exchange mechanisms (KEMs)
 */
object KEMs {

    init {
        loadNativeLibrary()
    }

    /**
     * \brief Wrapper for OQS_API int OQS_KEM_alg_count(void);
     * \return Maximum number of supported KEM algorithms
     */
    private external fun maxNumberKEMs(): Int

    /**
     * \brief Wrapper for OQS_API int OQS_KEM_alg_is_enabled(const char *method_name);
     * Checks whether the KEM algorithm alg_name is enabled
     * \param alg_name Cryptographic algorithm name
     * \return True if the KEM algorithm is enabled, false otherwise
     */
    private external fun isKemEnabled(alg_name: String?): Boolean

    /**
     * \brief Wrapper for OQS_API const char *OQS_KEM_alg_identifier(size_t i);
     * \param alg_id Cryptographic algorithm numerical id
     * \return KEM algorithm name
     */
    private external fun getKemName(alg_id: Long): String?


    fun supportedIds(): List<String> =
        (0 until maxNumberKEMs()).mapNotNull { i -> getKemName(i.toLong()) }

    fun enabledIds(): List<String> =
        supportedIds().filter(::isKemEnabled)

    fun supportedAlgorithms(
        includeUnknown: Boolean = false
    ): List<KemAlgorithm> =
        supportedIds().mapNotNull { id ->
            kemFromIdOrNull(id) ?: if (includeUnknown) PqcAlgorithm.Kem.UnknownKem(id) else null
        }

    fun enabledAlgorithms(
        includeUnknown: Boolean = false
    ): List<KemAlgorithm> =
        enabledIds().mapNotNull { id ->
            kemFromIdOrNull(id) ?: if (includeUnknown) PqcAlgorithm.Kem.UnknownKem(id) else null
        }

    fun isSupported(alg: KemAlgorithm): Boolean = supportedIds().contains(alg.id)
    fun isEnabled(alg: KemAlgorithm): Boolean = isKemEnabled(alg.id)
}
