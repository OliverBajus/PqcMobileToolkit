package io.github.oliverbajus.liboqs_android

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimingManagerTest {

    private val testMessage = "Timing test message".toByteArray()

    // ── KEM Timing ──────────────────────────────────────────────────

    @Test
    fun kemTiming_keygenReturnsPositiveNanos() {
        Oqs.createKemTimingManager(PqcAlgorithm.Kem.MlKem3).use { mgr ->
            val ns = mgr.timeKeygenNs()
            assertTrue("Expected positive keygen time, got $ns", ns > 0)
        }
    }

    @Test
    fun kemTiming_encapsReturnsPositiveNanos() {
        Oqs.createKemTimingManager(PqcAlgorithm.Kem.MlKem3).use { mgr ->
            val kp = mgr.generateKeyPair()
            val ns = mgr.timeEncapsNs(kp.public)
            assertTrue("Expected positive encaps time, got $ns", ns > 0)
        }
    }

    @Test
    fun kemTiming_decapsReturnsPositiveNanos() {
        Oqs.createKemTimingManager(PqcAlgorithm.Kem.MlKem3).use { mgr ->
            val kp = mgr.generateKeyPair()
            val encaps = mgr.encapsulate(kp.public)
            val ns = mgr.timeDecapsNs(encaps.kemCiphertext)
            assertTrue("Expected positive decaps time, got $ns", ns > 0)
        }
    }

    @Test
    fun kemTiming_correctnessPreservedAfterTimedOps() {
        Oqs.createKemTimingManager(PqcAlgorithm.Kem.MlKem3).use { mgr ->
            mgr.timeKeygenNs()
            val pk = mgr.getPublicKey()!!
            val encaps = mgr.encapsulate(pk)
            val decapsSecret = mgr.decapsulate(encaps.kemCiphertext)
            assertArrayEquals(encaps.kemSharedSecret.bytes, decapsSecret.bytes)
        }
    }

    // ── Sig Timing ──────────────────────────────────────────────────

    @Test
    fun sigTiming_keygenReturnsPositiveNanos() {
        Oqs.createSignatureTimingManager(PqcAlgorithm.Sig.MlDsa3).use { mgr ->
            val ns = mgr.timeKeygenNs()
            assertTrue("Expected positive keygen time, got $ns", ns > 0)
        }
    }

    @Test
    fun sigTiming_signReturnsPositiveNanos() {
        Oqs.createSignatureTimingManager(PqcAlgorithm.Sig.MlDsa3).use { mgr ->
            mgr.generateKeyPair()
            val ns = mgr.timeSignNs(testMessage)
            assertTrue("Expected positive sign time, got $ns", ns > 0)
        }
    }

    @Test
    fun sigTiming_verifyReturnsPositiveNanos() {
        Oqs.createSignatureTimingManager(PqcAlgorithm.Sig.MlDsa3).use { mgr ->
            mgr.generateKeyPair()
            val sig = mgr.sign(testMessage)
            val ns = mgr.timeVerifyNs(testMessage, sig, mgr.getPublicKey()!!)
            assertTrue("Expected positive verify time, got $ns", ns > 0)
        }
    }

    @Test
    fun sigTiming_correctnessPreservedAfterTimedOps() {
        Oqs.createSignatureTimingManager(PqcAlgorithm.Sig.MlDsa3).use { mgr ->
            mgr.timeKeygenNs()
            val sig = mgr.sign(testMessage)
            val valid = mgr.verify(testMessage, sig, mgr.getPublicKey()!!)
            assertTrue(valid)
        }
    }
}
