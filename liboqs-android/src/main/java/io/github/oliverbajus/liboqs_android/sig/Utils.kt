package io.github.oliverbajus.liboqs_android.sig

import io.github.oliverbajus.liboqs_android.api.sig.SignatureManager
import io.github.oliverbajus.liboqs_android.api.sig.SignatureTimingManager
import io.github.oliverbajus.liboqs_android.api.model.SignatureAlgorithm

internal fun provideSignatureManager(sigAlgorithm: SignatureAlgorithm): SignatureManager =
    Signature(sigAlgorithm)

internal fun provideSignatureTimingManager(sigAlgorithm: SignatureAlgorithm): SignatureTimingManager =
    Signature(sigAlgorithm)
