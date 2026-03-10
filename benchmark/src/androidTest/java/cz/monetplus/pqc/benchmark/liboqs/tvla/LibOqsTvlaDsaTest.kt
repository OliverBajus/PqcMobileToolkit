package cz.monetplus.pqc.benchmark.liboqs.tvla

import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.Oqs
import com.example.libqos_android.api.model.PqcAlgorithm
import com.example.libqos_android.api.model.SignatureAlgorithm
import com.google.common.truth.Truth.assertThat
import cz.monetplus.pqc.benchmark.utils.saveTimingsToCsv
import java.security.SecureRandom

@RunWith(AndroidJUnit4::class)
class LibOqsTvlaDsaTest {

    private val fixedMessage =
        "This is the message to sign to test TVLA on DSA PQC algorithms!".toByteArray()

    private val random = SecureRandom()

    private lateinit var sigAlg: SignatureAlgorithm

    @Test
    fun validate() {
        sigAlg = PqcAlgorithm.Sig.MlDsa3

        Oqs.createSignatureManager(sigAlg).use { signer ->
            val kp = signer.generateKeyPair()
            val sig = signer.sign(fixedMessage)

            Oqs.createSignatureManager(sigAlg).use { verifier ->
                val ok = verifier.verify(fixedMessage, sig, kp.public)
                assertThat(ok).isTrue()
            }
        }
    }

    @Test
    fun test_ML_DSA_3() {
        sigAlg = PqcAlgorithm.Sig.MlDsa3
        performTVLA_on_message()
        performTVLA_on_key()
    }

    @Test
    fun test_FALCON_5() {
        sigAlg = PqcAlgorithm.Sig.Falcon5
        performTVLA_on_message()
        performTVLA_on_key()
    }

    @Test
    fun test_MAYO_3() {
        sigAlg = PqcAlgorithm.Sig.Mayo3
        performTVLA_on_message()
        performTVLA_on_key()
    }

    @Test
    fun test_CROSS_RSDPG_FAST_3() {
        sigAlg = PqcAlgorithm.Sig.Cross3RsdpgFast
        performTVLA_on_message()
        performTVLA_on_key()
    }

    @Test
    fun test_UOV_3() {
        sigAlg = PqcAlgorithm.Sig.Uov3
        performTVLA_on_message()
        performTVLA_on_key()
    }

    private fun performTVLA_on_message() {
        Oqs.createSignatureTimingManager(sigAlg).use { signer ->
            signer.generateKeyPair()

            val schedule = BooleanArray(ITERATIONS) { random.nextBoolean() }
            val trueCount = schedule.count { it }
            val falseCount = schedule.size - trueCount

            val randomMessages = Array(falseCount) {
                ByteArray(fixedMessage.size).also { random.nextBytes(it) }
            }

            val fixedTimings = LongArray(trueCount)
            val randomTimings = LongArray(falseCount)

            // Warm-up
            Thread.sleep(100)
            repeat(WARMUP) { signer.timeSignNs(fixedMessage) }

            var fi = 0
            var ri = 0

            repeat(ITERATIONS) { idx ->
                if (schedule[idx]) {
                    fixedTimings[fi++] = signer.timeSignNs(fixedMessage)
                } else {
                    randomTimings[ri] = signer.timeSignNs(randomMessages[ri])
                    ri++
                }
            }

            saveTimingsToCsv(
                fixedTimings.asList(),
                randomTimings.asList(),
                sigAlg.id,
                "LibOQS_DSA_message_TVLA"
            )
        }
      }

    private fun performTVLA_on_key() {
        // Fixed-key signer
        Oqs.createSignatureTimingManager(sigAlg).use { fixedSigner ->
            fixedSigner.generateKeyPair()

            Oqs.createSignatureTimingManager(sigAlg).use { randomSigner ->
                val schedule = BooleanArray(ITERATIONS) { random.nextBoolean() }
                val trueCount = schedule.count { it }
                val falseCount = schedule.size - trueCount

                val fixedTimings = LongArray(trueCount)
                val randomTimings = LongArray(falseCount)

                // Warm-up (do both to stabilize JIT / caches a bit)
                Thread.sleep(100)
                repeat(WARMUP) {
                    fixedSigner.timeSignNs(fixedMessage)
                    randomSigner.generateKeyPair()
                    randomSigner.timeSignNs(fixedMessage)
                }

                var fi = 0
                var ri = 0

                repeat(ITERATIONS) { idx ->
                    if (schedule[idx]) {
                        fixedTimings[fi++] = fixedSigner.timeSignNs(fixedMessage)
                    } else {
                        randomSigner.generateKeyPair()
                        randomTimings[ri++] = randomSigner.timeSignNs(fixedMessage)
                    }
                }

                saveTimingsToCsv(
                    fixedTimings.asList(),
                    randomTimings.asList(),
                    sigAlg.id,
                    "LibOQS_DSA_key_TVLA"
                )
            }
        }
    }

    companion object {
        private const val ITERATIONS = 100_000
        private const val WARMUP = 100
    }
}
