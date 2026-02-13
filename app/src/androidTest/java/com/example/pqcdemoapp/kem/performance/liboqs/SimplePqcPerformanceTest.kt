/*
package com.example.pqcdemoapp.kem.performance.liboqs

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.kem.KeyEncapsulation
import com.example.libqos_android.sig.Signature
import com.example.pqcdemoapp.PqcConstants
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale
import kotlin.math.sqrt

*/
/**
 * Simple performance runner (NO BenchmarkRule).
 *
 * - Measures with System.nanoTime() and prints CSV.
 * - One place to change ITERATIONS / WARMUP / SLEEP.
 * - For each algorithm and operation, prints: avg, median, stdev, min, max (ns).
 *
 *//*

@RunWith(AndroidJUnit4::class)
class SimplePqcPerformanceTest {

    companion object {
        // Change these in ONE place
        private const val ITERATIONS = 1000
        private const val WARMUP = 50
        private const val SLEEP_BETWEEN_ALGOS_MS = 100L

        // If you want less memory, keep false (but then you only get avg)
        private const val COLLECT_ALL_SAMPLES = true
    }

    // --------------------------
    // DSA / Signatures
    // --------------------------
    @Test
    fun run_DSA_performance_profile() {
        val algorithms = listOf(
            PqcConstants.DSA.ALG_NAME_ML_DSA_3,
            PqcConstants.DSA.ALG_NAME_FALCON_PADDED_5,
            PqcConstants.DSA.ALG_NAME_MAYO_3,
            PqcConstants.DSA.ALG_NAME_SPHINCS_FAST_SHA_3
        )

        println("=== DSA PERFORMANCE (ns) ===")
        printCsvHeader()

        val msgSign = ByteArray(32) { 0xAA.toByte() }
        val msgVerify = ByteArray(32) { 0xBB.toByte() }

        for (algo in algorithms) {
            val sig = Signature(algo)

            // KEYGEN
            measureOperation(
                algo = algo,
                op = "keygen",
                warmup = { sig.generate_keypair() },
                measured = { sig.generate_keypair() }
            )

            // Need a keypair for sign/verify
            sig.generate_keypair()

            // SIGN
            measureOperation(
                algo = algo,
                op = "sign",
                warmup = { sig.sign(msgSign) },
                measured = { sig.sign(msgSign) }
            )

            // VERIFY setup (make a valid signature once)
            val pub = sig.export_public_key()
            val signature = sig.sign(msgVerify)

            // VERIFY
            measureOperation(
                algo = algo,
                op = "verify",
                warmup = { sig.verify(msgVerify, signature, pub) },
                measured = { sig.verify(msgVerify, signature, pub) }
            )

            sig.dispose_sig()
            SystemClock.sleep(SLEEP_BETWEEN_ALGOS_MS)
        }

        println("===========================\n")
    }

    // --------------------------
    // KEM
    // --------------------------
    @Test
    fun run_KEM_performance_profile() {
        val algorithms = listOf(
            PqcConstants.KEM.ALG_NAME_ML_KEM_3,
            PqcConstants.KEM.ALG_NAME_ML_KEM_5,
            PqcConstants.KEM.ALG_NAME_HQC_3,
            PqcConstants.KEM.ALG_NAME_HQC_5,
            PqcConstants.KEM.ALG_NAME_FRODO_AES_3,
            PqcConstants.KEM.ALG_NAME_FRODO_AES_5,
            PqcConstants.KEM.ALG_NAME_FRODO_SHAKE_3,
            PqcConstants.KEM.ALG_NAME_FRODO_SHAKE_5,
            PqcConstants.KEM.ALG_NAME_MC_ELIECE_3
        )

        println("=== KEM PERFORMANCE (ns) ===")
        printCsvHeader()

        for (algo in algorithms) {
            val kem = KeyEncapsulation(algo)

            // KEYGEN
            measureOperation(
                algo = algo,
                op = "keygen",
                warmup = { kem.generate_keypair() },
                measured = { kem.generate_keypair() }
            )

            // ENCAP setup: ensure we have a public key
            val pub = kem.generate_keypair()

            // ENCAP
            measureOperation(
                algo = algo,
                op = "encap",
                warmup = { kem.encapSecretNative(pub) },
                measured = { kem.encapSecretNative(pub) }
            )

            // DECAP setup: generate one ciphertext once, then decap that ciphertext repeatedly
            kem.generate_keypair()
            val pub2 = kem.export_public_key()
            val pair = kem.encapSecretNative(pub2)
            val ciphertext = pair.left

            // DECAP
            measureOperation(
                algo = algo,
                op = "decap",
                warmup = { kem.decap_secret(ciphertext) },
                measured = { kem.decap_secret(ciphertext) }
            )

            kem.dispose_KEM()
            SystemClock.sleep(SLEEP_BETWEEN_ALGOS_MS)
        }

        println("===========================\n")
    }

    // --------------------------
    // Measurement helpers
    // --------------------------
    private fun printCsvHeader() {
        if (COLLECT_ALL_SAMPLES) {
            println("Algorithm,Operation,Iterations,Avg_ns,Median_ns,StdDev_ns,Min_ns,Max_ns")
        } else {
            println("Algorithm,Operation,Iterations,Avg_ns")
        }
    }

    private fun measureOperation(
        algo: String,
        op: String,
        warmup: () -> Unit,
        measured: () -> Unit
    ) {
        // Warmup
        repeat(WARMUP) { warmup() }

        if (!COLLECT_ALL_SAMPLES) {
            var total = 0L
            repeat(ITERATIONS) {
                val t0 = System.nanoTime()
                measured()
                val t1 = System.nanoTime()
                total += (t1 - t0)
            }
            val avg = total / ITERATIONS
            println(csvLineSimple(algo, op, ITERATIONS, avg))
            return
        }

        val samples = LongArray(ITERATIONS)
        for (i in 0 until ITERATIONS) {
            val t0 = System.nanoTime()
            measured()
            val t1 = System.nanoTime()
            samples[i] = (t1 - t0)
        }

        val stats = computeStats(samples)
        println(
            if (COLLECT_ALL_SAMPLES)
                csvLineFull(algo, op, ITERATIONS, stats)
            else
                csvLineSimple(algo, op, ITERATIONS, stats.avg)
        )
    }

    private data class Stats(
        val avg: Long,
        val median: Long,
        val stdDev: Long,
        val min: Long,
        val max: Long
    )

    private fun computeStats(samples: LongArray): Stats {
        var sum = 0L
        var min = Long.MAX_VALUE
        var max = Long.MIN_VALUE
        for (v in samples) {
            sum += v
            if (v < min) min = v
            if (v > max) max = v
        }
        val avg = sum / samples.size

        val sorted = samples.clone()
        sorted.sort()
        val median = sorted[sorted.size / 2]

        // std dev (population)
        var acc = 0.0
        for (v in samples) {
            val d = (v - avg).toDouble()
            acc += d * d
        }
        val std = sqrt(acc / samples.size).toLong()

        return Stats(avg = avg, median = median, stdDev = std, min = min, max = max)
    }

    private fun csvLineSimple(algo: String, op: String, iters: Int, avg: Long): String =
        String.format(Locale.US, "%s,%s,%d,%d", algo, op, iters, avg)

    private fun csvLineFull(algo: String, op: String, iters: Int, s: Stats): String =
        String.format(
            Locale.US,
            "%s,%s,%d,%d,%d,%d,%d,%d",
            algo, op, iters, s.avg, s.median, s.stdDev, s.min, s.max
        )
}*/
