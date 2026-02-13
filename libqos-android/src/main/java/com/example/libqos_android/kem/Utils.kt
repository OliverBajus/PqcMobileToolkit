package com.example.libqos_android.kem

import com.example.libqos_android.api.kem.KemManager
import com.example.libqos_android.api.kem.KemTimingManager
import com.example.libqos_android.api.model.KemAlgorithm

internal fun provideKemManager(kemAlgorithm: KemAlgorithm): KemManager =
    KeyEncapsulation(kemAlgorithm)

internal fun provideKemTimingManager(kemAlgorithm: KemAlgorithm): KemTimingManager =
    KeyEncapsulation(kemAlgorithm)
