package com.example.libqos_android.api.sig.model

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