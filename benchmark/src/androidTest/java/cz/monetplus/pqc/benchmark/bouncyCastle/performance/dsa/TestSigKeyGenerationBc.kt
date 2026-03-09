package cz.monetplus.pqc.benchmark.bouncyCastle.performance.dsa

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import cz.monetplus.pqc.benchmark.utils.model.BcSig
import cz.monetplus.pqc.benchmark.utils.BcFactory
import cz.monetplus.pqc.benchmark.utils.BcSigManager
import java.security.SecureRandom

@RunWith(AndroidJUnit4::class)
class TestSigKeyGenerationBc {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val secureRandom = SecureRandom()
    private val bcFactory = BcFactory(secureRandom)

    private lateinit var sigManager: BcSigManager

    @Test fun benchmarkFalcon1024Keygen() = bench(BcSig.FALCON_1024)
    @Test fun benchmarkMayo3Keygen() = bench(BcSig.MAYO_3)
    @Test fun benchmarkMayo5Keygen() = bench(BcSig.MAYO_5)
    @Test fun benchmarkMlDsa65Keygen() = bench(BcSig.MLDSA_65)
    @Test fun benchmarkMlDsa87Keygen() = bench(BcSig.MLDSA_87)
    @Test fun benchmarkSnova24_5_5SskKeygen() = bench(BcSig.SNOVA_24_5_5_SSK)
    @Test fun benchmarkSnova24_5_5EskKeygen() = bench(BcSig.SNOVA_24_5_5_ESK)
    @Test fun benchmarkSnova37_8_4SskKeygen() = bench(BcSig.SNOVA_37_8_4_SSK)
    @Test fun benchmarkSnova37_8_4EskKeygen() = bench(BcSig.SNOVA_37_8_4_ESK)
    @Test fun benchmarkSnova49_11_3SskKeygen() = bench(BcSig.SNOVA_49_11_3_SSK)
    @Test fun benchmarkSnova49_11_3EskKeygen() = bench(BcSig.SNOVA_49_11_3_ESK)
    @Test fun benchmarkSnova56_25_2SskKeygen() = bench(BcSig.SNOVA_56_25_2_SSK)
    @Test fun benchmarkSnova56_25_2EskKeygen() = bench(BcSig.SNOVA_56_25_2_ESK)
    @Test fun benchmarkSnova29_6_5SskKeygen() = bench(BcSig.SNOVA_29_6_5_SSK)
    @Test fun benchmarkSnova29_6_5EskKeygen() = bench(BcSig.SNOVA_29_6_5_ESK)
    @Test fun benchmarkSnova60_10_4SskKeygen() = bench(BcSig.SNOVA_60_10_4_SSK)
    @Test fun benchmarkSnova60_10_4EskKeygen() = bench(BcSig.SNOVA_60_10_4_ESK)
    @Test fun benchmarkSlhdsaSha2_192fKeygen() = bench(BcSig.SLHDSA_SHA2_192F)
    @Test fun benchmarkSlhdsaSha2_192sKeygen() = bench(BcSig.SLHDSA_SHA2_192S)
    @Test fun benchmarkSlhdsaSha2_256fKeygen() = bench(BcSig.SLHDSA_SHA2_256F)
    @Test fun benchmarkSlhdsaSha2_256sKeygen() = bench(BcSig.SLHDSA_SHA2_256S)
    @Test fun benchmarkSlhdsaShake_192fKeygen() = bench(BcSig.SLHDSA_SHAKE_192F)
    @Test fun benchmarkSlhdsaShake_192sKeygen() = bench(BcSig.SLHDSA_SHAKE_192S)
    @Test fun benchmarkSlhdsaShake_256fKeygen() = bench(BcSig.SLHDSA_SHAKE_256F)
    @Test fun benchmarkSlhdsaShake_256sKeygen() = bench(BcSig.SLHDSA_SHAKE_256S)

    private fun bench(alg: BcSig) {
        sigManager = bcFactory.createSignatureManager(alg)
        benchmarkRule.measureRepeated {
            sigManager.generateKeyPair()
        }
    }
}