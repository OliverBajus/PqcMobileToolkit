package com.example.pqcdemoapp

import android.R
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libqos_android.Common
import com.example.libqos_android.KEMs
import com.example.libqos_android.KeyEncapsulation
import com.example.libqos_android.Pair
import com.example.libqos_android.Signature
import com.example.libqos_android.Sigs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.measureNanoTime


@HiltViewModel
class MainViewModel @Inject internal constructor(
    private val mlkemService: MLKEMService,
): ViewModel() {
    private val _logText = MutableStateFlow("")
    val logText = _logText.asStateFlow()

    var kemEncapsulationTimes: ArrayList<Long> = arrayListOf()
    var kemDecapsulationTimes: ArrayList<Long> = arrayListOf()
    var kemKeyGenerationTimes: ArrayList<Long> = arrayListOf()

    var dsaVerificationTimes: ArrayList<Long> = arrayListOf()
    var dsaSigningTimes: ArrayList<Long> = arrayListOf()
    var dsaKeyGenerationTimes: ArrayList<Long> = arrayListOf()

//    private val kems3 = listOf("BIKE-L3", "HQC-192", "ML-KEM-768", "FrodoKEM-976-AES", "FrodoKEM-976-SHAKE")
    private val kems3 = listOf("ML-KEM-768")
    private val kems5 = listOf("BIKE-L5", "HQC-256", "ML-KEM-1024", "FrodoKEM-1344-AES", "FrodoKEM-1344-SHAKE")

    enum class SecurityLevel {
        LEVEL_3,
        LEVEL_5,
    }

    private val dsa3 = listOf("ML-DSA-65", "SPHINCS+-SHA2-192f-simple", "SPHINCS+-SHA2-192s-simple", "SPHINCS+-SHAKE-192f-simple", "SPHINCS+-SHAKE-192s-simple", "MAYO-3", "cross-rsdp-192-balanced", "cross-rsdp-192-fast", "cross-rsdp-192-small", "cross-rsdpg-192-balanced", "cross-rsdpg-192-fast", "cross-rsdpg-192-small")

    @RequiresApi(Build.VERSION_CODES.Q)
    fun runDSA(context: Context, securityLevel: SecurityLevel) {
        val random = java.security.SecureRandom()
        val randomMessage = ByteArray("sskfskdfjjhksdflkshkfskfjsdjkfsdhfsksfhj".toByteArray().size)
        random.nextBytes(randomMessage)
        println(randomMessage)
        return
        viewModelScope.launch(Dispatchers.IO) {
            Common.loadNativeLibrary()
            val dsa = when(securityLevel) {
                SecurityLevel.LEVEL_3 -> dsa3
                SecurityLevel.LEVEL_5 -> dsa3
            }

            dsa3.forEach {
                appendLog("Testing $it...")
                testDSA(it, context, securityLevel)
                appendLog("Testing $it finished")
                dsaVerificationTimes = arrayListOf()
                dsaKeyGenerationTimes = arrayListOf()
                dsaSigningTimes = arrayListOf()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun runKEM(context: Context, securityLevel: SecurityLevel) {
        viewModelScope.launch(Dispatchers.IO) {
            Common.loadNativeLibrary()
            val kems = when(securityLevel) {
                SecurityLevel.LEVEL_3 -> kems3
                SecurityLevel.LEVEL_5 -> kems5
            }

            kems.forEach {
                appendLog("Testing $it...")
                testKEM(it, context, securityLevel)
                appendLog("Testing $it finished")
                kemEncapsulationTimes = arrayListOf()
                kemDecapsulationTimes = arrayListOf()
                kemKeyGenerationTimes = arrayListOf()
            }
            appendLog("----Test finished-----")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun testDSA(dsaName: String, context: Context, securityLevel: SecurityLevel) {
        val message = "This is the message to sign".toByteArray()
        repeat(1000) {
            val signer = Signature(dsaName)

            val keyGenerationNanoTime = measureNanoTime {
                signer.generate_keypair()
            }
            val signer_public_key: ByteArray = signer.generate_keypair()

            val signingNanoTime = measureNanoTime {
                signer.sign(message)
            }

            val signature = signer.sign(message)
            val verifier = Signature(dsaName)

            val verificationNanoTime = measureNanoTime {
                verifier.verify(message, signature, signer_public_key)
            }

            signer.dispose_sig()
            verifier.dispose_sig()

            dsaVerificationTimes.add(verificationNanoTime)
            dsaSigningTimes.add(signingNanoTime)
            dsaKeyGenerationTimes.add(keyGenerationNanoTime)
        }

        appendLog("Saving results")
        appendCsvToDownloads(context, "DSA_${securityLevel.name}_performance_times.csv", dsaSigningTimes, dsaName, "signing")
        appendCsvToDownloads(context, "DSA_${securityLevel.name}_performance_times.csv", dsaVerificationTimes, dsaName, "verification")
        appendCsvToDownloads(context, "DSA_${securityLevel.name}_performance_times.csv", dsaKeyGenerationTimes, dsaName, "key_generation")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun testKEM(kemName: String, context: Context, securityLevel: SecurityLevel) {
        repeat(100000) {

            val client = KeyEncapsulation(kemName)

            val keyGenerationNanoTime = measureNanoTime {
                client.generate_keypair()
            }
            val client_public_key = client.generate_keypair()
            val server = KeyEncapsulation(kemName)
            val encapsulationNanoTime = measureNanoTime {
                server.encap_secret(client_public_key)
            }

            val server_pair: Pair<ByteArray, ByteArray> = server.encap_secret(client_public_key)
            val decapsulationNanoTime = measureNanoTime {
                client.decap_secret(server_pair.left)
            }

            client.dispose_KEM()
            server.dispose_KEM()

            kemEncapsulationTimes.add(encapsulationNanoTime)
            kemDecapsulationTimes.add(decapsulationNanoTime)
            kemKeyGenerationTimes.add(keyGenerationNanoTime)
        }

        appendLog("Saving results")
        appendCsvToDownloads(context, "KEM_${securityLevel.name}_performance_times.csv", kemEncapsulationTimes, kemName, "encapsulation")
        appendCsvToDownloads(context, "KEM_${securityLevel.name}_performance_times.csv", kemDecapsulationTimes, kemName, "decapsulation")
        appendCsvToDownloads(context, "KEM_${securityLevel.name}_performance_times.csv", kemKeyGenerationTimes, kemName, "key_generation")
    }


    private fun appendLog(message: String) {
        Log.d("PQC", message)
        _logText.update { it + "\n" + message }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun appendCsvToDownloads(context: Context, fileName: String, data: List<Long>, algName: String, operation: String) {
    val resolver = context.contentResolver
    val queryUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI

    // Search for the existing file in Downloads
    val cursor = resolver.query(
        queryUri,
        arrayOf(MediaStore.Downloads._ID),
        "${MediaStore.Downloads.DISPLAY_NAME} = ?",
        arrayOf(fileName),
        null
    )

    var uri: Uri? = null

    if (cursor?.moveToFirst() == true) {
        // File exists, get its URI
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID))
        uri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
    }
    cursor?.close()


    if (uri != null) {
        // File exists, append data
        resolver.openOutputStream(uri, "wa")?.use { outputStream ->
            data.forEach { line ->
                outputStream.write("$algName, $operation, $line\n".toByteArray())
            }
        }
    } else {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write("alg_name, operation, time_nano_sec\n".toByteArray())

                data.forEach { line ->
                    outputStream.write("$algName, $operation, $line\n".toByteArray())
                }
            }
        }
    }

}
