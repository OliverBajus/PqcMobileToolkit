package cz.monetplus.pqc.benchmark.bouncyCastle.performance.kem

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import cz.monetplus.pqc.benchmark.utils.model.BcKem
import cz.monetplus.pqc.benchmark.utils.BcFactory
import cz.monetplus.pqc.benchmark.utils.BcKemManager
import java.security.SecureRandom

@RunWith(AndroidJUnit4::class)
class TestKemKeyGenerationBc {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val secureRandom = SecureRandom()
    private val bcFactory = BcFactory(secureRandom)

    private lateinit var kemManager: BcKemManager

    @Test
    fun benchmarkMlKem3KeyGeneration() = benchmarkDecap(BcKem.MLKEM_768)

    @Test
    fun benchmarkMlKem5KeyGeneration() = benchmarkDecap(BcKem.MLKEM_1024)

    @Test
    fun benchmarkHqc3KeyGeneration() = benchmarkDecap(BcKem.HQC_192)

    @Test
    fun benchmarkHqc5KeyGeneration() = benchmarkDecap(BcKem.HQC_256)

    @Test
    fun benchmarkFrodoAes3KeyGeneration() = benchmarkDecap(BcKem.FRODO_976_AES)

    @Test
    fun benchmarkFrodoAes5KeyGeneration() = benchmarkDecap(BcKem.FRODO_1344_AES)

    @Test
    fun benchmarkFrodoShake3KeyGeneration() = benchmarkDecap(BcKem.FRODO_976_SHAKE)

    @Test
    fun benchmarkFrodoShake5KeyGeneration() = benchmarkDecap(BcKem.FRODO_1344_SHAKE)

    private fun benchmarkDecap(alg: BcKem) {
        kemManager = bcFactory.createKemManager(alg)

        benchmarkRule.measureRepeated {
            kemManager.generateKeyPair()
        }
    }
}