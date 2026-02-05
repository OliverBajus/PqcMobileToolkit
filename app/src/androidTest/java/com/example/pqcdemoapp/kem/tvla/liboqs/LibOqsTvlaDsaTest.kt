package com.example.pqcdemoapp.kem.tvla.liboqs

import com.example.libqos_android.Signature
import com.example.pqcdemoapp.PqcConstants
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pqcdemoapp.saveTimingsToCsv
import kotlin.system.measureNanoTime
import com.google.common.truth.Truth.assertThat
import kotlin.compareTo

@RunWith(AndroidJUnit4::class)
class LibOqsTvlaDsaTest {

    private lateinit var client: Signature
    private val fixedMessage = "This is the message to sign to test TVLA on DSA PQC algorithms!".toByteArray()
    private val random = java.security.SecureRandom()

    private lateinit var algorithmName: String

    @Test
    fun validate() {
        client = Signature(PqcConstants.DSA.ALG_NAME_ML_DSA_3)
        val publicKey = client.generate_keypair()
        val signature = client.sign(fixedMessage)
        val result = client.verify(fixedMessage, signature, publicKey)
        assertThat(result).isTrue()
    }

    @Test
    fun test_ML_DSA_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_ML_DSA_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_ML_DSA_5() {
        algorithmName = PqcConstants.DSA.ALG_NAME_ML_DSA_5
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_SPHINCS_FAST_SHA_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_SPHINCS_FAST_SHA_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_SPHINCS_SMALL_SHA_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_SPHINCS_SMALL_SHA_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_SPHINCS_FAST_SHAKE_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_SPHINCS_FAST_SHAKE_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_SPHINCS_SMALL_SHAKE_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_SPHINCS_SMALL_SHAKE_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_FALCON_5() {
        algorithmName = PqcConstants.DSA.ALG_NAME_FALCON_5
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_MAYO_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_MAYO_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_MAYO_5() {
        algorithmName = PqcConstants.DSA.ALG_NAME_MAYO_5
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_CROSS_RSDP_FAST_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_CROSS_RSDP_FAST_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_CROSS_RSDPG_FAST_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_CROSS_RSDPG_FAST_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    private fun performTVLA_on_ciphertext() {
        client = Signature(algorithmName)
        client.generate_keypair()

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        repeat(100000) {
            val randomMessage = ByteArray(fixedMessage.size)
            random.nextBytes(randomMessage)

            if (random.nextBoolean()) {

                val time = measureNanoTime {
                    client.sign(fixedMessage)
                }
                fixedTimings.add(time)

            } else {

                val time = measureNanoTime {
                    client.sign(randomMessage)
                }
                randomTimings.add(time)

            }
        }

        client.dispose_sig()
        saveTimingsToCsv(fixedTimings, randomTimings, algorithmName, "LibOQS_DSA_message_TVLA")
    }

    private fun performTVLA_on_key() {
        // create fixed signature instance to ve used as fixed key
        client = Signature(algorithmName)
        client.generate_keypair()

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        repeat(100000) {
            // create new signature instance with newly generated keys
            val tempClient = Signature(algorithmName)
            tempClient.generate_keypair()

            if (random.nextBoolean()) {

                // reuse the same key for signing
                val time = measureNanoTime {
                    client.sign(fixedMessage)
                }
                fixedTimings.add(time)

            } else {

                // use random key for signing
                val time = measureNanoTime {
                    tempClient.sign(fixedMessage)
                }
                randomTimings.add(time)

            }
            tempClient.dispose_sig()
        }

        client.dispose_sig()
        saveTimingsToCsv(fixedTimings, randomTimings, algorithmName, "LibOQS_DSA_key_TVLA")
    }


    @Test
    fun testNativeTiming_SanityCheck() {
        // 1. Setup: Use a standard algorithm (e.g., ML-DSA-44 / Dilithium2)
        // Ensure this string matches one of the enabled algorithms in your build
        val algorithmName = "ML-DSA-44"
        val client = Signature(algorithmName)

        // 2. Generate Keys (Crucial! The wrapper needs 'this.secret_key_' initialized)
        client.generate_keypair()

        val message = "Sanity Check for C-Timer".toByteArray()

        // 3. Run the new Native Timing Method
        // This calls: Kotlin -> Java Wrapper -> JNI -> C (malloc -> timer -> math -> timer -> free)
        val timeInNanos = client.sign_with_timing(message)

        // 4. Print results to Logcat/Console
        println("---------------------------------------------------")
        println("SUCCESS: Native Timing Harness works!")
        println("Algorithm: $algorithmName")
        println("Execution Time: $timeInNanos ns")
        println("---------------------------------------------------")

        // 5. Assertions
        // Time must be positive
        println("Time should be > 0 = " + (timeInNanos > 0))

        // Time should be realistic (e.g., < 1 second for a single signature)
        // 1 second = 1,000,000,000 nanoseconds
        println("Time should be < 1s = "+(timeInNanos < 1_000_000_000) )
    }

    @Test
    fun compare_Native_vs_JavaWrapper_Overhead() {
        val algorithmName = PqcConstants.DSA.ALG_NAME_SPHINCS_FAST_SHA_3
        val client = Signature(algorithmName)
        client.generate_keypair()
        val message = "Benchmark payload to test JNI overhead".toByteArray()

        // Warm-up (important to trigger JIT and cache)
        repeat(100) { client.sign_with_timing(message); client.sign(message) }

        var nativeTotal = 0L
        var wrapperTotal = 0L
        val iterations = 1000

        repeat(iterations) {
            // 1. Measure Native (Pure Math)
            nativeTotal += client.sign_with_timing(message)

            // 2. Measure Wrapper (Math + JNI + Arrays + Java Objects)
            val tStart = System.nanoTime()
            client.sign(message) // The standard method
            val tEnd = System.nanoTime()
            wrapperTotal += (tEnd - tStart)
        }

        val avgNative = nativeTotal / iterations
        val avgWrapper = wrapperTotal / iterations
        val overhead = avgWrapper - avgNative
        val overheadPercent = (overhead.toDouble() / avgNative.toDouble()) * 100

        println("=== JNI OVERHEAD ANALYSIS ($algorithmName) ===")
        println("Avg Native Time (Math only): $avgNative ns")
        println("Avg Wrapper Time (Full stack): $avgWrapper ns")
        println("JNI/Java Overhead: $overhead ns (+${"%.2f".format(overheadPercent)}%)")
        println("============================================")
    }

    @Test
    fun generate_Chart_Data() {
        val algorithms = listOf(
            PqcConstants.DSA.ALG_NAME_ML_DSA_3,
            PqcConstants.DSA.ALG_NAME_FALCON_5,
            PqcConstants.DSA.ALG_NAME_MAYO_3,
            PqcConstants.DSA.ALG_NAME_SPHINCS_FAST_SHA_3
        )

        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            val client = Signature(algo)
            client.generate_keypair()
            val msg = ByteArray(32) { 0xAA.toByte() } // 32-byte dummy hash

            // Warmup
            repeat(50) { client.sign(msg) }

            var nativeTotal = 0L
            var wrapperTotal = 0L
            val iterations = 1000

            repeat(iterations) {
                nativeTotal += client.sign_with_timing(msg) // The new C function

                val t1 = System.nanoTime()
                client.sign(msg) // The standard Java function
                val t2 = System.nanoTime()
                wrapperTotal += (t2 - t1)
            }

            val avgNative = nativeTotal / iterations
            val avgWrapper = wrapperTotal / iterations
            val overheadAbs = avgWrapper - avgNative
            val overheadRel = (overheadAbs.toDouble() / avgNative.toDouble()) * 100

            // CSV OUTPUT FORMAT
            println("$algo,$avgNative,$avgWrapper,$overheadAbs,${"%.4f".format(overheadRel)}")
        }
    }
}
