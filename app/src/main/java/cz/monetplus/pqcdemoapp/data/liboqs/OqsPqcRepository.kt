package cz.monetplus.pqcdemoapp.data.liboqs

import com.aheaditec.architecture.domain.error.Failure
import com.aheaditec.functional.Either
import io.github.oliverbajus.liboqs_android.Oqs
import io.github.oliverbajus.liboqs_android.api.kem.model.KemEncapsulationResult
import io.github.oliverbajus.liboqs_android.api.kem.model.KemKeypair
import io.github.oliverbajus.liboqs_android.api.kem.model.KemSharedSecret
import io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm
import io.github.oliverbajus.liboqs_android.api.sig.model.SigKeypair
import io.github.oliverbajus.liboqs_android.kem.KEMs
import io.github.oliverbajus.liboqs_android.sig.Sigs
import cz.monetplus.pqcdemoapp.domain.PqcRepository
import cz.monetplus.pqcdemoapp.domain.model.AlgChoice
import cz.monetplus.pqcdemoapp.domain.model.KemResult
import cz.monetplus.pqcdemoapp.domain.model.PqcFailure
import cz.monetplus.pqcdemoapp.domain.model.SigResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureNanoTime

@Singleton
class OqsPqcRepository @Inject constructor() : PqcRepository {
    override fun supportedKems(): List<AlgChoice> =
        KEMs.supportedAlgorithms().map {
            AlgChoice(id = it.id, name = it.javaClass.simpleName.toString())
        }

    override fun supportedSigs(): List<AlgChoice> =
        Sigs.supportedAlgorithms()
            .map {
                AlgChoice(id = it.id, name = it.javaClass.simpleName.toString())
            }

    override suspend fun runKemFlow(
        alg: AlgChoice,
    ): Either<Failure, KemResult> {
        val oqsAlgorithm = oqsKemById(alg.id) ?: return Either.Left(PqcFailure.PqcError(null, "Unsupported algorithm"))
        try {
            Oqs.createKemManager(oqsAlgorithm).use { client ->
                lateinit var keypair: KemKeypair
                val keypairTimeNs = measureNanoTime { keypair = client.generateKeyPair() }

                Oqs.createKemManager(oqsAlgorithm).use { server ->
                    lateinit var encaps: KemEncapsulationResult
                    val encapsTimeNs = measureNanoTime {
                        encaps = server.encapsulate(keypair.public)
                    }

                    lateinit var ss: KemSharedSecret
                    val decapsTimeNs = measureNanoTime {
                        ss = client.decapsulate(encaps.kemCiphertext)
                    }

                    // 4) Validate
                    val ok = ss.bytes.contentEquals(encaps.kemSharedSecret.bytes)
                    return Either.Right(KemResult(keypairTimeNs, encapsTimeNs, decapsTimeNs, ok))
                }
            }
        } catch (t: Throwable) {
            return Either.Left(PqcFailure.PqcError(t))
        }
    }

    override suspend fun runSigFlow(
        alg: AlgChoice,
        message: ByteArray
    ): Either<Failure, SigResult> {
        val oqcAlgorithm = oqsSigById(alg.id) ?: return Either.Left(PqcFailure.PqcError(null, "Unsupported algorithm"))
        try {
            Oqs.createSignatureManager(oqcAlgorithm).use { signer ->
                lateinit var keypair: SigKeypair
                val keygenNs = measureNanoTime { keypair = signer.generateKeyPair() }

                lateinit var signature: ByteArray
                val signNs = measureNanoTime { signature = signer.sign(message) }

                Oqs.createSignatureManager(oqcAlgorithm).use { verifier ->
                    var isValid = false
                    val verifyNs = measureNanoTime {
                        isValid = verifier.verify(message, signature, keypair.public)
                    }

                    return Either.Right(SigResult(keygenNs, signNs, verifyNs, isValid))
                }
            }
        } catch (t: Throwable) {
            return Either.Left(PqcFailure.PqcError(t))
        }
    }

    private fun oqsKemById(id: String) =
        PqcAlgorithm.Kem.all.firstOrNull { it.id == id }

    private fun oqsSigById(id: String) =
        PqcAlgorithm.Sig.all.firstOrNull { it.id == id }
}