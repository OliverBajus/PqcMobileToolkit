package com.example.pqcdemoapp.kem.tvla.liboqs

import com.example.libqos_android.Signature
import com.example.pqcdemoapp.PqcConstants
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pqcdemoapp.saveTimingsToCsv
import kotlin.system.measureNanoTime

@RunWith(AndroidJUnit4::class)
class LibOqsTvlaDsaTest {

    private lateinit var client: Signature
    private val fixedMessage = "This is the message to sign to test TVLA on DSA PQC algorithms!".toByteArray()
    private val random = java.security.SecureRandom()

    private lateinit var algorithmName: String

    @Test
    fun verify() {
        client = Signature(PqcConstants.DSA.ALG_NAME_ML_DSA_3)
        val publicKey = client.generate_keypair()
        val signature = client.sign(fixedMessage)
        val result = client.verify(fixedMessage, signature, publicKey)
        assert(result)
    }

    @Test
    fun test_ML_DSA_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_ML_DSA_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_ML_DSA_5() {
        algorithmName = PqcConstants.DSA.ALG_NAME_ML_DSA_5
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_SPHINCS_FAST_SHA_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_SPHINCS_FAST_SHA_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_SPHINCS_SMALL_SHA_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_SPHINCS_SMALL_SHA_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_SPHINCS_FAST_SHAKE_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_SPHINCS_FAST_SHAKE_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

    @Test
    fun test_SPHINCS_SMALL_SHAKE_3() {
        algorithmName = PqcConstants.DSA.ALG_NAME_SPHINCS_SMALL_SHAKE_3
        performTVLA_on_ciphertext()
        performTVLA_on_key()
    }

//    @Test
//    fun test_FALCON_5() {
//        TODO("FALCON_5 not working, FIX ME")
//        algorithmName = PqcConstants.DSA.ALG_NAME_FALCON_5
//        performTVLA_on_key()
//    }

//    @Test
//    fun test_MAYO_3() {
//        algorithmName = PqcConstants.DSA.ALG_NAME_MAYO_3
//        performTVLA_on_ciphertext()
//    }
//
//    @Test
//    fun test_MAYO_5() {
//        algorithmName = PqcConstants.DSA.ALG_NAME_MAYO_5
//        performTVLA_on_ciphertext()
//    }
//
//    @Test
//    fun test_CROSS_RSDP_FAST_3() {
//        algorithmName = PqcConstants.DSA.ALG_NAME_CROSS_RSDP_FAST_3
//        performTVLA_on_ciphertext()
//    }
//
//    @Test
//    fun test_CROSS_RSDPG_FAST_3() {
//        algorithmName = PqcConstants.DSA.ALG_NAME_CROSS_RSDPG_FAST_3
//        performTVLA_on_ciphertext()
//    }

    private fun performTVLA_on_ciphertext() {
        client = Signature(algorithmName)
        client.generate_keypair()

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        repeat(100000) {
            val coin = random.nextInt(2)

            val randomMessage = ByteArray(fixedMessage.size)
            random.nextBytes(randomMessage)

            if (coin == 0) {

                val time = measureNanoTime {
                    client.sign(fixedMessage)
                }
                fixedTimings.add(time)

            } else {

                val time = measureNanoTime {
                    client.sign(randomMessage)
                }
                randomTimings.add(time)

            }
        }

        client.dispose_sig()
        saveTimingsToCsv(fixedTimings, randomTimings, algorithmName, "LibOQS_DSA_ciphertext_TVLA")
    }

    private fun performTVLA_on_key() {
        // create fixed signature instance to ve used as fixed key
        client = Signature(algorithmName)
        client.generate_keypair()

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        repeat(100000) {
            val coin = random.nextInt(2)

            // create new signature instance with newly generated keys
            val tempClient = Signature(algorithmName)
            tempClient.generate_keypair()

            if (coin == 0) {

                // reuse the same key for signing
                val time = measureNanoTime {
                    client.sign(fixedMessage)
                }
                fixedTimings.add(time)

            } else {

                // use random key for signing
                val time = measureNanoTime {
                    tempClient.sign(fixedMessage)
                }
                randomTimings.add(time)

            }
            tempClient.dispose_sig()
        }

        client.dispose_sig()
        saveTimingsToCsv(fixedTimings, randomTimings, algorithmName, "LibOQS_DSA_key_TVLA")
    }
}
