package com.example.pqcdemoapp.kem.tvla.liboqs

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.Oqs
import com.example.libqos_android.api.model.PqcAlgorithm
import com.example.libqos_android.api.model.KemAlgorithm
import com.example.pqcdemoapp.saveTimingsToCsv
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.security.SecureRandom

@RunWith(AndroidJUnit4::class)
class LibOqsTvlaKemTest {

    private val random = SecureRandom()

    private lateinit var kemAlg: KemAlgorithm

    @Test
    fun validate() {
        kemAlg = PqcAlgorithm.Kem.MlKem3

        Oqs.createKemManager(kemAlg).use { client ->
            val kp = client.generateKeyPair()

            Oqs.createKemManager(kemAlg).use { server ->
                val enc1 = server.encapsulate(kp.public)
                val ss1 = client.decapsulate(enc1.kemCiphertext)
                assertThat(enc1.kemSharedSecret.bytes).isEqualTo(ss1.bytes)

                // reuse same client public key again (new encapsulation)
                val enc2 = server.encapsulate(kp.public)
                val ss2 = client.decapsulate(enc2.kemCiphertext)
                assertThat(enc2.kemSharedSecret.bytes).isEqualTo(ss2.bytes)
            }
        }
    }

    @Test
    fun test_ML_KEM_3() {
        kemAlg = PqcAlgorithm.Kem.MlKem3
        performTVLA_on_ciphertext()
    }

    @Test
    fun test_ML_KEM_5() {
        kemAlg = PqcAlgorithm.Kem.MlKem5
        performTVLA_on_ciphertext()
    }

    @Test
    fun test_HQC_3() {
        kemAlg = PqcAlgorithm.Kem.Hqc3
        performTVLA_on_ciphertext()
    }

    @Test
    fun test_HQC_5() {
        kemAlg = PqcAlgorithm.Kem.Hqc5
        performTVLA_on_ciphertext()
    }

    @Test
    fun test_FRODO_SHAKE_3() {
        kemAlg = PqcAlgorithm.Kem.FrodoKemShake3
        performTVLA_on_ciphertext()
    }

    @Test
    fun test_FRODO_AES_3() {
        kemAlg = PqcAlgorithm.Kem.FrodoKemAes3
        performTVLA_on_ciphertext()
    }

    private fun performTVLA_on_ciphertext() {
        // 1) Fixed client keypair (fixed secret key inside timing manager instance)
        Oqs.createKemTimingManager(kemAlg).use { client ->
            val kp = client.generateKeyPair() // returns public key to share with server

            // 2) Server used only to create ciphertexts (not timed here)
            Oqs.createKemManager(kemAlg).use { server ->
                val fixedCt = server.encapsulate(kp.public).kemCiphertext

                val schedule = BooleanArray(ITERATIONS) { random.nextBoolean() }
                val trueCount = schedule.count { it }
                val falseCount = schedule.size - trueCount

                val fixedTimings = LongArray(trueCount)
                val randomTimings = LongArray(falseCount)

                // Warm-up: decaps on the fixed ciphertext
                Thread.sleep(100)
                repeat(WARMUP) { client.timeDecapsNs(fixedCt) }

                var fi = 0
                var ri = 0

                repeat(ITERATIONS) { idx ->
                    if (schedule[idx]) {
                        fixedTimings[fi++] = client.timeDecapsNs(fixedCt)
                    } else {
                        // fresh random ciphertext under the same public key
                        val ct = server.encapsulate(kp.public).kemCiphertext
                        randomTimings[ri++] = client.timeDecapsNs(ct)
                    }
                }

                saveTimingsToCsv(
                    fixedTimings.asList(),
                    randomTimings.asList(),
                    kemAlg.id,
                    "LibOQS_KEM_ciphertext_TVLA"
                )
            }
        }
    }

    companion object {
        private const val ITERATIONS = 100_000
        private const val WARMUP = 100
    }
}