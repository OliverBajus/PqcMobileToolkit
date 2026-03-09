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
class TestSigSignBc {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val secureRandom = SecureRandom()
    private val bcFactory = BcFactory(secureRandom)

    private lateinit var sigManager: BcSigManager

    private val message: ByteArray = ByteArray(32) { it.toByte() }

    @Test fun benchmarkFalcon1024Sign() = bench(BcSig.FALCON_1024)
    @Test fun benchmarkMayo3Sign() = bench(BcSig.MAYO_3)
    @Test fun benchmarkMayo5Sign() = bench(BcSig.MAYO_5)
    @Test fun benchmarkMlDsa65Sign() = bench(BcSig.MLDSA_65)
    @Test fun benchmarkMlDsa87Sign() = bench(BcSig.MLDSA_87)
    @Test fun benchmarkSnova24_5_5SskSign() = bench(BcSig.SNOVA_24_5_5_SSK)
    @Test fun benchmarkSnova24_5_5EskSign() = bench(BcSig.SNOVA_24_5_5_ESK)
    @Test fun benchmarkSnova37_8_4SskSign() = bench(BcSig.SNOVA_37_8_4_SSK)
    @Test fun benchmarkSnova37_8_4EskSign() = bench(BcSig.SNOVA_37_8_4_ESK)
    @Test fun benchmarkSnova49_11_3SskSign() = bench(BcSig.SNOVA_49_11_3_SSK)
    @Test fun benchmarkSnova49_11_3EskSign() = bench(BcSig.SNOVA_49_11_3_ESK)
    @Test fun benchmarkSnova56_25_2SskSign() = bench(BcSig.SNOVA_56_25_2_SSK)
    @Test fun benchmarkSnova56_25_2EskSign() = bench(BcSig.SNOVA_56_25_2_ESK)
    @Test fun benchmarkSnova29_6_5SskSign() = bench(BcSig.SNOVA_29_6_5_SSK)
    @Test fun benchmarkSnova29_6_5EskSign() = bench(BcSig.SNOVA_29_6_5_ESK)
    @Test fun benchmarkSnova60_10_4SskSign() = bench(BcSig.SNOVA_60_10_4_SSK)
    @Test fun benchmarkSnova60_10_4EskSign() = bench(BcSig.SNOVA_60_10_4_ESK)
    @Test fun benchmarkSlhdsaSha2_192fSign() = bench(BcSig.SLHDSA_SHA2_192F)
    @Test fun benchmarkSlhdsaSha2_192sSign() = bench(BcSig.SLHDSA_SHA2_192S)
    @Test fun benchmarkSlhdsaSha2_256fSign() = bench(BcSig.SLHDSA_SHA2_256F)
    @Test fun benchmarkSlhdsaSha2_256sSign() = bench(BcSig.SLHDSA_SHA2_256S)
    @Test fun benchmarkSlhdsaShake_192fSign() = bench(BcSig.SLHDSA_SHAKE_192F)
    @Test fun benchmarkSlhdsaShake_192sSign() = bench(BcSig.SLHDSA_SHAKE_192S)
    @Test fun benchmarkSlhdsaShake_256fSign() = bench(BcSig.SLHDSA_SHAKE_256F)
    @Test fun benchmarkSlhdsaShake_256sSign() = bench(BcSig.SLHDSA_SHAKE_256S)

    private fun bench(alg: BcSig) {
        sigManager = bcFactory.createSignatureManager(alg)

        val keyPair: AsymmetricCipherKeyPair = sigManager.generateKeyPair()
        val signer = sigManager.signerFactory(
            true,
            ParametersWithRandom(keyPair.private, secureRandom)
        )

        benchmarkRule.measureRepeated {
            signer.generateSignature(message)
        }
    }
}