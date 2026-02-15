package com.example.pqcdemoapp.domain.model

enum class AppSig(val label: String) {
    MLDSA_65("ML-DSA-65 (Level 3)"),
    MLDSA_87("ML-DSA-87 (Level 5)"),
    FALCON_1024("Falcon-1024 (Level 5)"),
    MAYO_3("MAYO-3 (Level 3)"),
    MAYO_5("MAYO-5 (Level 5)"),
    SLH_SHA2_192F("SLH-DSA-SHA2-192f (Level 3)"),
    SLH_SHA2_192S("SLH-DSA-SHA2-192s (Level 3)"),
    SLH_SHA2_256F("SLH-DSA-SHA2-256f (Level 5)"),
    SLH_SHA2_256S("SLH-DSA-SHA2-256s (Level 5)"),
    SLH_SHAKE_192F("SLH-DSA-SHAKE-192f (Level 3)"),
    SLH_SHAKE_192S("SLH-DSA-SHAKE-192s (Level 3)"),
    SLH_SHAKE_256F("SLH-DSA-SHAKE-256f (Level 5)"),
    SLH_SHAKE_256S("SLH-DSA-SHAKE-256s (Level 5)"),
}