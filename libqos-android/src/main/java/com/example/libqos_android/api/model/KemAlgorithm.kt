package com.example.libqos_android.api.model

sealed interface KemAlgorithm {
    val id: String
    val name: String
        get() = this.algoName()
}