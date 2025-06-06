package com.example.pqcdemoapp

import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.SecretWithEncapsulation
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters
import java.security.SecureRandom
import java.security.Security
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureNanoTime

@Singleton
class MLKEMService @Inject constructor() {
    fun executeMLKEMFlow(selectedParameter: MLKEMParameters): MLKEMResult {

        val random = SecureRandom()
        val keyGen = MLKEMKeyPairGenerator()
        keyGen.init(MLKEMKeyGenerationParameters(random, selectedParameter))

        // Generate Key Pair
        val keyPair: AsymmetricCipherKeyPair = keyGen.generateKeyPair()
        val publicKey = keyPair.public as MLKEMPublicKeyParameters
        val privateKey = keyPair.private as MLKEMPrivateKeyParameters

        // Encapsulation (Encrypt + Generate Shared Secret)
        val kemGen = MLKEMGenerator(random)
        val secretEncapsulation: SecretWithEncapsulation = kemGen.generateEncapsulated(publicKey)

        val cipherText = secretEncapsulation.encapsulation
        val sharedSecret = secretEncapsulation.secret

        // Decapsulation (Decrypt + Recover Shared Secret)
        val kemExtract = MLKEMExtractor(privateKey)
        val decryptedSharedSecret = kemExtract.extractSecret(cipherText)

        return MLKEMResult(cipherText, sharedSecret, decryptedSharedSecret)
    }
}

// Data class to hold the ML-KEM results
data class MLKEMResult(
    val cipherText: ByteArray,
    val sharedSecret: ByteArray,
    val decryptedSecret: ByteArray
)