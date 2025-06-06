package com.example.pqcdemoapp.kem.tvla.bc.dsa

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pqcdemoapp.saveTimingsToCsv
import org.bouncycastle.crypto.params.ParametersWithRandom
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyGenerationParameters
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyPairGenerator
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters
import org.bouncycastle.pqc.crypto.mldsa.MLDSASigner
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSASigner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.SecureRandom
import kotlin.system.measureNanoTime

@RunWith(AndroidJUnit4::class)
class MlDsaBouncyCastleTvlaTest {

    private val message = "This is the message to sign to test TVLA on DSA PQC algorithms!".toByteArray()
    private val random = SecureRandom()
    private val mlDsaParameters = MLDSAParameters.ml_dsa_65
    private val keyGenerator = MLDSAKeyPairGenerator()

    @Before
    fun setUp() {
        keyGenerator.init(MLDSAKeyGenerationParameters(random, mlDsaParameters))
    }

    @Test
    fun validate() {
        val keyPair = keyGenerator.generateKeyPair()
        val signer  = MLDSASigner()

        signer.init(true, ParametersWithRandom(keyPair.private, random))
        signer.update(message, 0, message.size)
        val signature = signer.generateSignature()
        val a = signer.verifySignature(signature)
        println(a)
    }

    @Test
    fun test_ML_DSA_3_messageTVLA() {
        val keyPair = keyGenerator.generateKeyPair()
        val signer  = MLDSASigner()

        signer.init(true, ParametersWithRandom(keyPair.private, random))

        warmUp(signer)

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        repeat(100000) {
            val coin = random.nextInt(2)

            val randomMessage = ByteArray(message.size)
            random.nextBytes(randomMessage)

            if (coin == 0) {
                signer.update(message, 0, message.size)
                val time = measureNanoTime {
                    signer.generateSignature()
                }
                fixedTimings.add(time)

            } else {
                signer.update(randomMessage, 0, randomMessage.size)
                val time = measureNanoTime {
                    signer.generateSignature()
                }
                randomTimings.add(time)
            }
        }

        saveTimingsToCsv(fixedTimings, randomTimings, mlDsaParameters.name, "BC_DSA_TVLA_message")
    }

    @Test
    fun test_ML_DSA_3_key_TVLA() {
        val keyPair = keyGenerator.generateKeyPair()
        val signer  = MLDSASigner()

        signer.init(true, ParametersWithRandom(keyPair.private, random))

        warmUp(signer)

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        repeat(100000) {
            val coin = random.nextInt(2)
            val tempSigner  = MLDSASigner()
            val tempKeyPair = keyGenerator.generateKeyPair()
            tempSigner.init(true, ParametersWithRandom(tempKeyPair.private, random))

            if (coin == 0) {
                signer.update(message, 0, message.size)
                val time = measureNanoTime {
                    signer.generateSignature()
                }
                fixedTimings.add(time)

            } else {
                signer.update(message, 0, message.size)
                val time = measureNanoTime {
                    tempSigner.generateSignature()
                }
                randomTimings.add(time)
            }
        }

        saveTimingsToCsv(fixedTimings, randomTimings, mlDsaParameters.name, "BC_DSA_TVLA_key")
    }

    private fun warmUp(signer: MLDSASigner) {
        // WARM-UP PHASE: run the target method 200 times
        signer.update(message, 0, message.size)
        repeat(200) {
            signer.generateSignature()
        }
        println("JVM warmed up")
    }
}

