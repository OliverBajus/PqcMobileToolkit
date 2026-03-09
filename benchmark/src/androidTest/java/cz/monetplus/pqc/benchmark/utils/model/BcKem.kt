package cz.monetplus.pqc.benchmark.utils.model

enum class BcKem(
    val id: String,
    val label: String
) {
    MLKEM_768("mlkem-768", "ML-KEM-768 (Level 3)"),
    MLKEM_1024("mlkem-1024", "ML-KEM-1024 (Level 5)"),
    HQC_192("hqc-192", "HQC-192 (Level 3)"),
    HQC_256("hqc-256", "HQC-256 (Level 5)"),
    FRODO_976_AES("frodo-976-aes", "FrodoKEM-976-AES (Level 3)"),
    FRODO_1344_AES("frodo-1344-aes", "FrodoKEM-1344-AES (Level 5)"),
    FRODO_976_SHAKE("frodo-976-shake", "FrodoKEM-976-SHAKE (Level 3)"),
    FRODO_1344_SHAKE("frodo-1344-shake", "FrodoKEM-1344-SHAKE (Level 5)");

    companion object {
        fun fromId(id: String): BcKem? = entries.firstOrNull { it.id == id }
    }
}