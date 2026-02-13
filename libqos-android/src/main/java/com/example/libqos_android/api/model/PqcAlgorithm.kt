package com.example.libqos_android.api.model

import com.example.libqos_android.api.model.PqcConstants.KEM
import com.example.libqos_android.api.model.PqcConstants.DSA

object PqcAlgorithm {

    object Kem {
        // --- ML-KEM
        data object MlKem3 : KemAlgorithm { override val id = KEM.ALG_NAME_ML_KEM_3 }
        data object MlKem5 : KemAlgorithm { override val id = KEM.ALG_NAME_ML_KEM_5 }

        // --- HQC
        data object Hqc1 : KemAlgorithm { override val id = KEM.ALG_NAME_HQC_1 }
        data object Hqc3 : KemAlgorithm { override val id = KEM.ALG_NAME_HQC_3 }
        data object Hqc5 : KemAlgorithm { override val id = KEM.ALG_NAME_HQC_5 }

        // --- BIKE
        data object Bike3 : KemAlgorithm { override val id = KEM.ALG_NAME_BIKE_3 }
        data object Bike5 : KemAlgorithm { override val id = KEM.ALG_NAME_BIKE_5 }

        // --- FrodoKEM
        data object FrodoKemAes3 : KemAlgorithm { override val id = KEM.ALG_NAME_FRODO_AES_3 }
        data object FrodoKemAes5 : KemAlgorithm { override val id = KEM.ALG_NAME_FRODO_AES_5 }
        data object FrodoKemShake3 : KemAlgorithm { override val id = KEM.ALG_NAME_FRODO_SHAKE_3 }
        data object FrodoKemShake5 : KemAlgorithm { override val id = KEM.ALG_NAME_FRODO_SHAKE_5 }

        // --- Classic McEliece (multiple L5 parameter sets)
        data object McEliece3 : KemAlgorithm { override val id = KEM.ALG_NAME_MC_ELIECE_3 }
        data object McEliece3f : KemAlgorithm { override val id = KEM.ALG_NAME_MC_ELIECE_3_F }

        data object McEliece5_6688128 : KemAlgorithm { override val id = KEM.ALG_NAME_MC_ELIECE_5_6688128 }
        data object McEliece5_6688128f : KemAlgorithm { override val id = KEM.ALG_NAME_MC_ELIECE_5_6688128F }

        data object McEliece5_6960119 : KemAlgorithm { override val id = KEM.ALG_NAME_MC_ELIECE_5_6960119 }
        data object McEliece5_6960119f : KemAlgorithm { override val id = KEM.ALG_NAME_MC_ELIECE_5_6960119F }

        data object McEliece5_8192128 : KemAlgorithm { override val id = KEM.ALG_NAME_MC_ELIECE_5_8192128 }
        data object McEliece5_8192128f : KemAlgorithm { override val id = KEM.ALG_NAME_MC_ELIECE_5_8192128F }

        data class UnknownKem(override val id: String) : KemAlgorithm

        val all: List<KemAlgorithm> = listOf(
            // ML-KEM
            MlKem3, MlKem5,
            // HQC
            Hqc1, Hqc3, Hqc5,
            // BIKE
            Bike3, Bike5,
            // Frodo
            FrodoKemAes3, FrodoKemAes5, FrodoKemShake3, FrodoKemShake5,
            // McEliece
            McEliece3, McEliece3f,
            McEliece5_6688128, McEliece5_6688128f,
            McEliece5_6960119, McEliece5_6960119f,
            McEliece5_8192128, McEliece5_8192128f
        )
    }

    object Sig {
        // --- ML-DSA
        data object MlDsa3 : SignatureAlgorithm { override val id = DSA.ALG_NAME_ML_DSA_3 }
        data object MlDsa5 : SignatureAlgorithm { override val id = DSA.ALG_NAME_ML_DSA_5 }

        // --- SPHINCS+ SHA2 (fast/small)
        data object Sphincs3FastSha : SignatureAlgorithm { override val id = DSA.ALG_NAME_SPHINCS_3_PS_FAST_SHA }
        data object Sphincs5FastSha : SignatureAlgorithm { override val id = DSA.ALG_NAME_SPHINCS_5_PS_FAST_SHA }
        data object Sphincs3SmallSha : SignatureAlgorithm { override val id = DSA.ALG_NAME_SPHINCS_3_PS_SMALL_SHA }
        data object Sphincs5SmallSha : SignatureAlgorithm { override val id = DSA.ALG_NAME_SPHINCS_5_PS_SMALL_SHA }

        // --- SPHINCS+ SHAKE (fast/small)
        data object Sphincs3FastShake : SignatureAlgorithm { override val id = DSA.ALG_NAME_SPHINCS_3_PS_FAST_SHAKE }
        data object Sphincs5FastShake : SignatureAlgorithm { override val id = DSA.ALG_NAME_SPHINCS_5_PS_FAST_SHAKE }
        data object Sphincs3SmallShake : SignatureAlgorithm { override val id = DSA.ALG_NAME_SPHINCS_3_PS_SMALL_SHAKE }
        data object Sphincs5SmallShake : SignatureAlgorithm { override val id = DSA.ALG_NAME_SPHINCS_5_PS_SMALL_SHAKE }

