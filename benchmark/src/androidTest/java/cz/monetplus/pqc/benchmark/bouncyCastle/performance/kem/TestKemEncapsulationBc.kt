package cz.monetplus.pqc.benchmark.bouncyCastle.performance.kem

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import cz.monetplus.pqc.benchmark.utils.model.BcKem
import cz.monetplus.pqc.benchmark.utils.BcFactory
import cz.monetplus.pqc.benchmark.utils.BcKemManager
import java.security.SecureRandom

@RunWith(AndroidJUnit4::class)
class TestKemEncapsulationBc {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val secureRandom = SecureRandom()
    private val bcFactory = BcFactory(secureRandom)

    private lateinit var kemManager: BcKemManager

    @Test
    fun benchmarkMlKem3Encapsulation() = benchmarkDecap(BcKem.MLKEM_768)

    @Test
    fun benchmarkMlKem5Encapsulation() = benchmarkDecap(BcKem.MLKEM_1024)

    @Test
    fun benchmarkHqc3Encapsulation() = benchmarkDecap(BcKem.HQC_192)

    @Test
    fun benchmarkHqc5Encapsulation() = benchmarkDecap(BcKem.HQC_256)

    @Test
    fun benchmarkFrodoAes3Encapsulation() = benchmarkDecap(BcKem.FRODO_976_AES)

    @Test
    fun benchmarkFrodoAes5Encapsulation() = benchmarkDecap(BcKem.FRODO_1344_AES)

    @Test
    fun benchmarkFrodoShake3Encapsulation() = benchmarkDecap(BcKem.FRODO_976_SHAKE)

    @Test
    fun benchmarkFrodoShake5Encapsulation() = benchmarkDecap(BcKem.FRODO_1344_SHAKE)

    private fun benchmarkDecap(alg: BcKem) {
        kemManager = bcFactory.createKemManager(alg)

        val keyPair: AsymmetricCipherKeyPair = kemManager.generateKeyPair()

        benchmarkRule.measureRepeated {
            kemManager.encapsulate(keyPair)
        }
    }
}