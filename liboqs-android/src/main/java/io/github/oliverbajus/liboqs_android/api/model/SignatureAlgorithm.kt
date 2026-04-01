package io.github.oliverbajus.liboqs_android.api.model

/**
 * Sealed interface representing a digital-signature algorithm supported
 * by liboqs.
 *
 * Each concrete implementation is a `data object` inside [PqcAlgorithm.Sig]
 * (e.g., [PqcAlgorithm.Sig.MlDsa3]). Unknown algorithms discovered at
 * runtime are represented by [PqcAlgorithm.Sig.UnknownSig].
 *
 * @property id the liboqs algorithm identifier string (e.g., `"ML-DSA-65"`)
 * @property name a human-readable name derived from the class name
 */
sealed interface SignatureAlgorithm {
    val id: String
    val name: String
        get() = this.algoName()
}