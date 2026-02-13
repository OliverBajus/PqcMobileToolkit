package com.example.libqos_android.api.kem

import com.example.libqos_android.api.kem.model.KemCiphertext
import com.example.libqos_android.api.kem.model.KemDetails
import com.example.libqos_android.api.kem.model.KemEncapsulationResult
import com.example.libqos_android.api.kem.model.KemKeypair
import com.example.libqos_android.api.kem.model.KemPrivateKey
import com.example.libqos_android.api.kem.model.KemPublicKey
import com.example.libqos_android.api.kem.model.KemSharedSecret
import java.lang.AutoCloseable

interface KemManager : AutoCloseable {
    val kemDetails: KemDetails

    fun generateKeyPair(): KemKeypair
    fun encapsulate(kemPublicKey: KemPublicKey): KemEncapsulationResult
    fun decapsulate(kemCiphertext: KemCiphertext): KemSharedSecret

    fun getPublicKey() : KemPublicKey?
    fun getPrivateKey() : KemPrivateKey?
}