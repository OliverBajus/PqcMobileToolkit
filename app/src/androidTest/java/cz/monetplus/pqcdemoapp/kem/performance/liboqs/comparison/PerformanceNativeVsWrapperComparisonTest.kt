package com.example.pqcdemoapp.kem.performance.liboqs.comparison

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.Oqs
import com.example.libqos_android.api.model.PqcAlgorithm
import com.example.libqos_android.api.model.KemAlgorithm
import com.example.libqos_android.api.model.SignatureAlgorithm
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class PerformanceNativeVsWrapperComparisonTest {

    @Test
    fun generate_Full_Architecture_Profile() {
        val algorithms: List<SignatureAlgorithm> = listOf(
            PqcAlgorithm.Sig.MlDsa3,
            PqcAlgorithm.Sig.Falcon5Padded,
            PqcAlgorithm.Sig.Mayo3,
            PqcAlgorithm.Sig.Sphincs3FastSha,
        )

        val iterations = 1000
        val warmup = 20

        // --------------------------------------------------------------------------------------------
        // BLOCK 1: KEY GENERATION ANALYSIS
        // --------------------------------------------------------------------------------------------
        println("=== SIG KEYGEN PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            Oqs.createSignatureTimingManager(algo).use { timing ->
                Oqs.createSignatureManager(algo).use { wrapper ->

                    // Warmup
                    repeat(warmup) {
                        timing.timeKeygenNs()
                        wrapper.generateKeyPair()
                    }

                    var nativeTotal = 0L
                    var wrapperTotal = 0L

                    repeat(iterations) {
                        nativeTotal += timing.timeKeygenNs()

                        val tStart = System.nanoTime()
                        wrapper.generateKeyPair()
                        val tEnd = System.nanoTime()
                        wrapperTotal += (tEnd - tStart)
                    }

                    printMetrics(algo.id, nativeTotal, wrapperTotal, iterations)
                }
            }
            Thread.sleep(50)
        }
        println("==============================\n")

        // --------------------------------------------------------------------------------------------
        // BLOCK 2: SIGNING ANALYSIS
        // --------------------------------------------------------------------------------------------
        println("=== SIG SIGN PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        val msg = ByteArray(32) { 0xAA.toByte() }

        for (algo in algorithms) {
            Oqs.createSignatureTimingManager(algo).use { timing ->
                Oqs.createSignatureManager(algo).use { wrapper ->

                    // Ensure both have keys
                    timing.generateKeyPair()
                    wrapper.generateKeyPair()

                    // Warmup
                    repeat(warmup) {
                        timing.timeSignNs(msg)
                        wrapper.sign(msg)
                    }

                    var nativeTotal = 0L
                    var wrapperTotal = 0L

                    repeat(iterations) {
                        nativeTotal += timing.timeSignNs(msg)

                        val tStart = System.nanoTime()
                        wrapper.sign(msg)
                        val tEnd = System.nanoTime()
                        wrapperTotal += (tEnd - tStart)
                    }

                    printMetrics(algo.id, nativeTotal, wrapperTotal, iterations)
                }
            }
            Thread.sleep(50)
        }
        println("==============================\n")

        // --------------------------------------------------------------------------------------------
        // BLOCK 3: VERIFICATION ANALYSIS
        // --------------------------------------------------------------------------------------------
        println("=== SIG VERIFY PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            Oqs.createSignatureTimingManager(algo).use { timing ->
                Oqs.createSignatureManager(algo).use { signer ->

                    val kp = signer.generateKeyPair()
                    val signature = signer.sign(msg)

                    // Warmup
                    repeat(warmup) {
                        timing.timeVerifyNs(msg, signature, kp.public)
                        signer.verify(msg, signature, kp.public) // wrapper verify on same instance is fine
                    }

                    var nativeTotal = 0L
                    var wrapperTotal = 0L

                    repeat(iterations) {
                        nativeTotal += timing.timeVerifyNs(msg, signature, kp.public)

                        val tStart = System.nanoTime()
                        signer.verify(msg, signature, kp.public)
                        val tEnd = System.nanoTime()
                        wrapperTotal += (tEnd - tStart)
                    }

                    printMetrics(algo.id, nativeTotal, wrapperTotal, iterations)
                }
            }
            Thread.sleep(50)
        }
    }

    @Test
    fun generate_KEM_Architecture_Profile() {
        val algorithms: List<KemAlgorithm> = listOf(
            PqcAlgorithm.Kem.MlKem3,
            PqcAlgorithm.Kem.Hqc3,
            PqcAlgorithm.Kem.FrodoKemAes3,
        )

        val iterations = 1000
        val warmup = 50

        // -------------------------------------------------------------------------
        // 1) KEYGEN
        // -------------------------------------------------------------------------
        println("=== KEM KEYGEN PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            Oqs.createKemTimingManager(algo).use { timing ->
                Oqs.createKemManager(algo).use { wrapper ->

                    repeat(warmup) {
                        timing.timeKeygenNs()
                        wrapper.generateKeyPair()
                    }

                    var nativeTotal = 0L
                    var wrapperTotal = 0L

                    repeat(iterations) {
                        nativeTotal += timing.timeKeygenNs()

                        val tStart = System.nanoTime()
                        wrapper.generateKeyPair()
                        val tEnd = System.nanoTime()
                        wrapperTotal += (tEnd - tStart)
                    }

                    printMetrics(algo.id, nativeTotal, wrapperTotal, iterations)
                }
            }
            Thread.sleep(50)
        }
        println("==============================\n")

        // -------------------------------------------------------------------------
        // 2) ENCAPS
        // -------------------------------------------------------------------------
        println("=== KEM ENCAPS PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            Oqs.createKemTimingManager(algo).use { timing ->
                Oqs.createKemManager(algo).use { wrapperServer ->
                    Oqs.createKemManager(algo).use { clientForPk ->

                        val kp = clientForPk.generateKeyPair()

                        repeat(warmup) {
                            timing.timeEncapsNs(kp.public)
                            wrapperServer.encapsulate(kp.public)
                        }

                        var nativeTotal = 0L
                        var wrapperTotal = 0L

                        repeat(iterations) {
                            nativeTotal += timing.timeEncapsNs(kp.public)

                            val tStart = System.nanoTime()
                            wrapperServer.encapsulate(kp.public)
                            val tEnd = System.nanoTime()
                            wrapperTotal += (tEnd - tStart)
                        }

                        printMetrics(algo.id, nativeTotal, wrapperTotal, iterations)
                    }
                }
            }
            Thread.sleep(50)
        }
        println("==============================\n")

        // -------------------------------------------------------------------------
        // 3) DECAPS
        // -------------------------------------------------------------------------
        println("=== KEM DECAPS PERFORMANCE ===")
        println("Algorithm,Native_ns,Wrapper_ns,Overhead_ns,Overhead_Percent")

        for (algo in algorithms) {
            Oqs.createKemTimingManager(algo).use { timing ->
                Oqs.createKemManager(algo).use { wrapperClient ->
                    Oqs.createKemManager(algo).use { server ->

                        // Ensure timing manager has a valid secret key
                        val kp = timing.generateKeyPair()

                        // ciphertext generated for that public key
                        val enc = server.encapsulate(kp.public)
                        val ct = enc.kemCiphertext

                        // Warmup
                        repeat(warmup) {
                            timing.timeDecapsNs(ct)
                            wrapperClient.decapsulate(ct) // NOTE: wrapperClient needs same SK to succeed
                        }

                        // IMPORTANT:
                        // wrapperClient must hold the same secret key as timing manager if you want correctness.
                        // If your KemManager cannot be constructed with an external secret key anymore,
                        // then wrapper decaps comparison must use timing manager’s decapsulate() instead.
                        //
                        // So we compare "native decaps time" vs "wrapper call overhead" on the SAME object:
                        //
                        var nativeTotal = 0L
                        var wrapperTotal = 0L

                        repeat(iterations) {
                            nativeTotal += timing.timeDecapsNs(ct)

                            val tStart = System.nanoTime()
                            // This should be the wrapper decapsulate call on the SAME instance that owns the key:
                            timing.decapsulate(ct) // if SignatureTimingManager extends Manager; otherwise remove
                            val tEnd = System.nanoTime()
                            wrapperTotal += (tEnd - tStart)
                        }

                        printMetrics(algo.id, nativeTotal, wrapperTotal, iterations)
                    }
                }
            }
            Thread.sleep(50)
        }
    }

    private fun printMetrics(algo: String, nativeTotal: Long, wrapperTotal: Long, iterations: Int) {
        val avgNative = nativeTotal / iterations
        val avgWrapper = wrapperTotal / iterations
        val overheadAbs = avgWrapper - avgNative
        val overheadRel = if (avgNative > 0) (overheadAbs.toDouble() / avgNative.toDouble()) * 100 else 0.0

        println(
            String.format(
                Locale.US,
                "%s,%d,%d,%d,%.4f",
                algo, avgNative, avgWrapper, overheadAbs, overheadRel
            )
        )
    }
}