package com.example.pqcdemoapp.kem.tvla.liboqs

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.KeyEncapsulation
import com.example.pqcdemoapp.PqcConstants
import com.example.pqcdemoapp.saveTimingsToCsv
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureNanoTime


@RunWith(AndroidJUnit4::class)
class LibOqsTvlaKemTest {

    private lateinit var client: KeyEncapsulation
    private lateinit var server: KeyEncapsulation
    private val random = java.security.SecureRandom()

    @Test
    fun validate() {
        client = KeyEncapsulation(PqcConstants.KEM.ALG_NAME_ML_KEM_3)
        server = KeyEncapsulation(PqcConstants.KEM.ALG_NAME_ML_KEM_3)

        val clientPublicKey: ByteArray = client.generate_keypair()

        val enc = server.encap_secret(clientPublicKey)
        val fixedCiphertext: ByteArray = enc.left
        val serverSharedSecret: ByteArray = enc.right

        val clientSharedSecret = client.decap_secret(fixedCiphertext)

        assertThat(serverSharedSecret).isEqualTo(clientSharedSecret)

        // test reused client public key
        val serverForReusedClientKey = KeyEncapsulation(PqcConstants.KEM.ALG_NAME_ML_KEM_3)

        val secondKeyEnc = serverForReusedClientKey.encap_secret(clientPublicKey)
        val secondCiphertext = secondKeyEnc.left
        val secondSharedSecret = secondKeyEnc.right

        val secondClientSharedSecret = client.decap_secret(secondCiphertext)

        assertThat(secondSharedSecret).isEqualTo(secondClientSharedSecret)
    }

    @Test
    fun test_ML_KEM_3() {
        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_ML_KEM_3)
    }

    @Test
    fun test_ML_HQC_1() {
        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_HQC_1)
    }

    @Test
    fun test_ML_HQC_3() {
        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_HQC_3)
    }

    @Test
    fun test_ML_HQC_5() {
        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_HQC_5)
    }

    @Test
    fun test_FRODO_SHAKE_3() {
        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_FRODO_SHAKE_3)
    }

    @Test
    fun test_FRODO_AES_3() {
        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_FRODO_AES_3)
    }

    @Test
    fun test_BIKE_3() {
        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_BIKE_3)
    }

    private fun performTVLA_on_ciphertext(algName: String) {
        client = KeyEncapsulation(algName)
        server = KeyEncapsulation(algName)

        val clientPublicKey: ByteArray = client.generate_keypair()
        val fixedCiphertext: ByteArray = server.encap_secret(clientPublicKey).left

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        repeat(100000) {
            // create new instance with newly generated keys
            val serverForRandomCiphertext = KeyEncapsulation(algName)
            val randomCiphertext = serverForRandomCiphertext.encap_secret(clientPublicKey).left

            if (random.nextBoolean()) {

                // reuse the same key for signing
                val time = measureNanoTime {
                    client.decap_secret(fixedCiphertext)
                }
                fixedTimings.add(time)

            } else {

                val time = measureNanoTime {
                    client.decap_secret(randomCiphertext)
                }
                randomTimings.add(time)

            }
            serverForRandomCiphertext.dispose_KEM()
        }
        saveTimingsToCsv(fixedTimings, randomTimings, algName, "LibOQS_KEM_ciphertext_TVLA")
    }

    private fun performTVLA_on_ciphertext_fail(algName: String) {
        client = KeyEncapsulation(algName)
        server = KeyEncapsulation(algName)

        val clientPublicKey: ByteArray = client.generate_keypair()
        val fixedCiphertext: ByteArray = server.encap_secret(clientPublicKey).left
        val invalidFixedCiphertext = createInvalidCiphertext(fixedCiphertext)

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        repeat(100000) {
            // create new instance with newly generated keys
            val serverForRandomCiphertext = KeyEncapsulation(algName)
            val randomCiphertext = serverForRandomCiphertext.encap_secret(clientPublicKey).left
            val invalidRandomCiphertext = createInvalidCiphertext(randomCiphertext)

            if (random.nextBoolean()) {

                // reuse the same key for signing
                val time = measureNanoTime {
                    client.decap_secret(invalidFixedCiphertext)
                }
                fixedTimings.add(time)

            } else {

                val time = measureNanoTime {
                    client.decap_secret(invalidRandomCiphertext)
                }
                randomTimings.add(time)

            }
            serverForRandomCiphertext.dispose_KEM()
        }
        saveTimingsToCsv(fixedTimings, randomTimings, algName, "LibOQS_KEM_ciphertext_TVLA")
    }


    private fun performTVLA_on_key_fail(algName: String) {
        val client = KeyEncapsulation(algName)
        val server = KeyEncapsulation(algName)

        val clientPublicKey: ByteArray = client.generate_keypair()
        val enc = server.encap_secret(clientPublicKey)

        val validCiphertext: ByteArray = enc.left

        val invalidCiphertext: ByteArray = createInvalidCiphertext(validCiphertext)

        check(client.decap_secret(validCiphertext).contentEquals(enc.right)) { "Valid decap failed" }
        check(!client.decap_secret(invalidCiphertext).contentEquals(enc.right)) { "cBad must reject" }

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        val coins = Array(100000) { random.nextBoolean() }

        repeat(100000) { i ->
            val randomClient = KeyEncapsulation(algName).apply { generate_keypair() }
            if (coins[i]) {
                val time = measureNanoTime {
                    client.decap_secret(invalidCiphertext)
                }
                fixedTimings.add(time)
            } else {
                val time = measureNanoTime {
                    randomClient.decap_secret(invalidCiphertext)
                }
                randomTimings.add(time)
            }
            randomClient.dispose_KEM()
        }

        saveTimingsToCsv(fixedTimings, randomTimings, algName, "LibOQS_KEM_key_fail_TVLA")
    }

    private fun createInvalidCiphertext(validCiphertext: ByteArray): ByteArray {
        return validCiphertext.clone().also { ct ->
            val mid = ct.size / 2
            ct[mid] = (ct[mid].toInt() xor 0x01).toByte()
        }
    }

    @After
    fun tearDown() {
        client.dispose_KEM()
        server.dispose_KEM()
    }
}