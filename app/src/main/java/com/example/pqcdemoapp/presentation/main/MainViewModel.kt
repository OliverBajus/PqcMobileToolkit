package com.example.pqcdemoapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aheaditec.functional.onLeft
import com.example.pqcdemoapp.domain.GetSupportedKemAlgorithms
import com.example.pqcdemoapp.domain.model.PqcLibrary
import com.example.pqcdemoapp.domain.GetSupportedSigAlgorithms
import com.example.pqcdemoapp.domain.RunKemFlow
import com.example.pqcdemoapp.domain.RunSigFlow
import com.example.pqcdemoapp.domain.model.AlgChoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSupportedSigAlgorithms: GetSupportedSigAlgorithms,
    private val getSupportedKemAlgorithms: GetSupportedKemAlgorithms,
    private val runKemFlow: RunKemFlow,
    private val runSigFlow: RunSigFlow
) : ViewModel() {
    private val _logText = MutableStateFlow("Ready to validate PQC...\n")
    val logText = _logText.asStateFlow()

    private val _selectedLibrary = MutableStateFlow(PqcLibrary.OQS)
    val selectedLibrary = _selectedLibrary.asStateFlow()

    private val _kemOptions = MutableStateFlow(listOf<AlgChoice>())
    val kemOptions = _kemOptions.asStateFlow()

    private val _sigOptions = MutableStateFlow(listOf<AlgChoice>())
    val sigOptions = _sigOptions.asStateFlow()

    private val _selectedKem = MutableStateFlow(_kemOptions.value.firstOrNull())
    val selectedKem = _selectedKem.asStateFlow()

    private val _selectedSig = MutableStateFlow(_sigOptions.value.firstOrNull())
    val selectedSig = _selectedSig.asStateFlow()

    private val _isInProgress = MutableStateFlow(false)
    val isInProgress = _isInProgress.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _kemOptions.value = getSupportedKemAlgorithms(_selectedLibrary.value)
            _sigOptions.value = getSupportedSigAlgorithms(_selectedLibrary.value)

            _selectedKem.value = _kemOptions.value.firstOrNull()

            _selectedSig.value = _sigOptions.value.firstOrNull()
        }
    }

    fun selectLibrary(lib: PqcLibrary) {
        appendLog("\n----- Library change ------\n")

        _selectedLibrary.value = lib

        viewModelScope.launch(Dispatchers.IO) {
            val newKem = getSupportedKemAlgorithms(_selectedLibrary.value)
            val newSig = getSupportedSigAlgorithms(_selectedLibrary.value)

            _kemOptions.value = newKem
            _sigOptions.value = newSig

            _selectedKem.value =
                _selectedKem.value?.let { prev -> newKem.firstOrNull { it.id == prev.id } }
                    ?: newKem.firstOrNull()

            _selectedSig.value =
                _selectedSig.value?.let { prev -> newSig.firstOrNull { it.id == prev.id } }
                    ?: newSig.firstOrNull()

            appendLog("Selected library: ${lib.displayName}")
            _selectedKem.value?.let {
                appendLog("KEM now: ${it.name}")
            }
            _selectedSig.value?.let {
                appendLog("SIG now: ${it.name}")
            }
        }
    }

    fun clearLogs() {
        _logText.value = "Logs cleared.\n"
    }

    fun selectKem(alg: AlgChoice) {
        _selectedKem.value = alg
        appendLog("Selected KEM: ${alg.name}")
    }

    fun selectSig(alg: AlgChoice) {
        _selectedSig.value = alg
        appendLog("Selected SIG: ${alg.name}")
    }

    fun runFullKemFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedKem.value?.let { kemAlg ->
                appendSeparator()
                appendLog("🚀 Starting ${kemAlg.name}...")

                _isInProgress.value = true
                val result = runKemFlow(RunKemFlow.Params(_selectedLibrary.value, kemAlg))
                    .onLeft {
                        appendLog("❌ KEM flow failed: ${it.l}")
                        _isInProgress.value = false
                        return@launch
                    }

                appendLog("1️⃣ KeyPair: ${nsToMs(result.keygenNs)} ms")
                appendLog("2️⃣ Encaps: ${nsToMs(result.encapsNs)} ms")
                appendLog("3️⃣ Decaps: ${nsToMs(result.decapsNs)} ms")
                if (result.ok) appendLog("✅ SHARED SECRETS MATCH!")
                else appendLog("❌ MISMATCH! Encryption failed.")
                _isInProgress.value = false
            } ?: run {
                appendLog("❌ No KEM selected.")
            }
        }
    }

    fun runFullSigFlow() {
        viewModelScope.launch(Dispatchers.Default) {
            _selectedSig.value?.let { sigAlg ->
                appendSeparator()
                appendLog("✍️ Starting ${sigAlg.name}...")

                _isInProgress.value = true
                val result = runSigFlow(RunSigFlow.Params(_selectedLibrary.value, sigAlg, "Thesis Proof".encodeToByteArray()))
                    .onLeft {
                        appendLog("❌ SIG flow failed: ${it.l}")
                        _isInProgress.value = false
                        return@launch
                    }

                appendLog("1️⃣ KeyPair: ${nsToMs(result.keygenNs)} ms")
                appendLog("2️⃣ Sign: ${nsToMs(result.signNs)} ms")
                appendLog("3️⃣ Verify: ${nsToMs(result.verifyNs)} ms")
                if (result.ok) appendLog("✅ SIGNATURE VALID!")
                else appendLog("❌ INVALID SIGNATURE.")
                _isInProgress.value = false
            } ?: run {
                appendLog("❌ No SIG selected.")
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
        String.Companion.format(Locale.US, "%.3f", ns / 1_000_000.0)
}