package com.example.pqcdemoapp.kem.tvla.bc.kem

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pqcdemoapp.saveTimingsToCsv
import com.google.common.truth.Truth.assertThat
import org.bouncycastle.pqc.crypto.hqc.HQCKEMExtractor
import org.bouncycastle.pqc.crypto.hqc.HQCKEMGenerator
import org.bouncycastle.pqc.crypto.hqc.HQCKeyGenerationParameters
import org.bouncycastle.pqc.crypto.hqc.HQCKeyPairGenerator
import org.bouncycastle.pqc.crypto.hqc.HQCParameters
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.SecureRandom
import kotlin.system.measureNanoTime


@RunWith(AndroidJUnit4::class)
class HqcBouncyCastleTvlaTest {

    private val random = SecureRandom()
    private val hqcParameters = HQCParameters.hqc192
    private val keyGenerator = HQCKeyPairGenerator()

    @Before
    fun setUp() {
        keyGenerator.init(HQCKeyGenerationParameters(random, hqcParameters))
    }

    @Test
    fun validate() {
        val keyPair = keyGenerator.generateKeyPair()
        val generator = HQCKEMGenerator(random)

        val fixedEncaps = generator.generateEncapsulated(keyPair.public)
        val kemExtractor = HQCKEMExtractor(keyPair.private as HQCPrivateKeyParameters)
        val decapsulatedKey = kemExtractor.extractSecret(fixedEncaps.encapsulation)

        assertThat(fixedEncaps.secret).isEqualTo(decapsulatedKey)
    }

    @Test
    fun test_HQC_3_message_TVLA() {
        val keyPair = keyGenerator.generateKeyPair()
        val generator = HQCKEMGenerator(random)

        val fixedEncaps = generator.generateEncapsulated(keyPair.public)
        val kemExtractor = HQCKEMExtractor(keyPair.private as HQCPrivateKeyParameters)

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        repeat(100000) {
            val randomEncaps = generator.generateEncapsulated(keyPair.public)

            if (random.nextBoolean()) {
                val time = measureNanoTime {
                    kemExtractor.extractSecret(fixedEncaps.encapsulation)
                }
                fixedTimings.add(time)
            } else {
                val time = measureNanoTime {
                    kemExtractor.extractSecret(randomEncaps.encapsulation)
                }
                randomTimings.add(time)
            }
        }
        saveTimingsToCsv(randomTimings, randomTimings, hqcParameters.name, "BC_KEM_TVLA_ciphertext")
        saveTimingsToCsv(fixedTimings, randomTimings, hqcParameters.name, "BC_KEM_TVLA_ciphertext")
    }
}