package com.example.pqcdemoapp

object PqcConstants {
    object KEM {
        const val ALG_NAME_ML_KEM_3 = "ML-KEM-768"
        const val ALG_NAME_ML_KEM_5 = "ML-KEM-1024"

        const val ALG_NAME_HQC_3 = "HQC-192"
        const val ALG_NAME_HQC_5 = "HQC-256"

        const val ALG_NAME_BIKE_3 = "BIKE-L3"
        const val ALG_NAME_BIKE_5 = "BIKE-L5"

        const val ALG_NAME_FRODO_AES_3 = "FrodoKEM-976-AES"
        const val ALG_NAME_FRODO_AES_5 = "FrodoKEM-1344-AES"

        const val ALG_NAME_FRODO_SHAKE_3 = "FrodoKEM-976-SHAKE"
        const val ALG_NAME_FRODO_SHAKE_5 = "FrodoKEM-1344-SHAKE"
    }

    object DSA {
        const val ALG_NAME_ML_DSA_3 = "ML-DSA-65"
        const val ALG_NAME_ML_DSA_5 = "ML-DSA-87"

        const val ALG_NAME_SPHINCS_FAST_SHA_3 = "SPHINCS+-SHA2-192f-simple"
        const val ALG_NAME_SPHINCS_FAST_SHA_5 = "SPHINCS+-SHA2-256f-simple"
        const val ALG_NAME_SPHINCS_SMALL_SHA_3 = "SPHINCS+-SHA2-192s-simple"
        const val ALG_NAME_SPHINCS_SMALL_SHA_5 = "SPHINCS+-SHA2-256s-simple"

        const val ALG_NAME_SPHINCS_FAST_SHAKE_3 = "SPHINCS+-SHAKE-192f-simple"
        const val ALG_NAME_SPHINCS_FAST_SHAKE_5 = "SPHINCS+-SHAKE-256f-simple"
        const val ALG_NAME_SPHINCS_SMALL_SHAKE_3 =  "SPHINCS+-SHAKE-192s-simple"
        const val ALG_NAME_SPHINCS_SMALL_SHAKE_5 = "SPHINCS+-SHAKE-256s-simple"

        const val ALG_NAME_MAYO_3 = "MAYO-3"
        const val ALG_NAME_MAYO_5 = "MAYO-5"

        const val ALG_NAME_FALCON_5 = "Falcon-1024"
        const val ALG_NAME_FALCON_PADDED_5 = "Falcon-padded-1024"

        const val ALG_NAME_CROSS_RSDP_SMALL_3 = "cross-rsdp-192-small"
        const val ALG_NAME_CROSS_RSDP_SMALL_5 = "cross-rsdp-256-small"
        const val ALG_NAME_CROSS_RSDP_FAST_3 =  "cross-rsdp-192-fast"
        const val ALG_NAME_CROSS_RSDP_FAST_5 = "cross-rsdp-256-fast"
        const val ALG_NAME_CROSS_RSDP_BALANCED_3 = "cross-rsdp-192-balanced"
        const val ALG_NAME_CROSS_RSDP_BALANCED_5 = "cross-rsdp-256-balanced"

        const val ALG_NAME_CROSS_RSDPG_SMALL_3 = "cross-rsdpg-192-small"
        const val ALG_NAME_CROSS_RSDPG_SMALL_5 = "cross-rsdpg-256-small"
        const val ALG_NAME_CROSS_RSDPG_FAST_3 = "cross-rsdpg-192-fast"
        const val ALG_NAME_CROSS_RSDPG_FAST_5 = "cross-rsdpg-256-fast"
        const val ALG_NAME_CROSS_RSDPG_BALANCED_3 = "cross-rsdpg-192-balanced"
        const val ALG_NAME_CROSS_RSDPG_BALANCED_5 = "cross-rsdpg-256-balanced"

    }
}