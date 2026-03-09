package cz.monetplus.pqcdemoapp.data.bc.utils

import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.Signer
import org.bouncycastle.pqc.crypto.MessageSigner

class SignerAsMessageSigner(
    private val delegate: Signer
) : MessageSigner {

    override fun init(forSigning: Boolean, param: CipherParameters) {
        delegate.init(forSigning, param)
    }

    override fun generateSignature(message: ByteArray): ByteArray {
        delegate.reset()
        delegate.update(message, 0, message.size)
        return delegate.generateSignature()
    }

    override fun verifySignature(message: ByteArray, signature: ByteArray): Boolean {
        delegate.reset()
        delegate.update(message, 0, message.size)
        return delegate.verifySignature(signature)
    }
}