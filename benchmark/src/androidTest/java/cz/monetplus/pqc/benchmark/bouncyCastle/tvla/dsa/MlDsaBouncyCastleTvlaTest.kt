package cz.monetplus.pqc.benchmark.bouncyCastle.tvla.dsa

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import cz.monetplus.pqc.benchmark.utils.SignerAsMessageSigner
import cz.monetplus.pqc.benchmark.utils.saveTimingsToCsv
import org.bouncycastle.crypto.params.ParametersWithRandom
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyGenerationParameters
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyPairGenerator
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters
import org.bouncycastle.pqc.crypto.mldsa.MLDSASigner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.SecureRandom

@RunWith(AndroidJUnit4::class)
class MlDsaBouncyCastleTvlaTest {

    private val fixedMessage =
        "This is the message to sign to test TVLA on DSA PQC algorithms!".toByteArray()

    private val random = SecureRandom()
    private val mlDsaParameters = MLDSAParameters.ml_dsa_65
    private val keyGenerator = MLDSAKeyPairGenerator()

    @Before
    fun setUp() {
        keyGenerator.init(MLDSAKeyGenerationParameters(random, mlDsaParameters))
    }

    /** Creates an ML-DSA signer wrapped as MessageSigner so that
     *  generateSignature(message) includes reset + update + sign,
     *  making timing comparable with liboqs timeSignNs(). */
    private fun createSigner(
        keyPair: org.bouncycastle.crypto.AsymmetricCipherKeyPair
    ): SignerAsMessageSigner {
        val signer = MLDSASigner()
        signer.init(true, ParametersWithRandom(keyPair.private, random))
        return SignerAsMessageSigner(signer)
    }

    @Test
    fun validate() {
        val keyPair = keyGenerator.generateKeyPair()
        val signer = createSigner(keyPair)
        val signature = signer.generateSignature(fixedMessage)

        val verifier = MLDSASigner()
        verifier.init(false, keyPair.public)
        verifier.update(fixedMessage, 0, fixedMessage.size)
        val shouldVerify = verifier.verifySignature(signature)
        assertThat(shouldVerify).isTrue()
    }

    @Test
    fun test_ML_DSA_3_message_TVLA() {
        performTVLA_on_message()
    }

    @Test
    fun test_ML_DSA_3_key_TVLA() {
        performTVLA_on_key()
    }

    private fun performTVLA_on_message() {
        val keyPair = keyGenerator.generateKeyPair()
        val signer = createSigner(keyPair)

        // Pre-generate schedule
        val schedule = BooleanArray(ITERATIONS) { random.nextBoolean() }
        val trueCount = schedule.count { it }
        val falseCount = schedule.size - trueCount

        // Pre-generate random messages (no SecureRandom in measurement loop)
        val randomMessages = Array(falseCount) {
            ByteArray(fixedMessage.size).also { random.nextBytes(it) }
        }

        // Pre-allocate primitive arrays (no boxing, no GC pressure)
        val fixedTimings = LongArray(trueCount)
        val randomTimings = LongArray(falseCount)

        // Warm-up: stabilize JIT
        Thread.sleep(100)
        repeat(WARMUP) { signer.generateSignature(fixedMessage) }

        var fi = 0
        var ri = 0

        repeat(ITERATIONS) { idx ->
            if (schedule[idx]) {
                val t0 = System.nanoTime()
                signer.generateSignature(fixedMessage)
                val t1 = System.nanoTime()
                fixedTimings[fi++] = t1 - t0
            } else {
                val t0 = System.nanoTime()
                signer.generateSignature(randomMessages[ri])
                val t1 = System.nanoTime()
                randomTimings[ri++] = t1 - t0
            }
        }

        saveTimingsToCsv(
            fixedTimings.asList(),
            randomTimings.asList(),
            mlDsaParameters.name,
            "BC_DSA_message_TVLA"
        )
    }

    private fun performTVLA_on_key() {
        // Fixed-key signer
        val fixedKeyPair = keyGenerator.generateKeyPair()
        val fixedSigner = createSigner(fixedKeyPair)

        // Pre-generate schedule
        val schedule = BooleanArray(ITERATIONS) { random.nextBoolean() }
        val trueCount = schedule.count { it }
        val falseCount = schedule.size - trueCount

        // Pre-allocate primitive arrays
        val fixedTimings = LongArray(trueCount)
        val randomTimings = LongArray(falseCount)

        // Warm-up both code paths
        Thread.sleep(100)
        repeat(WARMUP) {
            fixedSigner.generateSignature(fixedMessage)
            val tempKp = keyGenerator.generateKeyPair()
            val tempSigner = createSigner(tempKp)
            tempSigner.generateSignature(fixedMessage)
        }

        var fi = 0
        var ri = 0

        repeat(ITERATIONS) { idx ->
            // Keygen + init runs on EVERY iteration for cache symmetry:
            // both branches have the same memory/cache state before the timed call
            val tempKeyPair = keyGenerator.generateKeyPair()
            val tempSigner = createSigner(tempKeyPair)

            if (schedule[idx]) {
                val t0 = System.nanoTime()
                fixedSigner.generateSignature(fixedMessage)
                val t1 = System.nanoTime()
                fixedTimings[fi++] = t1 - t0
            } else {
                val t0 = System.nanoTime()
                tempSigner.generateSignature(fixedMessage)
                val t1 = System.nanoTime()
                randomTimings[ri++] = t1 - t0
            }
        }

        saveTimingsToCsv(
            fixedTimings.asList(),
            randomTimings.asList(),
            mlDsaParameters.name,
            "BC_DSA_key_TVLA"
        )
    }

    companion object {
        private const val ITERATIONS = 100_000
        private const val WARMUP = 200
    }
}
