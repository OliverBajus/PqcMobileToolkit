package com.example.libqos_android.api.sig

import com.example.libqos_android.api.sig.model.SigPublicKey

interface SignatureTimingManager : SignatureManager {
    fun timeKeygenNs(): Long
    fun timeSignNs(message: ByteArray): Long
    fun timeVerifyNs(message: ByteArray, signature: ByteArray, publicKey: SigPublicKey): Long
}