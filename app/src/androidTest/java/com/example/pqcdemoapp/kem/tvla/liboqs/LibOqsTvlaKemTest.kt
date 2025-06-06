package com.example.pqcdemoapp.kem.tvla.liboqs

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.KeyEncapsulation
import com.example.pqcdemoapp.PqcConstants
import com.example.pqcdemoapp.saveTimingsToCsv
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureNanoTime


@RunWith(AndroidJUnit4::class)
class LibOqsTvlaKemTest {

    private lateinit var client: KeyEncapsulation
    private lateinit var server: KeyEncapsulation
    private val random = java.security.SecureRandom()

    // NOTE: NOT POSSIBLE to do TVLA on key!
    //  - Can't import or set the private key.
    //  - Every generate_keypair() overwrites the internal key.

    @Test
    fun test_ML_KEM_3() {
        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_ML_KEM_3)
    }

    @Test
    fun test_ML_HQC_3() {
        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_HQC_3)
    }

//    @Test
//    fun test_FRODO_SHAKE_3() {
//        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_FRODO_SHAKE_3)
//    }
//
//    @Test
//    fun test_FRODO_AES_3() {
//        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_FRODO_AES_3)
//    }
//
//    @Test
//    fun test_BIKE_3() {
//        performTVLA_on_ciphertext(PqcConstants.KEM.ALG_NAME_BIKE_3)
//    }

    private fun performTVLA_on_ciphertext(algName: String) {
        client = KeyEncapsulation(algName)
        server = KeyEncapsulation(algName)

        val clientPublicKey: ByteArray = client.generate_keypair()
        val fixedCiphertext: ByteArray = server.encap_secret(clientPublicKey).left

        val fixedTimings = mutableListOf<Long>()
        val randomTimings = mutableListOf<Long>()

        repeat(100000) {
            val coin = random.nextInt(2)

            // create new instance with newly generated keys
            val serverForRandomCiphertext = KeyEncapsulation(algName)
            val randomCiphertext = serverForRandomCiphertext.encap_secret(clientPublicKey).left

            if (coin == 0) {

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
        client.dispose_KEM()
        // NOTE: result of decaps and encaps.right are shared secretes that should be equal

        saveTimingsToCsv(fixedTimings, randomTimings, algName, "LibOQS_KEM_ciphertext_TVLA")
    }

    @After
    fun tearDown() {
        client.dispose_KEM()
        server.dispose_KEM()
    }
}