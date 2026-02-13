package com.example.libqos_android

import com.example.libqos_android.api.model.KemAlgorithm
import com.example.libqos_android.api.kem.KemManager
import com.example.libqos_android.api.kem.KemTimingManager
import com.example.libqos_android.api.sig.SignatureManager
import com.example.libqos_android.api.sig.SignatureTimingManager
import com.example.libqos_android.api.model.SignatureAlgorithm
import com.example.libqos_android.kem.provideKemManager
import com.example.libqos_android.kem.provideKemTimingManager
import com.example.libqos_android.sig.provideSignatureManager
import com.example.libqos_android.sig.provideSignatureTimingManager
import com.example.libqos_android.utils.loadNativeLibrary

object Oqs {
    internal const val LIB_NAME = "oqs-jni"

    init {
        loadNativeLibrary()
    }

    fun createKemManager(kemAlgorithm: KemAlgorithm): KemManager =
        provideKemManager(kemAlgorithm)

    fun createKemTimingManager(kemAlgorithm: KemAlgorithm): KemTimingManager =
        provideKemTimingManager(kemAlgorithm)

    fun createSignatureManager(sigAlgorithm: SignatureAlgorithm): SignatureManager =
        provideSignatureManager(sigAlgorithm)

    fun createSignatureTimingManager(sigAlgorithm: SignatureAlgorithm): SignatureTimingManager =
        provideSignatureTimingManager(sigAlgorithm)
}