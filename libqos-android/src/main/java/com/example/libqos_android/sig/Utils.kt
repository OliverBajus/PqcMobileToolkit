package com.example.libqos_android.sig

import com.example.libqos_android.api.sig.SignatureManager
import com.example.libqos_android.api.sig.SignatureTimingManager
import com.example.libqos_android.api.model.SignatureAlgorithm

internal fun provideSignatureManager(sigAlgorithm: SignatureAlgorithm): SignatureManager =
    Signature(sigAlgorithm)

internal fun provideSignatureTimingManager(sigAlgorithm: SignatureAlgorithm): SignatureTimingManager =
    Signature(sigAlgorithm)
