package cz.monetplus.pqc.benchmark.bouncyCastle.performance.dsa

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.ParametersWithRandom
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import cz.monetplus.pqc.benchmark.utils.model.BcSig
import cz.monetplus.pqc.benchmark.utils.BcFactory
import cz.monetplus.pqc.benchmark.utils.BcSigManager
import java.security.SecureRandom

@RunWith(AndroidJUnit4::class)
class TestSigVerifyBc {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val secureRandom = SecureRandom()
    private val bcFactory = BcFactory(secureRandom)

    private lateinit var sigManager: BcSigManager

    private val message: ByteArray = ByteArray(32) { it.toByte() }

    @Test fun benchmarkFalcon1024Verify() = bench(BcSig.FALCON_1024)
    @Test fun benchmarkMayo3Verify() = bench(BcSig.MAYO_3)
    @Test fun benchmarkMayo5Verify() = bench(BcSig.MAYO_5)
    @Test fun benchmarkMlDsa65Verify() = bench(BcSig.MLDSA_65)
    @Test fun benchmarkMlDsa87Verify() = bench(BcSig.MLDSA_87)
    @Test fun benchmarkSnova24_5_5SskVerify() = bench(BcSig.SNOVA_24_5_5_SSK)
    @Test fun benchmarkSnova24_5_5EskVerify() = bench(BcSig.SNOVA_24_5_5_ESK)
    @Test fun benchmarkSnova37_8_4SskVerify() = bench(BcSig.SNOVA_37_8_4_SSK)
    @Test fun benchmarkSnova37_8_4EskVerify() = bench(BcSig.SNOVA_37_8_4_ESK)
    @Test fun benchmarkSnova49_11_3SskVerify() = bench(BcSig.SNOVA_49_11_3_SSK)
    @Test fun benchmarkSnova49_11_3EskVerify() = bench(BcSig.SNOVA_49_11_3_ESK)
    @Test fun benchmarkSnova56_25_2SskVerify() = bench(BcSig.SNOVA_56_25_2_SSK)
    @Test fun benchmarkSnova56_25_2EskVerify() = bench(BcSig.SNOVA_56_25_2_ESK)
    @Test fun benchmarkSnova29_6_5SskVerify() = bench(BcSig.SNOVA_29_6_5_SSK)
    @Test fun benchmarkSnova29_6_5EskVerify() = bench(BcSig.SNOVA_29_6_5_ESK)
    @Test fun benchmarkSnova60_10_4SskVerify() = bench(BcSig.SNOVA_60_10_4_SSK)
    @Test fun benchmarkSnova60_10_4EskVerify() = bench(BcSig.SNOVA_60_10_4_ESK)
    @Test fun benchmarkSlhdsaSha2_192fVerify() = bench(BcSig.SLHDSA_SHA2_192F)
    @Test fun benchmarkSlhdsaSha2_192sVerify() = bench(BcSig.SLHDSA_SHA2_192S)
    @Test fun benchmarkSlhdsaSha2_256fVerify() = bench(BcSig.SLHDSA_SHA2_256F)
    @Test fun benchmarkSlhdsaSha2_256sVerify() = bench(BcSig.SLHDSA_SHA2_256S)
    @Test fun benchmarkSlhdsaShake_192fVerify() = bench(BcSig.SLHDSA_SHAKE_192F)
    @Test fun benchmarkSlhdsaShake_192sVerify() = bench(BcSig.SLHDSA_SHAKE_192S)
    @Test fun benchmarkSlhdsaShake_256fVerify() = bench(BcSig.SLHDSA_SHAKE_256F)
    @Test fun benchmarkSlhdsaShake_256sVerify() = bench(BcSig.SLHDSA_SHAKE_256S)

    private fun bench(alg: BcSig) {
        sigManager = bcFactory.createSignatureManager(alg)

        val keyPair: AsymmetricCipherKeyPair = sigManager.generateKeyPair()

        val signer = sigManager.signerFactory(
            true,
            ParametersWithRandom(keyPair.private, secureRandom)
        )
        val signature: ByteArray = signer.generateSignature(message)

        val verifier = sigManager.signerFactory(false, keyPair.public)

        benchmarkRule.measureRepeated {
            verifier.verifySignature(message, signature)
        }
    }
}