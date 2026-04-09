package cz.monetplus.pqc.benchmark.liboqs.tvla

import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.oliverbajus.liboqs_android.Oqs
import io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm
import io.github.oliverbajus.liboqs_android.api.model.SignatureAlgorithm
import com.google.common.truth.Truth.assertThat
import cz.monetplus.pqc.benchmark.utils.saveTimingsToCsv
import io.github.oliverbajus.liboqs_android.api.sig.model.SigKeypair
import io.github.oliverbajus.liboqs_android.api.sig.model.SigPrivateKey
import io.github.oliverbajus.liboqs_android.api.sig.model.SigPublicKey
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

    @Test
    fun test_SLH_DSA_SHA_192f() {
        sigAlg = PqcAlgorithm.Sig.SlhDsa3FastSha
        performTVLA_on_message()
        performTVLA_on_key()
    }

    private fun performTVLA_on_message() {
        Oqs.createSignatureTimingManager(sigAlg).use { signer ->
            val keypair = signer.generateKeyPair()

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
            repeat(WARMUP) { signer.timeSignNs(fixedMessage, keypair.private) }

            var fi = 0
            var ri = 0

            repeat(ITERATIONS) { idx ->
                if (schedule[idx]) {
                    fixedTimings[fi] = signer.timeSignNs(fixedMessage, keypair.private)
                    fi++
                } else {
                    randomTimings[ri] = signer.timeSignNs(randomMessages[ri], keypair.private)
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
        Oqs.createSignatureTimingManager(sigAlg).use { signer ->
            // Generate the fixed key
            val fixedKp = signer.generateKeyPair()

            val schedule = BooleanArray(ITERATIONS) { random.nextBoolean() }

            val trueCount = schedule.count { it }
            val falseCount = schedule.size - trueCount

            val fixedTimings = LongArray(trueCount)
            val randomTimings = LongArray(falseCount)

            // Warm-up (do both to stabilize JIT / caches a bit)
            Thread.sleep(100)
            repeat(WARMUP) {
                signer.timeSignNs(fixedMessage, fixedKp.private)
                signer.generateKeyPair()
            }

            var fi = 0
            var ri = 0

            repeat(ITERATIONS) { idx ->
                val randomKp = signer.generateKeyPair()
                val fixedKeyCopy = SigPrivateKey(fixedKp.private.bytes.clone())

                if (schedule[idx]) {
                    // always the same fixed key, assignment from array just for fairness
                    fixedTimings[fi] = signer.timeSignNs(fixedMessage, fixedKeyCopy)
                    fi++
                } else {
                    randomTimings[ri] = signer.timeSignNs(fixedMessage, randomKp.private)
                    ri++
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

    private fun performTVLA_on_key_pregen(pollSize: Int = 1000) {
        Oqs.createSignatureTimingManager(sigAlg).use { signer ->
            // Generate the fixed key
            val fixedKp = signer.generateKeyPair()

            val schedule = BooleanArray(ITERATIONS) { random.nextBoolean() }

            val trueCount = schedule.count { it }
            val falseCount = schedule.size - trueCount

            val fixedTimings = LongArray(trueCount)
            val randomTimings = LongArray(falseCount)

            //  unique random keys
            val randomKeyPool = Array(pollSize) { signer.generateKeyPair() }

            // array of references to that EXACT SAME fixed key for access fairness
            val fixedKeyPool = Array(pollSize) { SigKeypair(SigPublicKey(fixedKp.public.bytes.clone()),
                SigPrivateKey(fixedKp.private.bytes.clone()))  }

            // Warm-up
            Thread.sleep(100)
            repeat(WARMUP) { idx ->
                signer.timeSignNs(fixedMessage, fixedKeyPool[idx % fixedKeyPool.size].private)
                signer.timeSignNs(fixedMessage, randomKeyPool[idx % randomKeyPool.size].private)
            }

            var fi = 0
            var ri = 0

            repeat(ITERATIONS) { idx ->
                if (schedule[idx]) {
                    // always the same fixed key, assignment from array just for fairness
                    fixedTimings[fi] = signer.timeSignNs(fixedMessage, fixedKeyPool[fi % fixedKeyPool.size].private)
                    fi++
                } else {
                    randomTimings[ri] = signer.timeSignNs(fixedMessage, randomKeyPool[ri % randomKeyPool.size].private)
                    ri++
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

    companion object {
        private const val ITERATIONS = 500_000
        private const val WARMUP = 100
    }
}
