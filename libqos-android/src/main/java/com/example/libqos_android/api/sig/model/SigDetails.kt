package com.example.libqos_android.api.sig.model

/**
 * Immutable metadata describing a digital-signature algorithm instance.
 *
 * @property methodName        liboqs algorithm name (e.g., `"ML-DSA-65"`)
 * @property version           liboqs version string of the algorithm implementation
 * @property claimedNistLevel  claimed NIST post-quantum security level (1-5)
 * @property isEufCma          `true` if the scheme is EUF-CMA secure
 * @property publicKeyLength   public key size in bytes
 * @property secretKeyLength   secret (private) key size in bytes
 * @property signatureMaxLength maximum signature size in bytes
 */
data class SigDetails(
    val methodName: String,
    val version: String,
    val claimedNistLevel: Int,
    val isEufCma: Boolean,
    val publicKeyLength: Long,
    val secretKeyLength: Long,
    val signatureMaxLength: Long,
) {
    override fun toString(): String {
        return "Signature Details:" +
                "\n  Name: " + this.methodName +
                "\n  Version: " + this.version +
                "\n  Claimed NIST level: " + this.claimedNistLevel +
                "\n  Is IND-CCA: " + this.isEufCma +
                "\n  Length public key (bytes): " + this.publicKeyLength +
                "\n  Length secret key (bytes): " + this.secretKeyLength +
                "\n  Maximum length signature (bytes): " + this.signatureMaxLength
    }
}