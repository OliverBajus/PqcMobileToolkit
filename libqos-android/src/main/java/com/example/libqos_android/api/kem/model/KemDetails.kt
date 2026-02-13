package com.example.libqos_android.api.kem.model

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
