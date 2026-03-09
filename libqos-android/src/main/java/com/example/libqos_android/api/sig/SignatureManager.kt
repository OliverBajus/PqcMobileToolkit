package com.example.libqos_android.api.sig

import com.example.libqos_android.api.sig.model.SigDetails
import com.example.libqos_android.api.sig.model.SigKeypair
import com.example.libqos_android.api.sig.model.SigPrivateKey
import com.example.libqos_android.api.sig.model.SigPublicKey

interface SignatureManager : AutoCloseable {
    val signatureDetails: SigDetails

    fun generateKeyPair(): SigKeypair
    fun sign(message: ByteArray): ByteArray
    fun verify(message: ByteArray, signature: ByteArray, publicKey: SigPublicKey): Boolean

    fun getPublicKey() : SigPublicKey?
    fun getPrivateKey() : SigPrivateKey?
}