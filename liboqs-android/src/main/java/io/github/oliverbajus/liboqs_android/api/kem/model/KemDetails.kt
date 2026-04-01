package io.github.oliverbajus.liboqs_android.api.kem.model

/**
 * Immutable metadata describing a KEM algorithm instance.
 *
 * @property methodName        liboqs algorithm name (e.g., `"ML-KEM-768"`)
 * @property version           liboqs version string of the algorithm implementation
 * @property claimedNistLevel  claimed NIST post-quantum security level (1-5)
 * @property indCca            `true` if the scheme is IND-CCA secure
 * @property publicKeyLength   public key size in bytes
 * @property secretKeyLength   secret (private) key size in bytes
 * @property ciphertextLength  ciphertext size in bytes
 * @property sharedSecretLength shared secret size in bytes
 */
data class KemDetails(
    val methodName: String,
    val version: String,
    val claimedNistLevel: Int,
    val indCca: Boolean,
    val publicKeyLength: Long,
    val secretKeyLength: Long,
    val ciphertextLength: Long,
    val sharedSecretLength: Long,
) {
    override fun toString(): String {
        return "KEM Details:" +
                    "\n  Name: " + this.methodName +
                    "\n  Version: " + this.version +
                    "\n  Claimed NIST level: " + this.claimedNistLevel +
                    "\n  Is IND-CCA: " + this.indCca +
                    "\n  Length public key (bytes): " + this.publicKeyLength +
                    "\n  Length secret key (bytes): " + this.secretKeyLength +
                    "\n  Length ciphertext (bytes): " + this.ciphertextLength +
                    "\n  Length shared secret (bytes): " + this.sharedSecretLength
    }
}
