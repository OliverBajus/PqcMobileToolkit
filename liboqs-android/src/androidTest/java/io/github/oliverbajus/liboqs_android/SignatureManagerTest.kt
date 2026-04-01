package io.github.oliverbajus.liboqs_android

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm
import io.github.oliverbajus.liboqs_android.api.model.SignatureAlgorithm
import io.github.oliverbajus.liboqs_android.api.sig.SignatureManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignatureManagerTest {

    private val testMessage = "Hello, post-quantum world!".toByteArray()

    private fun withManager(alg: SignatureAlgorithm, block: (SignatureManager) -> Unit) {
        Oqs.createSignatureManager(alg).use { block(it) }
    }

    // ── Key Generation ──────────────────────────────────────────────

    @Test
    fun keyGeneration_producesNonEmptyKeys() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            val kp = mgr.generateKeyPair()
            assertTrue(kp.public.bytes.isNotEmpty())
            assertTrue(kp.private.bytes.isNotEmpty())
        }
    }

    @Test
    fun keyGeneration_publicKeyLengthMatchesDetails() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            val kp = mgr.generateKeyPair()
            assertEquals(
                mgr.signatureDetails.publicKeyLength,
                kp.public.bytes.size.toLong()
            )
        }
    }

    @Test
    fun keyGeneration_secretKeyLengthMatchesDetails() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            val kp = mgr.generateKeyPair()
            assertEquals(
                mgr.signatureDetails.secretKeyLength,
                kp.private.bytes.size.toLong()
            )
        }
    }

    @Test
    fun keyGeneration_producesUniqueKeys() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            val kp1 = mgr.generateKeyPair()
            val kp2 = mgr.generateKeyPair()
            assertFalse(kp1.public.bytes.contentEquals(kp2.public.bytes))
        }
    }

    @Test
    fun getPublicKey_nonNullBeforeKeygen() {
        // Sig implementation pre-allocates key buffers on construction
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            assertNotNull(mgr.getPublicKey())
        }
    }

    @Test
    fun getPublicKey_nonNullAfterKeygen() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            mgr.generateKeyPair()
            assertNotNull(mgr.getPublicKey())
        }
    }

    @Test
    fun getPrivateKey_nonNullBeforeKeygen() {
        // Sig implementation pre-allocates key buffers on construction
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            assertNotNull(mgr.getPrivateKey())
        }
    }

    @Test
    fun getPrivateKey_nonNullAfterKeygen() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            mgr.generateKeyPair()
            assertNotNull(mgr.getPrivateKey())
        }
    }

    // ── Sign / Verify ───────────────────────────────────────────────

    @Test
    fun sign_producesNonEmptySignature() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            mgr.generateKeyPair()
            val sig = mgr.sign(testMessage)
            assertTrue(sig.isNotEmpty())
        }
    }

    @Test
    fun signVerify_validSignatureVerifies() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            mgr.generateKeyPair()
            val sig = mgr.sign(testMessage)
            val valid = mgr.verify(testMessage, sig, mgr.getPublicKey()!!)
            assertTrue(valid)
        }
    }

    @Test
    fun signVerify_tamperedMessageFailsVerification() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            mgr.generateKeyPair()
            val sig = mgr.sign(testMessage)
            val tampered = "Tampered message".toByteArray()
            val valid = mgr.verify(tampered, sig, mgr.getPublicKey()!!)
            assertFalse(valid)
        }
    }

    @Test
    fun signVerify_tamperedSignatureFailsVerification() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            mgr.generateKeyPair()
            val sig = mgr.sign(testMessage)
            val tamperedSig = sig.copyOf()
            tamperedSig[0] = (tamperedSig[0].toInt() xor 0xFF).toByte()
            val valid = mgr.verify(testMessage, tamperedSig, mgr.getPublicKey()!!)
            assertFalse(valid)
        }
    }

    @Test
    fun sign_lengthWithinMaxLength() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            mgr.generateKeyPair()
            val sig = mgr.sign(testMessage)
            assertTrue(sig.size.toLong() <= mgr.signatureDetails.signatureMaxLength)
        }
    }

    // ── SigDetails ──────────────────────────────────────────────────

    @Test
    fun sigDetails_methodNameMatchesAlgorithm() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            assertEquals(PqcAlgorithm.Sig.MlDsa3.id, mgr.signatureDetails.methodName)
        }
    }

    @Test
    fun sigDetails_hasPositiveLengths() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            val d = mgr.signatureDetails
            assertTrue(d.publicKeyLength > 0)
            assertTrue(d.secretKeyLength > 0)
            assertTrue(d.signatureMaxLength > 0)
        }
    }

    @Test
    fun sigDetails_versionNonEmpty() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            assertTrue(mgr.signatureDetails.version.isNotEmpty())
        }
    }

    @Test
    fun sigDetails_nistLevelPositive() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            assertTrue(mgr.signatureDetails.claimedNistLevel > 0)
        }
    }

    @Test
    fun sigDetails_isEufCma() {
        withManager(PqcAlgorithm.Sig.MlDsa3) { mgr ->
            assertTrue(mgr.signatureDetails.isEufCma)
        }
    }

    // ── All Algorithms Round-Trip ───────────────────────────────────

    @Test
    fun allSigAlgorithms_fullRoundTrip() {
        for (alg in PqcAlgorithm.Sig.all) {
            withManager(alg) { mgr ->
                mgr.generateKeyPair()
                val sig = mgr.sign(testMessage)
                val valid = mgr.verify(testMessage, sig, mgr.getPublicKey()!!)
                assertTrue("Round-trip failed for ${alg.id}", valid)
            }
        }
    }
}
