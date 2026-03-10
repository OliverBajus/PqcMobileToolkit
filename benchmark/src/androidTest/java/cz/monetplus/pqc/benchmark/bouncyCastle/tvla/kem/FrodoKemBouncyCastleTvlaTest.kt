package cz.monetplus.pqc.benchmark.bouncyCastle.tvla.kem

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import cz.monetplus.pqc.benchmark.utils.makeInvalidCtByBitFlip
import cz.monetplus.pqc.benchmark.utils.saveTimingsToCsv
import org.bouncycastle.pqc.crypto.frodo.FrodoKEMExtractor
import org.bouncycastle.pqc.crypto.frodo.FrodoKEMGenerator
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyGenerationParameters
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyPairGenerator
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.SecureRandom

@RunWith(AndroidJUnit4::class)
class FrodoKemBouncyCastleTvlaTest {

    private val random = SecureRandom()
    private val frodoParameters = FrodoParameters.frodokem976shake
    private val keyGenerator = FrodoKeyPairGenerator()

    @Before
    fun setUp() {
        keyGenerator.init(FrodoKeyGenerationParameters(random, frodoParameters))
    }

    @Test
    fun validate() {
        val keyPair = keyGenerator.generateKeyPair()
        val generator = FrodoKEMGenerator(random)

        val encapsResult = generator.generateEncapsulated(keyPair.public)
        val kemExtractor = FrodoKEMExtractor(keyPair.private as FrodoPrivateKeyParameters)
        val decapsulatedKey = kemExtractor.extractSecret(encapsResult.encapsulation)

        assertThat(encapsResult.secret).isEqualTo(decapsulatedKey)
    }

    @Test
    fun test_FRODO_SHAKE_3_ciphertext_fixed_vs_random() {
        performTVLA_on_ciphertext_fixed_vs_random()
    }

    @Test
    fun test_FRODO_SHAKE_3_ciphertext_valid_vs_invalid() {
        performTVLA_on_ciphertext_valid_vs_invalid()
    }

    private fun performTVLA_on_ciphertext_fixed_vs_random() {
        val keyPair = keyGenerator.generateKeyPair()
        val generator = FrodoKEMGenerator(random)
        val kemExtractor = FrodoKEMExtractor(keyPair.private as FrodoPrivateKeyParameters)

        val fixedCt = generator.generateEncapsulated(keyPair.public).encapsulation

        // Pre-generate schedule
        val schedule = BooleanArray(ITERATIONS) { random.nextBoolean() }
        val trueCount = schedule.count { it }
        val falseCount = schedule.size - trueCount

        // Pre-allocate primitive arrays (no boxing, no GC pressure)
        val fixedTimings = LongArray(trueCount)
        val randomTimings = LongArray(falseCount)

        // Warm-up: stabilize JIT
        Thread.sleep(100)
        repeat(WARMUP) { kemExtractor.extractSecret(fixedCt) }

        var fi = 0
        var ri = 0

        repeat(ITERATIONS) { idx ->
            // Fresh encapsulation every iteration for cache/memory consistency
            val randomCt = generator.generateEncapsulated(keyPair.public).encapsulation

            if (schedule[idx]) {
                val t0 = System.nanoTime()
                kemExtractor.extractSecret(fixedCt)
                val t1 = System.nanoTime()
                fixedTimings[fi++] = t1 - t0
            } else {
                val t0 = System.nanoTime()
                kemExtractor.extractSecret(randomCt)
                val t1 = System.nanoTime()
                randomTimings[ri++] = t1 - t0
            }
        }

        saveTimingsToCsv(
            fixedTimings.asList(),
            randomTimings.asList(),
            frodoParameters.name,
            "BC_KEM_ciphertext_TVLA_fixed_vs_random"
        )
    }

    private fun performTVLA_on_ciphertext_valid_vs_invalid() {
        val keyPair = keyGenerator.generateKeyPair()
        val generator = FrodoKEMGenerator(random)
        val kemExtractor = FrodoKEMExtractor(keyPair.private as FrodoPrivateKeyParameters)

        // Pre-generate schedule
        val schedule = BooleanArray(ITERATIONS) { random.nextBoolean() }
        val trueCount = schedule.count { it }
        val falseCount = schedule.size - trueCount

        // Pre-allocate primitive arrays
        val fixedTimings = LongArray(trueCount)
        val randomTimings = LongArray(falseCount)

        // Warm-up
        val warmCt = generator.generateEncapsulated(keyPair.public).encapsulation
        Thread.sleep(100)
        repeat(WARMUP) { kemExtractor.extractSecret(warmCt) }

        var fi = 0
        var ri = 0

        repeat(ITERATIONS) { idx ->
            val validCt = generator.generateEncapsulated(keyPair.public).encapsulation
            val invalidCt = makeInvalidCtByBitFlip(random, validCt)

            if (schedule[idx]) {
                val t0 = System.nanoTime()
                kemExtractor.extractSecret(validCt)
                val t1 = System.nanoTime()
                fixedTimings[fi++] = t1 - t0
            } else {
                val t0 = System.nanoTime()
                kemExtractor.extractSecret(invalidCt)
                val t1 = System.nanoTime()
                randomTimings[ri++] = t1 - t0
            }
        }

        saveTimingsToCsv(
            fixedTimings.asList(),
            randomTimings.asList(),
            frodoParameters.name,
            "BC_KEM_ciphertext_TVLA_valid_vs_invalid"
        )
    }

    companion object {
        private const val ITERATIONS = 100_000
        private const val WARMUP = 200
    }
}
