package com.example.libqos_android.api.sig.model

class SigPublicKey(val bytes: ByteArray)
class SigPrivateKey(val bytes: ByteArray)

data class SigKeypair(val public: SigPublicKey, val private: SigPrivateKey)
