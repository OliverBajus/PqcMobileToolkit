package cz.monetplus.pqc.benchmark.liboqs.performance.kem

import androidx.benchmark.ExperimentalBenchmarkConfigApi
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
import kotlin.system.measureNanoTime

@RunWith(AndroidJUnit4::class)
class TestKemKeyGeneration {
    @OptIn(ExperimentalBenchmarkConfigApi::class)
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var kemManager:  KemManager

    @Test
    fun benchmarkMlKem3KeyGeneration() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.MlKem3)
        testKeyGeneration()
    }

    @Test
    fun benchmarkMlKem3EDecapsulationManual() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.MlKem3)

        repeat(1000) {
            kemManager.generateKeyPair()
        }

        val timings = LongArray(100_000)

        repeat(100_000) { indx ->
            timings[indx] = measureNanoTime {
                kemManager.generateKeyPair()
            }
        }

        println("Median is " + timings.asList().median())
    }

    fun List<Long>.median(): Double {
        if (isEmpty()) return Double.NaN
        val s = sorted()
        val mid = s.size / 2
        return if (s.size % 2 == 1) s[mid].toDouble()
        else (s[mid - 1] + s[mid]).toDouble() / 2.0
    }

    @Test
    fun benchmarkMlKem5KeyGeneration() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.MlKem5)
        testKeyGeneration()
    }

    @Test
    fun benchmarkHqc3KeyGeneration() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.Hqc3)
        testKeyGeneration()
    }

    @Test
    fun benchmarkHqc5KeyGeneration() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.Hqc5)
        testKeyGeneration()
    }

    @Test
    fun benchmarkFrodoAes3KeyGeneration() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemAes3)
        testKeyGeneration()
    }

    @Test
    fun benchmarkFrodoAes5KeyGeneration() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemAes5)
        testKeyGeneration()
    }

    @Test
    fun benchmarkFrodoShake3KeyGeneration() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemShake3)
        testKeyGeneration()
    }

    @Test
    fun benchmarkFrodoShake5KeyGeneration() {
        kemManager = Oqs.createKemManager(PqcAlgorithm.Kem.FrodoKemShake5)
        testKeyGeneration()
    }

    private fun testKeyGeneration() {
        var count = 0

        benchmarkRule.measureRepeated {
            kemManager.generateKeyPair()
            runWithTimingDisabled { count++ }
        }

        println("Test iterations: $count")
    }

    @After
    fun tearDown() {
        kemManager.close()
    }
}
