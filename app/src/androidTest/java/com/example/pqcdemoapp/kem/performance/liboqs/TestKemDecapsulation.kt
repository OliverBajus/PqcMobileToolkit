package com.example.pqcdemoapp.kem.performance.liboqs

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.KeyEncapsulation
import com.example.pqcdemoapp.PqcConstants
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestKemDecapsulation {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var client: KeyEncapsulation

    @Test
    fun benchmarkMlKem3Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_ML_KEM_3)
    }

    @Test
    fun benchmarkMlKem5Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_ML_KEM_5)
    }

    @Test
    fun benchmarkHqc3Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_HQC_3)
    }

    @Test
    fun benchmarkHqc5Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_HQC_5)
    }

    @Test
    fun benchmarkBike3Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_BIKE_3)
    }

    @Test
    fun benchmarkBike5Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_BIKE_5)
    }

    @Test
    fun benchmarkFrodoAes3Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_FRODO_AES_3)
    }

    @Test
    fun benchmarkFrodoAes5Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_FRODO_AES_5)
    }

    @Test
    fun benchmarkFrodoShake3Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_FRODO_SHAKE_3)
    }

    @Test
    fun benchmarkFrodoShake5Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_FRODO_SHAKE_5)
    }

    private fun testEncapsulation(algName: String) {
        var count = 0
        client = KeyEncapsulation(algName)
        val keyPair = client.generate_keypair()
        val serverPair = client.encap_secret(keyPair)

        benchmarkRule.measureRepeated {
            client.decap_secret(serverPair.left)
            runWithTimingDisabled { count++ }
        }

        println("$algName test iterations: $count")
    }


    @After
    fun tearDown() {
        client.dispose_KEM()
    }
}