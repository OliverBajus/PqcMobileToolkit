package cz.monetplus.pqcdemoapp.data.bc

import com.aheaditec.architecture.domain.error.Failure
import com.aheaditec.functional.Either
import cz.monetplus.pqcdemoapp.data.bc.model.BcKem
import cz.monetplus.pqcdemoapp.data.bc.model.BcSig
import cz.monetplus.pqcdemoapp.data.bc.utils.BcFactory
import cz.monetplus.pqcdemoapp.domain.PqcRepository
import cz.monetplus.pqcdemoapp.domain.model.AlgChoice
import cz.monetplus.pqcdemoapp.domain.model.KemResult
import cz.monetplus.pqcdemoapp.domain.model.PqcFailure
import cz.monetplus.pqcdemoapp.domain.model.SigResult
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.SecretWithEncapsulation
import org.bouncycastle.crypto.params.ParametersWithRandom
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureNanoTime

@Singleton
class BcPqcRepository @Inject constructor(
    private val bcFactory: BcFactory,
    private val secureRandom: SecureRandom,
) : PqcRepository {

    override fun supportedKems(): List<AlgChoice> =
        BcKem.entries.map { AlgChoice(it.id, it.label) }

    override fun supportedSigs(): List<AlgChoice> =
        BcSig.entries.map { AlgChoice(it.id, it.label) }

    override suspend fun runKemFlow(alg: AlgChoice): Either<Failure, KemResult> {
        val bcKem = BcKem.fromId(alg.id)
            ?: return Either.Left(PqcFailure.PqcError(null, "Unsupported BC KEM choice: ${alg.id}"))

        val kemManager = bcFactory.createKemManager(bcKem)

        lateinit var keyPair: AsymmetricCipherKeyPair
        val keypairTimeNs = measureNanoTime { keyPair = kemManager.generateKeyPair() }

        lateinit var encapsResult: SecretWithEncapsulation
        val encapsTimeNs = measureNanoTime {
            encapsResult = kemManager.encapsulate(keyPair)
        }

        lateinit var extractedSecret: ByteArray
        val extractor = kemManager.extractorFactory(keyPair.private)
        val decapsTimeNs = measureNanoTime {
            extractedSecret = extractor.extractSecret(encapsResult.encapsulation)
        }

        val ok = extractedSecret.contentEquals(encapsResult.secret)

        return Either.Right(KemResult(keypairTimeNs, encapsTimeNs, decapsTimeNs, ok))
    }

    override suspend fun runSigFlow(
        alg: AlgChoice,
        message: ByteArray
    ): Either<Failure, SigResult> {
        val bcSig = BcSig.Companion.fromId(alg.id) ?: return Either.Left(PqcFailure.PqcError(null, "Unsupported BC Sig choice: ${alg.id}"))

        val sigManager = bcFactory.createSignatureManager(bcSig)

        lateinit var keyPair: AsymmetricCipherKeyPair
        val keygenNs = measureNanoTime { keyPair = sigManager.generateKeyPair() }

        val signer = sigManager.signerFactory(true,
            ParametersWithRandom(keyPair.private, secureRandom)
        )
        lateinit var signature: ByteArray
        val signNs = measureNanoTime { signature = signer.generateSignature(message) }

        val verifier = sigManager.signerFactory(false, keyPair.public)
        var isValid = false
        val verifyNs = measureNanoTime {
            isValid = verifier.verifySignature(message, signature)
        }

        return Either.Right(SigResult(keygenNs, signNs, verifyNs, isValid))
    }
}