package com.example.libqos_android.api.kem.model

class KemPublicKey(val bytes: ByteArray)
class KemPrivateKey(val bytes: ByteArray)

data class KemKeypair(val public: KemPublicKey, internal val private: KemPrivateKey)
