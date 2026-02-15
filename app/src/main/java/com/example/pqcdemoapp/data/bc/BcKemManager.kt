package com.example.pqcdemoapp.data.bc

import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
import org.bouncycastle.crypto.EncapsulatedSecretExtractor
import org.bouncycastle.crypto.EncapsulatedSecretGenerator
import org.bouncycastle.crypto.SecretWithEncapsulation
import org.bouncycastle.crypto.params.AsymmetricKeyParameter

class BcKemManager(
    private val kemGenerator: EncapsulatedSecretGenerator,
    private val keyGenerator: AsymmetricCipherKeyPairGenerator,
    val extractorFactory: (AsymmetricKeyParameter) -> EncapsulatedSecretExtractor
) {
    fun generateKeyPair(): AsymmetricCipherKeyPair =
        keyGenerator.generateKeyPair()

    fun encapsulate(keyPair: AsymmetricCipherKeyPair): SecretWithEncapsulation =
        kemGenerator.generateEncapsulated(keyPair.public)

    fun decapsulation(fixedEncaps: SecretWithEncapsulation, kemExtractor: EncapsulatedSecretExtractor): ByteArray =
        kemExtractor.extractSecret(fixedEncaps.encapsulation)
}