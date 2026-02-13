package com.example.libqos_android.utils

import com.example.libqos_android.Oqs.LIB_NAME
import java.util.Arrays

fun ByteArray.wipe() {
    Arrays.fill(this, 0.toByte())
}

fun loadNativeLibrary() {
    System.loadLibrary(LIB_NAME)
}
