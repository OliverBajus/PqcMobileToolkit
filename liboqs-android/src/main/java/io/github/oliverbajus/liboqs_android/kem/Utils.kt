package io.github.oliverbajus.liboqs_android.kem

import io.github.oliverbajus.liboqs_android.api.kem.KemManager
import io.github.oliverbajus.liboqs_android.api.kem.KemTimingManager
import io.github.oliverbajus.liboqs_android.api.model.KemAlgorithm

internal fun provideKemManager(kemAlgorithm: KemAlgorithm): KemManager =
    KeyEncapsulation(kemAlgorithm)

internal fun provideKemTimingManager(kemAlgorithm: KemAlgorithm): KemTimingManager =
    KeyEncapsulation(kemAlgorithm)
