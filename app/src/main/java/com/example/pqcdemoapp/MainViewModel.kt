// PqcViewModel.kt
package com.example.pqcdemoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libqos_android.Oqs
import com.example.libqos_android.api.model.PqcAlgorithm
import com.example.libqos_android.api.model.KemAlgorithm
import com.example.libqos_android.api.model.SignatureAlgorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.system.measureNanoTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PqcViewModel : ViewModel() {

    private val _logText = MutableStateFlow("Ready to validate PQC...\n")
    val logText = _logText.asStateFlow()

    // NEW: options for pickers (swap to enabled-only lists if you want)
    val kemOptions: List<KemAlgorithm> = PqcAlgorithm.Kem.all

    val sigOptions: List<SignatureAlgorithm> = PqcAlgorithm.Sig.all

    private val _selectedKem = MutableStateFlow(kemOptions.first())
    val selectedKem = _selectedKem.asStateFlow()

    private val _selectedSig = MutableStateFlow(sigOptions.first())
    val selectedSig = _selectedSig.asStateFlow()

    fun selectKem(alg: KemAlgorithm) {
        _selectedKem.value = alg
        appendLog("Selected KEM: ${alg.name}")
    }

    fun selectSig(alg: SignatureAlgorithm) {
        _selectedSig.value = alg
        appendLog("Selected SIG: ${alg.name}")
    }

    fun clearLogs() {
        _logText.value = "Logs cleared.\n"
    }

    fun onAppStart() {
        appendSeparator()
        appendLog("App started.")
        appendLog("Default KEM: ${_selectedKem.value.name}")
        appendLog("Default SIG: ${_selectedSig.value.name}")
    }

    fun runFullKemFlow() {
        val kemAlg = _selectedKem.value
        viewModelScope.launch(Dispatchers.Default) {
            appendSeparator()
            appendLog("🚀 Starting ${kemAlg.name}...")

            try {
                Oqs.createKemManager(kemAlg).use { client ->
                    // 1) Keypair
                    lateinit var keypair: com.example.libqos_android.api.kem.model.KemKeypair
                    val keypairTimeNs = measureNanoTime { keypair = client.generateKeyPair() }
                    appendLog("1️⃣ KeyPair: ${nsToMs(keypairTimeNs)} ms")

                    // 2) Encaps
                    Oqs.createKemManager(kemAlg).use { server ->
                        lateinit var encaps: com.example.libqos_android.api.kem.model.KemEncapsulationResult
                        val encapsTimeNs = measureNanoTime { encaps = server.encapsulate(keypair.public) }
                        appendLog("2️⃣ Encaps: ${nsToMs(encapsTimeNs)} ms")

                        // 3) Decaps
                        lateinit var ss: com.example.libqos_android.api.kem.model.KemSharedSecret
                        val decapsTimeNs = measureNanoTime { ss = client.decapsulate(encaps.kemCiphertext) }
                        appendLog("3️⃣ Decaps: ${nsToMs(decapsTimeNs)} ms")

                        // 4) Validate
                        val ok = ss.bytes.contentEquals(encaps.kemSharedSecret.bytes)
                        if (ok) appendLog("✅ SHARED SECRETS MATCH!")
                        else appendLog("❌ MISMATCH! Encryption failed.")
                    }
                }
            } catch (t: Throwable) {
                appendLog("❌ KEM flow failed: ${t.message ?: t::class.java.simpleName}")
            }
        }
    }

    fun runFullSigFlow() {
        val sigAlg = _selectedSig.value
        viewModelScope.launch(Dispatchers.Default) {
            appendSeparator()
            appendLog("✍️ Starting ${sigAlg.name}...")

            val message = "Thesis Proof".encodeToByteArray()

            try {
                Oqs.createSignatureManager(sigAlg).use { signer ->
                    // 1) Keypair
                    lateinit var keypair: com.example.libqos_android.api.sig.model.SigKeypair
                    val keygenNs = measureNanoTime { keypair = signer.generateKeyPair() }
                    appendLog("1️⃣ KeyPair: ${nsToMs(keygenNs)} ms")

                    // 2) Sign
                    lateinit var signature: ByteArray
                    val signNs = measureNanoTime { signature = signer.sign(message) }
                    appendLog("2️⃣ Sign: ${nsToMs(signNs)} ms")

                    // 3) Verify (separate instance)
                    Oqs.createSignatureManager(sigAlg).use { verifier ->
                        var isValid = false
                        val verifyNs = measureNanoTime { isValid = verifier.verify(message, signature, keypair.public) }
                        appendLog("3️⃣ Verify: ${nsToMs(verifyNs)} ms")

                        if (isValid) appendLog("✅ SIGNATURE VALID!")
                        else appendLog("❌ INVALID SIGNATURE.")
                    }
                }
            } catch (t: Throwable) {
                appendLog("❌ SIG flow failed: ${t.message ?: t::class.java.simpleName}")
            }
        }
    }

    private fun appendSeparator() {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        appendLog("\n--- Run at $time ---")
    }

    private fun appendLog(msg: String) {
        _logText.update { it + msg + "\n" }
    }

    private fun nsToMs(ns: Long): String =
        String.format(Locale.US, "%.3f", ns / 1_000_000.0)
}