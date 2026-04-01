package cz.monetplus.pqc.benchmark.liboqs.performance.kem

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.oliverbajus.liboqs_android.Oqs
import io.github.oliverbajus.liboqs_android.api.kem.KemManager
import io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestKemDecapsulation {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var kemManager:  KemManager

    @Test
    fun benchmarkMlKem3EDecapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.MlKem3)
        testDecapsulation()
    }

    @Test
    fun benchmarkMlKem5Decapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.MlKem5)
        testDecapsulation()
    }

    @Test
    fun benchmarkHqc3Decapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.Hqc3)
        testDecapsulation()
    }

    @Test
    fun benchmarkHqc5Decapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.Hqc5)
        testDecapsulation()
    }

    @Test
    fun benchmarkFrodoAes3Decapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemAes3)
        testDecapsulation()
    }

    @Test
    fun benchmarkFrodoAes5Decapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemAes5)
        testDecapsulation()
    }

    @Test
    fun benchmarkFrodoShake3Decapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemShake3)
        testDecapsulation()
    }

    @Test
    fun benchmarkFrodoShake5Encapsulation() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemShake5)
        testDecapsulation()
    }

    private fun testDecapsulation() {
        val keyPair = kemManager.generateKeyPair()
        val encapsulation = kemManager.encapsulate(keyPair.public)

        benchmarkRule.measureRepeated {
            kemManager.decapsulate(encapsulation.kemCiphertext)
        }
    }

    @After
    fun tearDown() {
        kemManager.close()
    }
}