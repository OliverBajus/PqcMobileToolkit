package com.example.pqcdemoapp.domain.model

import com.example.libqos_android.api.model.KemAlgorithm
import com.example.libqos_android.api.model.PqcAlgorithm
import com.example.libqos_android.api.model.SignatureAlgorithm

enum class PqcLibrary(val displayName: String) {
    OQS("liboqs (JNI)"),
    BC("Bouncy Castle (Java)")
}
