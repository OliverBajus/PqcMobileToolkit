package cz.monetplus.pqc.benchmark.utils

import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.pqc.crypto.MessageSigner

class BcSigManager(
    private val keyGenerator: AsymmetricCipherKeyPairGenerator,
    val signerFactory: (Boolean, CipherParameters) -> MessageSigner
) {

    fun generateKeyPair(): AsymmetricCipherKeyPair =
        keyGenerator.generateKeyPair()

    fun sign(signer: MessageSigner, message: ByteArray): ByteArray =
        signer.generateSignature(message)

    fun verify(verifier: MessageSigner, message: ByteArray, signature: ByteArray): Boolean =
        verifier.verifySignature(message, signature)
}