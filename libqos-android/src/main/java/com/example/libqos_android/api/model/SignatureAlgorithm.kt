package com.example.libqos_android.api.model

sealed interface SignatureAlgorithm {
    val id: String
    val name: String
        get() = this.algoName()
}