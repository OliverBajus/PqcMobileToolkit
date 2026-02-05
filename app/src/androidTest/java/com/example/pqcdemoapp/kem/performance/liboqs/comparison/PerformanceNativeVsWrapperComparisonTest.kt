package com.example.pqcdemoapp.kem.performance.liboqs.comparison

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.KeyEncapsulation
import com.example.libqos_android.Signature
import com.example.pqcdemoapp.PqcConstants
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PerformanceNativeVsWrapperComparisonTest {

    @Test
    fun generate_Full_Architecture_Profile() {
        val algorithms = listOf(
            PqcConstants.DSA.ALG_NAME_ML_DSA_3,
            PqcConstants.DSA.ALG_NAME_FALCON_PADDED_5,
            PqcConstants.DSA.ALG_NAME_MAYO_3,
            PqcConstants.DSA.ALG_NAME_SPHINCS_FAST_SHA_3
        )

        val iterations = 1000

        // --------------------------------------------------------------------------------------------
        // BLOCK 1: KEY GENERATION ANALYSIS
        // --------------------------------------------------------------------------------------------
        println("=== KEYGEN PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            val client = Signature(algo)

            // Warmup
            repeat(10) {
                client.generate_keypair_with_timing() // Native
                client.generate_keypair()             // Wrapper
            }

            var nativeTotal = 0L
            var wrapperTotal = 0L

            repeat(iterations) {
                // 1. Measure Native (Pure C Malloc/Free)
                nativeTotal += client.generate_keypair_with_timing()

                // 2. Measure Wrapper (Updates Java Object State)
                val tStart = System.nanoTime()
                client.generate_keypair()
                val tEnd = System.nanoTime()
                wrapperTotal += (tEnd - tStart)
            }

            printMetrics(algo, nativeTotal, wrapperTotal, iterations)
            client.dispose_sig()
            Thread.sleep(100)
        }
        println("==========================\n")

        // --------------------------------------------------------------------------------------------
        // BLOCK 2: SIGNING ANALYSIS
        // --------------------------------------------------------------------------------------------
        println("=== SIGNING PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            val client = Signature(algo)
            client.generate_keypair() // Ensure keys exist
            val msg = ByteArray(32) { 0xAA.toByte() }

            // Warmup
            repeat(10) { client.sign_with_timing(msg); client.sign(msg) }

            var nativeTotal = 0L
            var wrapperTotal = 0L

            repeat(iterations) {
                nativeTotal += client.sign_with_timing(msg)

                val tStart = System.nanoTime()
                client.sign(msg)
                val tEnd = System.nanoTime()
                wrapperTotal += (tEnd - tStart)
            }

            printMetrics(algo, nativeTotal, wrapperTotal, iterations)
            client.dispose_sig()
            Thread.sleep(100)
        }
        println("==========================\n")


        // --------------------------------------------------------------------------------------------
        // BLOCK 3: VERIFICATION ANALYSIS
        // --------------------------------------------------------------------------------------------
        println("=== VERIFICATION PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            val client = Signature(algo)
            client.generate_keypair()
            val pubKey = client.export_public_key()
            val msg = ByteArray(32) { 0xBB.toByte() }
            val signature = client.sign(msg) // Generate valid signature

            // Warmup
            repeat(10) {
                client.verify_with_timing(msg, signature, pubKey)
                client.verify(msg, signature, pubKey)
            }

            var nativeTotal = 0L
            var wrapperTotal = 0L

            repeat(iterations) {
                nativeTotal += client.verify_with_timing(msg, signature, pubKey)

                val tStart = System.nanoTime()
                client.verify(msg, signature, pubKey)
                val tEnd = System.nanoTime()
                wrapperTotal += (tEnd - tStart)
            }

            printMetrics(algo, nativeTotal, wrapperTotal, iterations)
            client.dispose_sig()
            Thread.sleep(100)
        }
    }

    @Test
    fun generate_KEM_Architecture_Profile() {
        val algorithms = listOf(
            PqcConstants.KEM.ALG_NAME_ML_KEM_3,
            PqcConstants.KEM.ALG_NAME_HQC_3,
            PqcConstants.KEM.ALG_NAME_FRODO_AES_3,
        )

        val iterations = 1000

        // -------------------------------------------------------------------------
        // 1. KEYGEN
        // -------------------------------------------------------------------------
        println("=== KEM KEYGEN PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            val kem = KeyEncapsulation(algo)

            // Warmup
            repeat(50) { kem.generate_keypair(); kem.generate_keypair_with_timing() }

            var nativeTotal = 0L
            var wrapperTotal = 0L

            repeat(iterations) {
                // Warmup CPU
                kem.generate_keypair()

                nativeTotal += kem.generate_keypair_with_timing()

                val tStart = System.nanoTime()
                kem.generate_keypair()
                val tEnd = System.nanoTime()
                wrapperTotal += (tEnd - tStart)
            }
            printMetrics(algo, nativeTotal, wrapperTotal, iterations)
            kem.dispose_KEM()
            Thread.sleep(100)
        }
        println("==============================\n")

        Thread.sleep(100)
        // -------------------------------------------------------------------------
        // 2. ENCAPSULATION
        // -------------------------------------------------------------------------
        println("=== KEM ENCAPS PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            val kem = KeyEncapsulation(algo)
            val pubKey = kem.generate_keypair() // We need a valid key to encapsulate

            repeat(50) { kem.encap_secret(pubKey); kem.encap_secret_with_timing(pubKey) }

            var nativeTotal = 0L
            var wrapperTotal = 0L

            repeat(iterations) {
                kem.encap_secret(pubKey) // CPU Warmup

                nativeTotal += kem.encap_secret_with_timing(pubKey)

                val tStart = System.nanoTime()
                kem.encap_secret(pubKey)
                val tEnd = System.nanoTime()
                wrapperTotal += (tEnd - tStart)
            }
            printMetrics(algo, nativeTotal, wrapperTotal, iterations)
            kem.dispose_KEM()
            Thread.sleep(100)
        }
        println("==============================\n")

        // -------------------------------------------------------------------------
        // 3. DECAPSULATION
        // -------------------------------------------------------------------------
        println("=== KEM DECAPS PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            val kem = KeyEncapsulation(algo)
            // Setup: Generate Key + Valid Ciphertext
            kem.generate_keypair()
            val pubKey = kem.export_public_key()
            val secKey = kem.export_secret_key()
            val pair = kem.encap_secret(pubKey)
            val ciphertext = pair.left

            repeat(50) {
                kem.decap_secret(ciphertext)
                kem.decap_secret_with_timing(ciphertext, secKey)
            }

            var nativeTotal = 0L
            var wrapperTotal = 0L

            repeat(iterations) {
                kem.decap_secret(ciphertext) // CPU Warmup

                nativeTotal += kem.decap_secret_with_timing(ciphertext, secKey)

                val tStart = System.nanoTime()
                kem.decap_secret(ciphertext)
                val tEnd = System.nanoTime()
                wrapperTotal += (tEnd - tStart)
            }
            printMetrics(algo, nativeTotal, wrapperTotal, iterations)
            kem.dispose_KEM()
            Thread.sleep(100)
        }
    }

    private fun printMetrics(algo: String, nativeTotal: Long, wrapperTotal: Long, iterations: Int) {
        val avgNative = nativeTotal / iterations
        val avgWrapper = wrapperTotal / iterations
        val overheadAbs = avgWrapper - avgNative

        val overheadRel = if (avgNative > 0) (overheadAbs.toDouble() / avgNative.toDouble()) * 100 else 0.0


        println(
            String.format(
                java.util.Locale.US,
                "%s,%d,%d,%d,%.4f",
                algo, avgNative, avgWrapper, overheadAbs, overheadRel
            )
        )
    }
}