package io.github.oliverbajus.liboqs_android.api.model

/**
 * Sealed interface representing a Key Encapsulation Mechanism algorithm
 * supported by liboqs.
 *
 * Each concrete implementation is a `data object` inside [PqcAlgorithm.Kem]
 * (e.g., [PqcAlgorithm.Kem.MlKem3]). Unknown algorithms discovered at runtime
 * are represented by [PqcAlgorithm.Kem.UnknownKem].
 *
 * @property id the liboqs algorithm identifier string (e.g., `"ML-KEM-768"`)
 * @property name a human-readable name derived from the class name
 */
sealed interface KemAlgorithm {
    val id: String
    val name: String
        get() = this.algoName()
}