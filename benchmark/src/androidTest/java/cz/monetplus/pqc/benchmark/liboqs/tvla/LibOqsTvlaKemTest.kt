package cz.monetplus.pqc.benchmark.liboqs.tvla

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.Oqs
import com.example.libqos_android.api.kem.model.KemCiphertext
import com.example.libqos_android.api.model.PqcAlgorithm
import com.example.libqos_android.api.model.KemAlgorithm
import cz.monetplus.pqc.benchmark.utils.makeInvalidCtByBitFlip
import com.google.common.truth.Truth.assertThat
import cz.monetplus.pqc.benchmark.utils.saveTimingsToCsv
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
        performTVLA_on_ciphertext_fixed_vs_random()
        performTVLA_on_ciphertext_valid_vs_invalid()
    }

    @Test
    fun test_HQC_3_fixed_vs_random() {
        kemAlg = PqcAlgorithm.Kem.Hqc3
        performTVLA_on_ciphertext_fixed_vs_random()
    }

    @Test
    fun test_HQC_3_valid_vs_invalid() {
        kemAlg = PqcAlgorithm.Kem.Hqc3
        performTVLA_on_ciphertext_valid_vs_invalid()
    }

    @Test
    fun test_FRODO_SHAKE_3() {
        kemAlg = PqcAlgorithm.Kem.FrodoKemShake3
        performTVLA_on_ciphertext_fixed_vs_random()
        performTVLA_on_ciphertext_valid_vs_invalid()
    }

    private fun performTVLA_on_ciphertext_fixed_vs_random() {
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
                    val ct = server.encapsulate(kp.public).kemCiphertext
                    if (schedule[idx]) {
                        fixedTimings[fi] = client.timeDecapsNs(fixedCt)
                        fi++
                    } else {
                        randomTimings[ri] = client.timeDecapsNs(ct)
                        ri++
                    }
                }

                saveTimingsToCsv(
                    fixedTimings.asList(),
                    randomTimings.asList(),
                    kemAlg.id,
                    "LibOQS_KEM_ciphertext_TVLA_fixed_vs_random"
                )
            }
        }
    }

    private fun performTVLA_on_ciphertext_valid_vs_invalid() {
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
                    val valid = server.encapsulate(kp.public).kemCiphertext
                    val invalid = KemCiphertext(makeInvalidCtByBitFlip(random, valid.bytes))

                    if (schedule[idx]) {
                        fixedTimings[fi] = client.timeDecapsNs(valid)
                        fi++
                    } else {
                        randomTimings[ri] = client.timeDecapsNs(invalid)
                        ri++
                    }
                }

                saveTimingsToCsv(
                    fixedTimings.asList(),
                    randomTimings.asList(),
                    kemAlg.id,
                    "LibOQS_KEM_ciphertext_TVLA_valid_vs_invalid"
                )
            }
        }
    }

    companion object {
        private const val ITERATIONS = 100_000
        private const val WARMUP = 100
    }
}