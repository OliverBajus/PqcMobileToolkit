package io.github.oliverbajus.liboqs_android

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.oliverbajus.liboqs_android.api.kem.KemManager
import io.github.oliverbajus.liboqs_android.api.model.KemAlgorithm
import io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KemManagerTest {

    private fun withManager(alg: KemAlgorithm, block: (KemManager) -> Unit) {
        Oqs.createKemManager(alg).use { block(it) }
    }

    // ── Key Generation ──────────────────────────────────────────────

    @Test
    fun keyGeneration_producesNonEmptyKeys() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            val kp = mgr.generateKeyPair()
            assertTrue(kp.public.bytes.isNotEmpty())
        }
    }

    @Test
    fun keyGeneration_lengthsMatchDetails() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            val kp = mgr.generateKeyPair()
            assertEquals(mgr.kemDetails.publicKeyLength, kp.public.bytes.size.toLong())
        }
    }

    @Test
    fun keyGeneration_producesUniqueKeys() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            val kp1 = mgr.generateKeyPair()
            val kp2 = mgr.generateKeyPair()
            assertFalse(kp1.public.bytes.contentEquals(kp2.public.bytes))
        }
    }

    @Test
    fun getPublicKey_notNullBeforeKeygen() {
        // Kem implementation pre-allocates key buffers on construction
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            assertNotNull(mgr.getPublicKey())
        }
    }

    @Test
    fun getPublicKey_nonNullAfterKeygen() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            mgr.generateKeyPair()
            assertNotNull(mgr.getPublicKey())
        }
    }

    @Test
    fun getPrivateKey_notNullBeforeKeygen() {
        // Kem implementation pre-allocates key buffers on construction
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            assertNotNull(mgr.getPrivateKey())
        }
    }

    // ── Encapsulation / Decapsulation ───────────────────────────────

    @Test
    fun encapsDecaps_sharedSecretsMatch() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            val kp = mgr.generateKeyPair()
            val encaps = mgr.encapsulate(kp.public)
            val decapsSecret = mgr.decapsulate(encaps.kemCiphertext)
            assertArrayEquals(encaps.kemSharedSecret.bytes, decapsSecret.bytes)
        }
    }

    @Test
    fun encapsDecaps_sharedSecretLengthMatchesDetails() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            val kp = mgr.generateKeyPair()
            val encaps = mgr.encapsulate(kp.public)
            assertEquals(
                mgr.kemDetails.sharedSecretLength,
                encaps.kemSharedSecret.bytes.size.toLong()
            )
        }
    }

    @Test
    fun encapsDecaps_ciphertextLengthMatchesDetails() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            val kp = mgr.generateKeyPair()
            val encaps = mgr.encapsulate(kp.public)
            assertEquals(
                mgr.kemDetails.ciphertextLength,
                encaps.kemCiphertext.bytes.size.toLong()
            )
        }
    }

    @Test
    fun encapsDecaps_differentCiphertextsPerEncaps() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            val kp = mgr.generateKeyPair()
            val encaps1 = mgr.encapsulate(kp.public)
            val encaps2 = mgr.encapsulate(kp.public)
            assertFalse(encaps1.kemCiphertext.bytes.contentEquals(encaps2.kemCiphertext.bytes))
        }
    }

    // ── KemDetails ──────────────────────────────────────────────────

    @Test
    fun kemDetails_methodNameMatchesAlgorithm() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            assertEquals(PqcAlgorithm.Kem.MlKem3.id, mgr.kemDetails.methodName)
        }
    }

    @Test
    fun kemDetails_hasPositiveLengths() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            val d = mgr.kemDetails
            assertTrue(d.publicKeyLength > 0)
            assertTrue(d.secretKeyLength > 0)
            assertTrue(d.ciphertextLength > 0)
            assertTrue(d.sharedSecretLength > 0)
        }
    }

    @Test
    fun kemDetails_versionNonEmpty() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            assertTrue(mgr.kemDetails.version.isNotEmpty())
        }
    }

    @Test
    fun kemDetails_nistLevelPositive() {
        withManager(PqcAlgorithm.Kem.MlKem3) { mgr ->
            assertTrue(mgr.kemDetails.claimedNistLevel > 0)
        }
    }

    // ── All Algorithms Round-Trip ───────────────────────────────────

    @Test
    fun allKemAlgorithms_fullRoundTrip() {
        for (alg in PqcAlgorithm.Kem.all) {
            withManager(alg) { mgr ->
                val kp = mgr.generateKeyPair()
                val encaps = mgr.encapsulate(kp.public)
                val decapsSecret = mgr.decapsulate(encaps.kemCiphertext)
                assertArrayEquals(
                    "Round-trip failed for ${alg.id}",
                    encaps.kemSharedSecret.bytes,
                    decapsSecret.bytes
                )
            }
        }
    }
}
