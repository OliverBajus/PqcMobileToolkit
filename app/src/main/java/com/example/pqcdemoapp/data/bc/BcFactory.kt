package com.example.pqcdemoapp.data.bc

import com.example.pqcdemoapp.data.bc.model.BcKem
import com.example.pqcdemoapp.data.bc.model.BcSig
import org.bouncycastle.pqc.crypto.falcon.FalconKeyGenerationParameters
import org.bouncycastle.pqc.crypto.falcon.FalconKeyPairGenerator
import org.bouncycastle.pqc.crypto.falcon.FalconParameters
import org.bouncycastle.pqc.crypto.falcon.FalconSigner
import org.bouncycastle.pqc.crypto.frodo.FrodoKEMExtractor
import org.bouncycastle.pqc.crypto.frodo.FrodoKEMGenerator
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyGenerationParameters
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyPairGenerator
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters
import org.bouncycastle.pqc.crypto.hqc.HQCKEMExtractor
import org.bouncycastle.pqc.crypto.hqc.HQCKEMGenerator
import org.bouncycastle.pqc.crypto.hqc.HQCKeyGenerationParameters
import org.bouncycastle.pqc.crypto.hqc.HQCKeyPairGenerator
import org.bouncycastle.pqc.crypto.hqc.HQCParameters
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters
import org.bouncycastle.pqc.crypto.mayo.MayoKeyGenerationParameters
import org.bouncycastle.pqc.crypto.mayo.MayoKeyPairGenerator
import org.bouncycastle.pqc.crypto.mayo.MayoParameters
import org.bouncycastle.pqc.crypto.mayo.MayoSigner
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyGenerationParameters
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyPairGenerator
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters
import org.bouncycastle.pqc.crypto.mldsa.MLDSASigner
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyGenerationParameters
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyPairGenerator
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSASigner
import org.bouncycastle.pqc.crypto.snova.SnovaKeyGenerationParameters
import org.bouncycastle.pqc.crypto.snova.SnovaKeyPairGenerator
import org.bouncycastle.pqc.crypto.snova.SnovaParameters
import org.bouncycastle.pqc.crypto.snova.SnovaSigner
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BcFactory @Inject constructor(
    private val secureRandom: SecureRandom,
) {
    fun createKemManager(alg: BcKem): BcKemManager =
        when(alg) {
            BcKem.FRODO_976_AES -> {
                val keyGenerator = FrodoKeyPairGenerator()
                keyGenerator.init(
                    FrodoKeyGenerationParameters(secureRandom, FrodoParameters.frodokem976aes)
                )

                BcKemManager(
                    FrodoKEMGenerator(secureRandom),
                    keyGenerator
                ) { key ->
                    FrodoKEMExtractor(key as FrodoPrivateKeyParameters)
                }
            }
            BcKem.FRODO_1344_AES -> {
                val keyGenerator = FrodoKeyPairGenerator()
                keyGenerator.init(
                    FrodoKeyGenerationParameters(secureRandom, FrodoParameters.frodokem1344aes)
                )

                BcKemManager(FrodoKEMGenerator(secureRandom), keyGenerator)
                    { key ->
                        FrodoKEMExtractor(key as FrodoPrivateKeyParameters)
                    }
            }
            BcKem.FRODO_976_SHAKE -> {
                val keyGenerator = FrodoKeyPairGenerator()
                keyGenerator.init(
                    FrodoKeyGenerationParameters(secureRandom, FrodoParameters.frodokem976shake)
                )

                BcKemManager(FrodoKEMGenerator(secureRandom), keyGenerator)
                    { key ->
                        FrodoKEMExtractor(key as FrodoPrivateKeyParameters)
                    }
            }
            BcKem.FRODO_1344_SHAKE -> {
                val keyGenerator = FrodoKeyPairGenerator()
                keyGenerator.init(
                    FrodoKeyGenerationParameters(secureRandom, FrodoParameters.frodokem1344shake)
                )

                BcKemManager(FrodoKEMGenerator(secureRandom), keyGenerator)
                    { key ->
                        FrodoKEMExtractor(key as FrodoPrivateKeyParameters)
                    }
            }
            BcKem.HQC_192 -> {
                val keyGenerator = HQCKeyPairGenerator()
                keyGenerator.init(
                    HQCKeyGenerationParameters(secureRandom, HQCParameters.hqc192)
                )

                BcKemManager(HQCKEMGenerator(secureRandom), keyGenerator)
                    { key ->
                        HQCKEMExtractor(key as HQCPrivateKeyParameters)
                    }
            }
            BcKem.HQC_256 ->  {
                val keyGenerator =HQCKeyPairGenerator()
                keyGenerator.init(
                    HQCKeyGenerationParameters(secureRandom, HQCParameters.hqc256)
                )

                BcKemManager(HQCKEMGenerator(secureRandom), keyGenerator)
                    { key ->
                        HQCKEMExtractor(key as HQCPrivateKeyParameters)
                    }
            }
            BcKem.MLKEM_768 -> {
                val keyGenerator = MLKEMKeyPairGenerator()
                keyGenerator.init(MLKEMKeyGenerationParameters(secureRandom, MLKEMParameters.ml_kem_768))

                BcKemManager(MLKEMGenerator(secureRandom), keyGenerator)
                    { key ->
                        MLKEMExtractor(key as MLKEMPrivateKeyParameters)
                    }
            }
            BcKem.MLKEM_1024-> {
                val keyGenerator = MLKEMKeyPairGenerator()
                keyGenerator.init(MLKEMKeyGenerationParameters(secureRandom, MLKEMParameters.ml_kem_1024))

                BcKemManager(MLKEMGenerator(secureRandom), keyGenerator)
                    { key ->
                        MLKEMExtractor(key as MLKEMPrivateKeyParameters)
                    }
            }
        }

    fun createSignatureManager(alg: BcSig): BcSigManager =
        when(alg) {
            BcSig.FALCON_1024 -> {
                val keyGenerator = FalconKeyPairGenerator()
                keyGenerator.init(FalconKeyGenerationParameters(secureRandom, FalconParameters.falcon_1024))

                BcSigManager(
                    keyGenerator
                ) { forSigning, params ->
                    FalconSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.MAYO_3 -> {
                val keyGenerator = MayoKeyPairGenerator()
                keyGenerator.init(MayoKeyGenerationParameters(secureRandom, MayoParameters.mayo3))
                BcSigManager(keyGenerator) { forSigning, params ->
                    MayoSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.MAYO_5 -> {
                val keyGenerator = MayoKeyPairGenerator()
                keyGenerator.init(MayoKeyGenerationParameters(secureRandom, MayoParameters.mayo5))
                BcSigManager(keyGenerator) { forSigning, params ->
                    MayoSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.MLDSA_65 -> {
                val keyGenerator = MLDSAKeyPairGenerator()
                keyGenerator.init(MLDSAKeyGenerationParameters(secureRandom, MLDSAParameters.ml_dsa_65))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SignerAsMessageSigner(
                        MLDSASigner()
                            .apply { init(forSigning, params) }
                    )
                }
            }
            BcSig.MLDSA_87 -> {
                val keyGenerator = MLDSAKeyPairGenerator()
                keyGenerator.init(MLDSAKeyGenerationParameters(secureRandom, MLDSAParameters.ml_dsa_87))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SignerAsMessageSigner(
                        MLDSASigner()
                            .apply { init(forSigning, params) }
                    )
                }
            }
            BcSig.SNOVA_24_5_5_SSK -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_24_5_5_SSK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_24_5_5_ESK -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_24_5_5_ESK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_37_8_4_SSK -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_37_8_4_SSK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_37_8_4_ESK -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_37_8_4_ESK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_49_11_3_SSK  -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_49_11_3_SSK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_49_11_3_ESK  -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_49_11_3_ESK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_56_25_2_SSK -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_56_25_2_SSK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_56_25_2_ESK -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_56_25_2_ESK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_29_6_5_SSK -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_29_6_5_SSK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_29_6_5_ESK -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_29_6_5_ESK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_60_10_4_SSK -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_60_10_4_SSK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SNOVA_60_10_4_ESK -> {
                val keyGenerator = SnovaKeyPairGenerator()
                keyGenerator.init(SnovaKeyGenerationParameters(secureRandom, SnovaParameters.SNOVA_60_10_4_ESK))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SnovaSigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SLHDSA_SHA2_192F -> {
                val keyGenerator = SLHDSAKeyPairGenerator()
                keyGenerator.init(SLHDSAKeyGenerationParameters(secureRandom, SLHDSAParameters.sha2_192f))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SLHDSASigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SLHDSA_SHA2_192S -> {
                val keyGenerator = SLHDSAKeyPairGenerator()
                keyGenerator.init(SLHDSAKeyGenerationParameters(secureRandom, SLHDSAParameters.sha2_192s))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SLHDSASigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SLHDSA_SHA2_256F -> {
                val keyGenerator = SLHDSAKeyPairGenerator()
                keyGenerator.init(SLHDSAKeyGenerationParameters(secureRandom, SLHDSAParameters.sha2_256f))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SLHDSASigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SLHDSA_SHA2_256S -> {
                val keyGenerator = SLHDSAKeyPairGenerator()
                keyGenerator.init(SLHDSAKeyGenerationParameters(secureRandom, SLHDSAParameters.sha2_256s))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SLHDSASigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SLHDSA_SHAKE_192F -> {
                val keyGenerator = SLHDSAKeyPairGenerator()
                keyGenerator.init(SLHDSAKeyGenerationParameters(secureRandom, SLHDSAParameters.shake_192f))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SLHDSASigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SLHDSA_SHAKE_192S -> {
                val keyGenerator = SLHDSAKeyPairGenerator()
                keyGenerator.init(SLHDSAKeyGenerationParameters(secureRandom, SLHDSAParameters.shake_192s))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SLHDSASigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SLHDSA_SHAKE_256F -> {
                val keyGenerator = SLHDSAKeyPairGenerator()
                keyGenerator.init(SLHDSAKeyGenerationParameters(secureRandom, SLHDSAParameters.shake_256f))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SLHDSASigner()
                        .apply { init(forSigning, params) }
                }
            }
            BcSig.SLHDSA_SHAKE_256S -> {
                val keyGenerator = SLHDSAKeyPairGenerator()
                keyGenerator.init(SLHDSAKeyGenerationParameters(secureRandom, SLHDSAParameters.shake_256s))
                BcSigManager(keyGenerator) { forSigning, params ->
                    SLHDSASigner()
                        .apply { init(forSigning, params) }
                }
            }
        }
}