package cz.monetplus.pqcdemoapp.domain.model

enum class AppKem(val label: String) {
    MLKEM_768("ML-KEM-768 (Level 3)"),
    MLKEM_1024("ML-KEM-1024 (Level 5)"),
    HQC_192("HQC-192 (Level 3)"),
    HQC_256("HQC-256 (Level 5)"),
    FRODO_976_AES("FrodoKEM-976-AES (Level 3)"),
    FRODO_1344_AES("FrodoKEM-1344-AES (Level 5)"),
    FRODO_976_SHAKE("FrodoKEM-976-SHAKE (Level 3)"),
    FRODO_1344_SHAKE("FrodoKEM-1344-SHAKE (Level 5)"),
}

