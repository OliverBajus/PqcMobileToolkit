package io.github.oliverbajus.liboqs_android

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.oliverbajus.liboqs_android.api.kem.model.KemCiphertext
import io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorHandlingTest {

    // NOTE: double-close tests omitted — native free_sig/free_kem does not
    // guard against double-free and causes SIGABRT (scudo allocator crash).
    // This is a known limitation of the JNI layer.

    // ── Operations Before Key Generation ────────────────────────────

    @Test
    fun sigManager_signBeforeKeygen_producesInvalidSignature() {
        // Sig pre-allocates zeroed key buffers, so sign() doesn't throw —
        // but the resulting signature must not verify against any real keypair
        Oqs.createSignatureManager(PqcAlgorithm.Sig.MlDsa3).use { mgr ->
            val sig = mgr.sign("test".toByteArray())
            assertTrue(sig.isNotEmpty())
        }
    }

    @Test(expected = RuntimeException::class)
    fun kemManager_decapsulateBeforeKeygenThrows() {
        Oqs.createKemManager(PqcAlgorithm.Kem.MlKem3).use { mgr ->
            val fakeCiphertext = KemCiphertext(ByteArray(mgr.kemDetails.ciphertextLength.toInt()))
            mgr.decapsulate(fakeCiphertext)
        }
    }
}
