package cz.monetplus.pqc.benchmark.utils.model

enum class BcSig(
    val id: String,
    val label: String
) {
    // ML-DSA
    MLDSA_65("mldsa-65", "ML-DSA-65 (Level 3)"),
    MLDSA_87("mldsa-87", "ML-DSA-87 (Level 5)"),

    // Falcon
    FALCON_1024("falcon-1024", "Falcon-1024 (Level 5)"),

    // MAYO
    MAYO_3("mayo-3", "MAYO-3 (Level 3)"),
    MAYO_5("mayo-5", "MAYO-5 (Level 5)"),

    // SNOVA
    SNOVA_24_5_5_ESK("snova-24-5-5-esk", "SNOVA-24-5-5-ESK"),
    SNOVA_24_5_5_SSK("snova-24-5-5-ssk", "SNOVA-24-5-5-SSK"),
    SNOVA_37_8_4_ESK("snova-37-8-4-esk", "SNOVA-37-8-4-ESK"),
    SNOVA_37_8_4_SSK("snova-37-8-4-ssk", "SNOVA-37-8-4-SSK"),
    SNOVA_49_11_3_ESK("snova-49-11-3-esk", "SNOVA-49-11-3-ESK"),
    SNOVA_49_11_3_SSK("snova-49-11-3-ssk", "SNOVA-49-11-3-SSK"),
    SNOVA_56_25_2_ESK("snova-56-25-2-esk", "SNOVA-56-25-2-ESK"),
    SNOVA_56_25_2_SSK("snova-56-25-2-ssk", "SNOVA-56-25-2-SSK"),
    SNOVA_29_6_5_ESK("snova-29-6-5-esk", "SNOVA-29-6-5-ESK"),
    SNOVA_29_6_5_SSK("snova-29-6-5-ssk", "SNOVA-29-6-5-SSK"),
    SNOVA_60_10_4_ESK("snova-60-10-4-esk", "SNOVA-60-10-4-ESK"),
    SNOVA_60_10_4_SSK("snova-60-10-4-ssk", "SNOVA-60-10-4-SSK"),

    // SLH-DSA (SHA2)
    SLHDSA_SHA2_192F("slhdsa-sha2-192f", "SLH-DSA-SHA2-192f (Level 3)"),
    SLHDSA_SHA2_192S("slhdsa-sha2-192s", "SLH-DSA-SHA2-192s (Level 3)"),
    SLHDSA_SHA2_256F("slhdsa-sha2-256f", "SLH-DSA-SHA2-256f (Level 5)"),
    SLHDSA_SHA2_256S("slhdsa-sha2-256s", "SLH-DSA-SHA2-256s (Level 5)"),

    // SLH-DSA (SHAKE)
    SLHDSA_SHAKE_192F("slhdsa-shake-192f", "SLH-DSA-SHAKE-192f (Level 3)"),
    SLHDSA_SHAKE_192S("slhdsa-shake-192s", "SLH-DSA-SHAKE-192s (Level 3)"),
    SLHDSA_SHAKE_256F("slhdsa-shake-256f", "SLH-DSA-SHAKE-256f (Level 5)"),
    SLHDSA_SHAKE_256S("slhdsa-shake-256s", "SLH-DSA-SHAKE-256s (Level 5)");

    companion object {
        fun fromId(id: String): BcSig? = entries.firstOrNull { it.id == id }
    }
}