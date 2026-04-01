package io.github.oliverbajus.liboqs_android.utils

import io.github.oliverbajus.liboqs_android.Oqs.LIB_NAME
import java.util.Arrays

internal fun ByteArray.wipe() {
    Arrays.fill(this, 0.toByte())
}

internal fun loadNativeLibrary() {
    System.loadLibrary(LIB_NAME)
}
