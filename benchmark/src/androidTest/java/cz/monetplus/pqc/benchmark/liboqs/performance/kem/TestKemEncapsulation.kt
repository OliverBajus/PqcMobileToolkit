package cz.monetplus.pqc.benchmark.liboqs.performance.kem

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.Oqs
import com.example.libqos_android.api.kem.KemManager
import com.example.libqos_android.api.model.PqcAlgorithm
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestKemEncapsulation {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var kemManager:  KemManager

    @Test
    fun benchmarkMlKem3Encapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.MlKem3)
        testEncapsulation()
    }

    @Test
    fun benchmarkMlKem5Encapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.MlKem5)
        testEncapsulation()
    }

    @Test
    fun benchmarkHqc3Encapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.Hqc3)
        testEncapsulation()
    }

    @Test
    fun benchmarkHqc5Encapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.Hqc5)
        testEncapsulation()
    }

    @Test
    fun benchmarkFrodoAes3Encapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemAes3)
        testEncapsulation()
    }

    @Test
    fun benchmarkFrodoAes5Encapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemAes5)
        testEncapsulation()
    }

    @Test
    fun benchmarkFrodoShake3Encapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemShake3)
        testEncapsulation()
    }

    @Test
    fun benchmarkFrodoShake5Encapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemShake5)
        testEncapsulation()
    }

    @Test
    fun benchmarkMcEliece3Encapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.McEliece3)
        testEncapsulation()
    }

    @Test
    fun benchmarkMcEliece3fEncapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.McEliece3f)
        testEncapsulation()
    }

    private fun testEncapsulation() {
        val keyPair = kemManager.generateKeyPair()

        benchmarkRule.measureRepeated {
            kemManager.encapsulate(keyPair.public)
        }

        benchmarkRule.getState()
    }

    @After
    fun tearDown() {
        kemManager.close()
    }
}