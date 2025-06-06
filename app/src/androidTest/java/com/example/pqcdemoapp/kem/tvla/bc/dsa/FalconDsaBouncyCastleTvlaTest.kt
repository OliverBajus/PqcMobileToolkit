package com.example.pqcdemoapp.kem.tvla.bc.dsa

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pqcdemoapp.saveTimingsToCsv
import org.bouncycastle.crypto.params.ParametersWithRandom
import org.bouncycastle.pqc.crypto.falcon.FalconKeyGenerationParameters
import org.bouncycastle.pqc.crypto.falcon.FalconKeyPairGenerator
import org.bouncycastle.pqc.crypto.falcon.FalconParameters
import org.bouncycastle.pqc.crypto.falcon.FalconSigner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.SecureRandom
import kotlin.system.measureNanoTime

@RunWith(AndroidJUnit4::class)
class FalconDsaBouncyCastleTvlaTest {

    private val message = "This is the message to sign to test TVLA on DSA PQC algorithms!".toByteArray()
    private val random = SecureRandom()
    private val falconDsaParameters = FalconParameters.falcon_512
    private val keyGenerator = FalconKeyPairGenerator()

    @Before
    fun setUp() {
        keyGenerator.init(FalconKeyGenerationParameters(random, falconDsaParameters))
    }

    @Test
    fun validate() {
        val keyPair = keyGenerator.generateKeyPair()
        val signer  = FalconSigner()

        signer.init(true, ParametersWithRandom(keyPair.private, random))
        val signature = signer.generateSignature(message)
        val a = signer.verifySignature(message, signature)
        println(a)
    }

    @Test
    fun test_Falcon_3_messageTVLA() {
        val keyPair = keyGenerator.generateKeyPair()
        val signer  = FalconSigner()

        signer.init(true, ParametersWithRandom(keyPair.private, random))

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        warmUp(signer)

        repeat(100000) {
            val coin = random.nextInt(2)

            val randomMessage = ByteArray(message.size)
            random.nextBytes(randomMessage)

            if (coin == 0) {
                val time = measureNanoTime {
                    signer.generateSignature(message)
                }
                fixedTimings.add(time)

            } else {
                val time = measureNanoTime {
                    signer.generateSignature(randomMessage)
                }
                randomTimings.add(time)
            }
        }

        saveTimingsToCsv(fixedTimings, randomTimings, falconDsaParameters.name, "BC_DSA_TVLA_message")
    }

    @Test
    fun test_Falcon_3_key_TVLA() {
        val keyPair = keyGenerator.generateKeyPair()
        val signer  = FalconSigner()

        signer.init(true, ParametersWithRandom(keyPair.private, random))

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        warmUp(signer)

        repeat(100000) {
            val coin = random.nextInt(2)
            val tempSigner  = FalconSigner()
            val tempKeyPair = keyGenerator.generateKeyPair()
            tempSigner.init(true, ParametersWithRandom(tempKeyPair.private, random))

            if (coin == 0) {
                val time = measureNanoTime {
                    signer.generateSignature(message)
                }
                fixedTimings.add(time)

            } else {
                val time = measureNanoTime {
                    tempSigner.generateSignature(message)
                }
                randomTimings.add(time)
            }
        }

        saveTimingsToCsv(fixedTimings, randomTimings, falconDsaParameters.name, "BC_DSA_TVLA_key")
    }

    private fun warmUp(signer: FalconSigner) {
        // WARM-UP PHASE: run the target method 200 times
        repeat(200) {
            signer.generateSignature(message)
        }
        println("JVM warmed up")
    }
}