        // --- MAYO
        data object Mayo3 : SignatureAlgorithm { override val id = DSA.ALG_NAME_MAYO_3 }
        data object Mayo5 : SignatureAlgorithm { override val id = DSA.ALG_NAME_MAYO_5 }

        // --- Falcon
        data object Falcon5 : SignatureAlgorithm { override val id = DSA.ALG_NAME_FALCON_5 }
        data object Falcon5Padded : SignatureAlgorithm { override val id = DSA.ALG_NAME_FALCON_5_PS_PADDED }

        // --- CROSS (rsdp)
        data object Cross3RsdpSmall : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_3_PS_RSDP_SMALL }
        data object Cross5RsdpSmall : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_5_PS_RSDP_SMALL }
        data object Cross3RsdpFast : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_3_PS_RSDP_FAST }
        data object Cross5RsdpFast : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_5_PS_RSDP_FAST }
        data object Cross3RsdpBalanced : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_3_PS_RSDP_BALANCED }
        data object Cross5RsdpBalanced : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_5_PS_RSDP_BALANCED }

        // --- CROSS (rsdpg)
        data object Cross3RsdpgSmall : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_3_PS_RSDPG_SMALL }
        data object Cross5RsdpgSmall : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_5_PS_RSDPG_SMALL }
        data object Cross3RsdpgFast : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_3_PS_RSDPG_FAST }
        data object Cross5RsdpgFast : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_5_PS_RSDPG_FAST }
        data object Cross3RsdpgBalanced : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_3_PS_RSDPG_BALANCED }
        data object Cross5RsdpgBalanced : SignatureAlgorithm { override val id = DSA.ALG_NAME_CROSS_5_PS_RSDPG_BALANCED }

        // --- SNOVA
        data object Snova3Ps56x25x2 : SignatureAlgorithm { override val id = DSA.ALG_NAME_SNOVA_3_PS_56_25_2 }
        data object Snova3Ps49x11x3 : SignatureAlgorithm { override val id = DSA.ALG_NAME_SNOVA_3_PS_49_11_3 }
        data object Snova3Ps37x8x4  : SignatureAlgorithm { override val id = DSA.ALG_NAME_SNOVA_3_PS_37_8_4 }
        data object Snova3Ps24x5x5  : SignatureAlgorithm { override val id = DSA.ALG_NAME_SNOVA_3_PS_24_5_5 }

        // --- UOV / OV
        data object Uov3 : SignatureAlgorithm { override val id = DSA.ALG_NAME_UOV_3 }
        data object Uov5 : SignatureAlgorithm { override val id = DSA.ALG_NAME_UOV_5 }
        data object Uov3Pkc : SignatureAlgorithm { override val id = DSA.ALG_NAME_UOV_3_PS_PKC }
        data object Uov5Pkc : SignatureAlgorithm { override val id = DSA.ALG_NAME_UOV_5_PS_PKC }
        data object Uov3PkcSkc : SignatureAlgorithm { override val id = DSA.ALG_NAME_UOV_3_PS_PKC_SKC }
        data object Uov5PkcSkc : SignatureAlgorithm { override val id = DSA.ALG_NAME_UOV_5_PS_PKC_SKC }

        data class UnknownSig(override val id: String) : SignatureAlgorithm

        val all: List<SignatureAlgorithm> = listOf(
            MlDsa3, MlDsa5,

            Sphincs3FastSha, Sphincs5FastSha, Sphincs3SmallSha, Sphincs5SmallSha,
            Sphincs3FastShake, Sphincs5FastShake, Sphincs3SmallShake, Sphincs5SmallShake,

            Mayo3, Mayo5,
            Falcon5, Falcon5Padded,

            Cross3RsdpSmall, Cross5RsdpSmall,
            Cross3RsdpFast, Cross5RsdpFast,
            Cross3RsdpBalanced, Cross5RsdpBalanced,

            Cross3RsdpgSmall, Cross5RsdpgSmall,
            Cross3RsdpgFast, Cross5RsdpgFast,
            Cross3RsdpgBalanced, Cross5RsdpgBalanced,

            Snova3Ps56x25x2, Snova3Ps49x11x3, Snova3Ps37x8x4, Snova3Ps24x5x5,

            Uov3, Uov5, Uov3Pkc, Uov5Pkc, Uov3PkcSkc, Uov5PkcSkc
        )
    }
}

internal fun kemFromIdOrNull(id: String): KemAlgorithm? =
    PqcAlgorithm.Kem.all.firstOrNull { it.id == id }

internal fun sigFromIdOrNull(id: String): SignatureAlgorithm? =
    PqcAlgorithm.Sig.all.firstOrNull { it.id == id }

internal fun KemAlgorithm.algoName(): String =
    this::class.simpleName ?: this.javaClass.simpleName ?: "Unknown"

internal fun SignatureAlgorithm.algoName(): String =
    this::class.simpleName ?: this.javaClass.simpleName ?: "Unknown"