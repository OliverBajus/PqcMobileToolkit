package com.example.libqos_android.api.kem

import com.example.libqos_android.api.kem.model.KemCiphertext
import com.example.libqos_android.api.kem.model.KemPublicKey

interface KemTimingManager : KemManager {
    fun timeKeygenNs(): Long
    fun timeEncapsNs(publicKey: KemPublicKey): Long
    fun timeDecapsNs(ciphertext: KemCiphertext): Long
}