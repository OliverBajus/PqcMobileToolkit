package com.example.libqos_android.api.model

internal object PqcConstants {
    object KEM {
        const val ALG_NAME_ML_KEM_3 = "ML-KEM-768"
        const val ALG_NAME_ML_KEM_5 = "ML-KEM-1024"

        const val ALG_NAME_HQC_1 = "HQC-128"
        const val ALG_NAME_HQC_3 = "HQC-192"
        const val ALG_NAME_HQC_5 = "HQC-256"

        const val ALG_NAME_BIKE_3 = "BIKE-L3"
        const val ALG_NAME_BIKE_5 = "BIKE-L5"

        const val ALG_NAME_FRODO_AES_3 = "FrodoKEM-976-AES"
        const val ALG_NAME_FRODO_AES_5 = "FrodoKEM-1344-AES"

        const val ALG_NAME_FRODO_SHAKE_3 = "FrodoKEM-976-SHAKE"
        const val ALG_NAME_FRODO_SHAKE_5 = "FrodoKEM-1344-SHAKE"

        // !BIG STACK USAGE!
/*        const val ALG_NAME_MC_ELIECE_3 = "Classic-McEliece-460896"
        const val ALG_NAME_MC_ELIECE_3_F = "Classic-McEliece-460896f"

        const val ALG_NAME_MC_ELIECE_5_6688128  = "Classic-McEliece-6688128"
        const val ALG_NAME_MC_ELIECE_5_6688128F = "Classic-McEliece-6688128f"

        const val ALG_NAME_MC_ELIECE_5_6960119  = "Classic-McEliece-6960119"
        const val ALG_NAME_MC_ELIECE_5_6960119F = "Classic-McEliece-6960119f"

        const val ALG_NAME_MC_ELIECE_5_8192128  = "Classic-McEliece-8192128"
        const val ALG_NAME_MC_ELIECE_5_8192128F = "Classic-McEliece-8192128f"*/
    }

    object DSA {
        const val ALG_NAME_ML_DSA_3 = "ML-DSA-65"
        const val ALG_NAME_ML_DSA_5 = "ML-DSA-87"

        const val ALG_NAME_SLH_DSA_3_PS_FAST_SHA = "SLH_DSA_PURE_SHA2_192F"
        const val ALG_NAME_SLH_DSA_5_PS_FAST_SHA = "SLH_DSA_PURE_SHA2_256F"
        const val ALG_NAME_SLH_DSA_3_PS_SMALL_SHA = "SLH_DSA_PURE_SHA2_192S"
        const val ALG_NAME_SLH_DSA_5_PS_SMALL_SHA = "SLH_DSA_PURE_SHA2_256S"

        const val ALG_NAME_SLH_DSA_3_PS_FAST_SHAKE = "SLH_DSA_PURE_SHAKE_192F"
        const val ALG_NAME_SLH_DSA_5_PS_FAST_SHAKE = "SLH_DSA_PURE_SHAKE_256F"
        const val ALG_NAME_SLH_DSA_3_PS_SMALL_SHAKE = "SLH_DSA_PURE_SHAKE_192S"
        const val ALG_NAME_SLH_DSA_5_PS_SMALL_SHAKE = "SLH_DSA_PURE_SHAKE_256S"

        const val ALG_NAME_MAYO_3 = "MAYO-3"
        //const val ALG_NAME_MAYO_5 = "MAYO-5"

        const val ALG_NAME_FALCON_5 = "Falcon-1024"
        const val ALG_NAME_FALCON_5_PS_PADDED = "Falcon-padded-1024"

        // !BIG STACK USAGE!
        //const val ALG_NAME_CROSS_3_PS_RSDP_SMALL = "cross-rsdp-192-small"
        // BIG STACK USAGE
        //const val ALG_NAME_CROSS_5_PS_RSDP_SMALL = "cross-rsdp-256-small"

        const val ALG_NAME_CROSS_3_PS_RSDP_FAST =  "cross-rsdp-192-fast"
        const val ALG_NAME_CROSS_5_PS_RSDP_FAST = "cross-rsdp-256-fast"
        const val ALG_NAME_CROSS_3_PS_RSDP_BALANCED = "cross-rsdp-192-balanced"

        // !BIG STACK USAGE!
        //const val ALG_NAME_CROSS_5_PS_RSDP_BALANCED = "cross-rsdp-256-balanced"
        // const val ALG_NAME_CROSS_3_PS_RSDPG_SMALL = "cross-rsdpg-192-small"
        // BIG STACK USAGE
        //const val ALG_NAME_CROSS_5_PS_RSDPG_SMALL = "cross-rsdpg-256-small"
        const val ALG_NAME_CROSS_3_PS_RSDPG_FAST = "cross-rsdpg-192-fast"
        const val ALG_NAME_CROSS_5_PS_RSDPG_FAST = "cross-rsdpg-256-fast"
        const val ALG_NAME_CROSS_3_PS_RSDPG_BALANCED = "cross-rsdpg-192-balanced"
        const val ALG_NAME_CROSS_5_PS_RSDPG_BALANCED = "cross-rsdpg-256-balanced"

        // !BIG STACK USAGE!
        // const val ALG_NAME_SNOVA_3_PS_56_25_2 = "SNOVA_56_25_2"
        // const val ALG_NAME_SNOVA_3_PS_49_11_3 = "SNOVA_49_11_3"
        // const val ALG_NAME_SNOVA_3_PS_37_8_4 = "SNOVA_37_8_4"
        // const val ALG_NAME_SNOVA_3_PS_24_5_5 = "SNOVA_24_5_5"

        // const val ALG_NAME_SNOVA_5_PS_60_10_4 = "SNOVA_60_10_4"
        // const val ALG_NAME_SNOVA_5_PS_29_6_5 = "SNOVA_29_6_5"

        const val ALG_NAME_UOV_3= "OV-III"
        const val ALG_NAME_UOV_5= "OV-V"

        const val ALG_NAME_UOV_3_PS_PKC= "OV-III-pkc"
        const val ALG_NAME_UOV_5_PS_PKC= "OV-V-pkc"

        const val ALG_NAME_UOV_3_PS_PKC_SKC= "OV-III-pkc-skc"
        const val ALG_NAME_UOV_5_PS_PKC_SKC= "OV-V-pkc-skc"
    }
}