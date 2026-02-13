package sk.bajuso.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.kem.KeyEncapsulation
import com.example.libqos_android.api.model.PqcConstants
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


    @Test
    fun benchmarkMcEliece3Decapsulation() {
        testEncapsulation(PqcConstants.KEM.ALG_NAME_MC_ELIECE_3)
    }

    private fun testEncapsulation(algName: String) {
        var count = 0
        client = KeyEncapsulation(algName)
        val keyPair = client.generate_keypair()
        val serverPair = client.encapSecretNative(keyPair)

        benchmarkRule.measureRepeated {
            client.decap_secret(serverPair.left)
            runWithTimingDisabled { count++ }
        }
    }


    @After
    fun tearDown() {
        client.dispose_KEM()
    }
}