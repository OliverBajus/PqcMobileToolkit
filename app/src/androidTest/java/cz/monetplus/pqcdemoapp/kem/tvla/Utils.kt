package com.example.pqcdemoapp.kem.tvla

import java.security.SecureRandom

fun makeInvalidCtByBitFlip(random: SecureRandom, validCt: ByteArray): ByteArray {
    val ct = validCt.clone()
    // flip a few random bits/bytes (tweak counts if you want)
    repeat(4) {
        val i = random.nextInt(ct.size)
        ct[i] = (ct[i].toInt() xor (1 shl random.nextInt(8))).toByte()
    }
    return ct
}

fun makeInvalidCtRandom(random: SecureRandom, length: Int): ByteArray {
    val ct = ByteArray(length)
    random.nextBytes(ct)
    return ct
}