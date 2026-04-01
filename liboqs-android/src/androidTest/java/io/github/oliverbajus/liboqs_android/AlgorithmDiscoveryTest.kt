package io.github.oliverbajus.liboqs_android

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm
import io.github.oliverbajus.liboqs_android.kem.KEMs
import io.github.oliverbajus.liboqs_android.sig.Sigs
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlgorithmDiscoveryTest {

    // ── KEM Discovery ───────────────────────────────────────────────

    @Test
    fun kems_supportedIdsNonEmpty() {
        assertTrue(KEMs.supportedIds().isNotEmpty())
    }

    @Test
    fun kems_enabledIdsNonEmpty() {
        assertTrue(KEMs.enabledIds().isNotEmpty())
    }

    @Test
    fun kems_supportedAlgorithmsNonEmpty() {
        assertTrue(KEMs.supportedAlgorithms().isNotEmpty())
    }

    @Test
    fun kems_enabledAlgorithmsNonEmpty() {
        assertTrue(KEMs.enabledAlgorithms().isNotEmpty())
    }

    @Test
    fun kems_mlKem768IsSupported() {
        assertTrue(KEMs.isSupported(PqcAlgorithm.Kem.MlKem3))
    }

    @Test
    fun kems_mlKem768IsEnabled() {
        assertTrue(KEMs.isEnabled(PqcAlgorithm.Kem.MlKem3))
    }

    @Test
    fun kems_enabledIsSubsetOfSupported() {
        val supported = KEMs.supportedIds().toSet()
        val enabled = KEMs.enabledIds()
        for (id in enabled) {
            assertTrue("Enabled KEM '$id' not in supported list", supported.contains(id))
        }
    }

    @Test
    fun kems_supportedIdsContainKnownAlgorithms() {
        val ids = KEMs.supportedIds()
        assertTrue(ids.contains(PqcAlgorithm.Kem.MlKem3.id))
        assertTrue(ids.contains(PqcAlgorithm.Kem.MlKem5.id))
    }

    @Test
    fun kems_supportedAlgorithmsWithUnknownIncludesMore() {
        val withUnknown = KEMs.supportedAlgorithms(includeUnknown = true)
        val withoutUnknown = KEMs.supportedAlgorithms(includeUnknown = false)
        assertTrue(withUnknown.size >= withoutUnknown.size)
    }

    @Test
    fun kems_enabledAlgorithmsCountMatchesEnabledIds() {
        val algCount = KEMs.enabledAlgorithms(includeUnknown = true).size
        val idCount = KEMs.enabledIds().size
        assertEquals(algCount, idCount)
    }

    // ── Sig Discovery ───────────────────────────────────────────────

    @Test
    fun sigs_supportedIdsNonEmpty() {
        assertTrue(Sigs.supportedIds().isNotEmpty())
    }

    @Test
    fun sigs_enabledIdsNonEmpty() {
        assertTrue(Sigs.enabledIds().isNotEmpty())
    }

    @Test
    fun sigs_supportedAlgorithmsNonEmpty() {
        assertTrue(Sigs.supportedAlgorithms().isNotEmpty())
    }

    @Test
    fun sigs_enabledAlgorithmsNonEmpty() {
        assertTrue(Sigs.enabledAlgorithms().isNotEmpty())
    }

    @Test
    fun sigs_mlDsa65IsSupported() {
        assertTrue(Sigs.isSupported(PqcAlgorithm.Sig.MlDsa3))
    }

    @Test
    fun sigs_mlDsa65IsEnabled() {
        assertTrue(Sigs.isEnabled(PqcAlgorithm.Sig.MlDsa3))
    }

    @Test
    fun sigs_enabledIsSubsetOfSupported() {
        val supported = Sigs.supportedIds().toSet()
        val enabled = Sigs.enabledIds()
        for (id in enabled) {
            assertTrue("Enabled Sig '$id' not in supported list", supported.contains(id))
        }
    }

    @Test
    fun sigs_supportedIdsContainKnownAlgorithms() {
        val ids = Sigs.supportedIds()
        assertTrue(ids.contains(PqcAlgorithm.Sig.MlDsa3.id))
        assertTrue(ids.contains(PqcAlgorithm.Sig.MlDsa5.id))
    }

    @Test
    fun sigs_supportedAlgorithmsWithUnknownIncludesMore() {
        val withUnknown = Sigs.supportedAlgorithms(includeUnknown = true)
        val withoutUnknown = Sigs.supportedAlgorithms(includeUnknown = false)
        assertTrue(withUnknown.size >= withoutUnknown.size)
    }

    @Test
    fun sigs_enabledAlgorithmsCountMatchesEnabledIds() {
        val algCount = Sigs.enabledAlgorithms(includeUnknown = true).size
        val idCount = Sigs.enabledIds().size
        assertEquals(algCount, idCount)
    }
}
