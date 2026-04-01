package io.github.oliverbajus.liboqs_android.sig;

import static io.github.oliverbajus.liboqs_android.utils.CommonUtilsKt.wipe;

import androidx.annotation.NonNull;
import io.github.oliverbajus.liboqs_android.api.exceptions.MechanismNotEnabledError;
import io.github.oliverbajus.liboqs_android.api.exceptions.MechanismNotSupportedError;
import io.github.oliverbajus.liboqs_android.api.sig.SignatureManager;
import io.github.oliverbajus.liboqs_android.api.sig.SignatureTimingManager;
import io.github.oliverbajus.liboqs_android.api.sig.model.SigDetails;
import io.github.oliverbajus.liboqs_android.api.sig.model.SigKeypair;
import io.github.oliverbajus.liboqs_android.api.sig.model.SigPrivateKey;
import io.github.oliverbajus.liboqs_android.api.sig.model.SigPublicKey;
import io.github.oliverbajus.liboqs_android.api.model.SignatureAlgorithm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;

/**
 * \brief Signature Mechanisms
 */
class Signature implements SignatureManager, SignatureTimingManager {

    /**
     * Keep native pointers for Java to remember which C memory it is managing.
     */
    private long native_sig_handle_;

    private final byte[] publicKey;
    private final byte[] secretKey;


    /**
     * Private object that has the signature details.
     */
    private SignatureDetails _algDetails;

    /**
     * \brief Constructs an instance of oqs::Signature
     * \param alg_name Cryptographic algorithm method_name
     */
    Signature(SignatureAlgorithm alg) throws RuntimeException {
        this(alg, null);
    }

    /**
     * \brief Constructs an instance of oqs::Signature
     * \param alg_name Cryptographic algorithm method_name
     * \param secret_key Secret key
     */
    Signature(SignatureAlgorithm alg, byte[] secret_key) throws RuntimeException {
        String alg_name = alg.getId();

        // signature not enabled
        if (!Sigs.INSTANCE.isEnabled(alg)) {
            // perhaps it's supported
            if (Sigs.INSTANCE.isSupported(alg)) {
                throw new MechanismNotEnabledError(alg_name);
            } else {
                throw new MechanismNotSupportedError(alg_name);
            }
        }
        create_sig_new(alg_name);
        _algDetails = get_sig_details();
        if (_algDetails == null) {
            free_sig();
            throw new IllegalStateException("Failed to load Sig details");
        }

        // initialize keys
        if (secret_key != null) {
            this.secretKey = Arrays.copyOf(secret_key, secret_key.length);
        } else {
            this.secretKey = new byte[(int) _algDetails.length_secret_key];
        }
        this.publicKey = new byte[(int) _algDetails.length_public_key];
    }


    @Override
    public void close() {
        dispose_sig();
    }

    @Override
    public @NotNull SigDetails getSignatureDetails() {
        return new SigDetails(
                _algDetails.method_name,
                _algDetails.alg_version,
                _algDetails.claimed_nist_level,
                _algDetails.is_euf_cma,
                _algDetails.length_public_key,
                _algDetails.length_secret_key,
                _algDetails.max_length_signature
        );
    }

    @Override
    @NotNull
    public SigKeypair generateKeyPair() {
        int rv_ = generate_keypair(this.publicKey, this.secretKey);
        if (rv_ != 0) throw new RuntimeException("Cannot generate keypair");
        return new SigKeypair(
                new SigPublicKey(Arrays.copyOf(this.publicKey, this.publicKey.length)),
                new SigPrivateKey(Arrays.copyOf(this.secretKey, this.secretKey.length))
        );
    }

    @Override
    public boolean verify(@NotNull byte[] message, @NotNull byte[] signature, @NotNull SigPublicKey publicKey) {
        if (publicKey.getBytes().length != _algDetails.length_public_key) {
            throw new RuntimeException("Incorrect public key length");
        }
        if (signature.length > _algDetails.max_length_signature) {
            throw new RuntimeException("Incorrect signature length");
        }

        return verify(message, message.length, signature, signature.length, publicKey.getBytes());
    }

    @NonNull
    @Override
    public byte[] sign(@NonNull byte[] message) throws RuntimeException {
        if (this.secretKey.length != _algDetails.length_secret_key) {
            throw new RuntimeException("Incorrect secret key length, " +
                    "make sure you specify one in the " +
                    "constructor or run generate_keypair()");
        }
        byte[] signature = new byte[(int) _algDetails.max_length_signature];
        Mutable<Long> signature_len_ret = new Mutable<>();
        int rv_= sign(signature, signature_len_ret,
                message, message.length, this.secretKey);
        long actual_signature_len = signature_len_ret.value;
        byte[] actual_signature = new byte[(int) actual_signature_len];
        System.arraycopy(signature, 0,
                actual_signature, 0, (int) actual_signature_len);
        if (rv_ != 0) throw new RuntimeException("Cannot sign message");
        return actual_signature;
    }

    @Override
    public long timeKeygenNs() {
        if (_algDetails == null) _algDetails = get_sig_details();

        long t = keypair_with_timing_native(this.publicKey, this.secretKey);
        if (t < 0) throw new RuntimeException("Native keypair timing failed");
        return t;
    }

    @Override
    public long timeSignNs(@NonNull byte[] message) {
        if (_algDetails == null) _algDetails = get_sig_details();

        if (this.secretKey == null || this.secretKey.length != _algDetails.length_secret_key) {
            throw new RuntimeException("Incorrect secret key length, " +
                    "make sure you specify one in the constructor or run generate_keypair()");
        }

        byte[] signatureBuf = new byte[(int) _algDetails.max_length_signature];

        long t = sign_with_timing_native(signatureBuf, message, this.secretKey);
        if (t < 0) throw new RuntimeException("Native sign timing failed");
        return t;
    }

    @Override
    public long timeVerifyNs(@NonNull byte[] message, @NonNull byte[] signature, @NonNull SigPublicKey publicKey) {
        if (_algDetails == null) _algDetails = get_sig_details();

        if (publicKey.getBytes().length != _algDetails.length_public_key) {
            throw new RuntimeException("Invalid public key length");
        }

        long t = verify_with_timing_native(message, signature, publicKey.getBytes());
        if (t < 0) throw new RuntimeException("Native verify timing failed");
        return t;
    }

    @Override
    public @Nullable SigPublicKey getPublicKey() {
        return new SigPublicKey(Arrays.copyOf(this.publicKey, this.publicKey.length));
    }

    @Override
    public @Nullable SigPrivateKey getPrivateKey() {
        return new SigPrivateKey(Arrays.copyOf(this.secretKey, this.secretKey.length));
    }

    /**
     * \brief Signature algorithm details
     */
    private static class SignatureDetails {

        String method_name;
        String alg_version;
        byte claimed_nist_level;
        boolean is_euf_cma;
        long length_public_key;
        long length_secret_key;
        long max_length_signature;
    }

    /**
     * \brief Invoke native free_sig
     */
    private void dispose_sig() {
        wipe(this.secretKey);
        free_sig();
    }

    public static class Mutable<T> {
        T value;
        public void setValue(T t) { this.value = t; }
        public T getValue() { return this.value; }
    }

    //////////////////////
    /// NATIVE METHODS ///
    //////////////////////

    /**
     * \brief Wrapper for OQS_API OQS_SIG *OQS_SIG_new(const char *method_name);
     * Calls OQS_SIG_new and stores return value to native_sig_handle_.
     */
    private native void create_sig_new(String method_name);

    /**
     * \brief Wrapper for OQS_API void OQS_SIG_free(OQS_SIG *sig);
     * Frees an OQS_SIG object that was constructed by OQS_SIG_new.
     */
    private native void free_sig();

    /**
     * \brief Initialize and fill a SignatureDetails object from the native
     * C struct pointed by native_sig_handle_.
     */
    private native SignatureDetails get_sig_details();

    /**
     * \brief Wrapper for OQS_API OQS_STATUS OQS_SIG_keypair(const OQS_SIG *sig,
     *                              uint8_t *public_key, uint8_t *secret_key);
     * \param Public key
     * \param Secret key
     * \return Status
     */
    private native int generate_keypair(byte[] public_key, byte[] secret_key);

    /**
     * \brief Wrapper for OQS_API OQS_STATUS OQS_SIG_sign(const OQS_SIG *sig,
     *                                              uint8_t *signature,
     *                                              size_t *signature_len,
     *                                              const uint8_t *message,
     *                                              size_t message_len,
     *                                              const uint8_t *secret_key);
     * \param signature
     * \param signature_len_ret
     * \param message
     * \param message_len
     * \param secret_key
     * \return Status
     */
    private native int sign(byte[] signature, Mutable<Long> signature_len_ret,
                        byte[] message, long message_len, byte[] secret_key);

    /**
     * \brief Wrapper for OQS_API OQS_STATUS OQS_SIG_verify(const OQS_SIG *sig,
     *                                              const uint8_t *message,
     *                                              size_t message_len,
     *                                              const uint8_t *signature,
     *                                              size_t signature_len,
     *                                              const uint8_t *public_key);
     * \param message
     * \param message_len
     * \param signature
     * \param signature_len
     * \param public_key
     * \return True if the signature is valid, false otherwise
     */
    private native boolean verify(byte[] message, long message_len,
                                byte[] signature, long signature_len,
                                byte[] public_key);
    
    /**
     * \brief Wrapper for OQS_API OQS_STATUS OQS_SIG_sign_with_ctx_str(const OQS_SIG *sig,
     *                                              uint8_t *signature,
     *                                              size_t *signature_len,
     *                                              const uint8_t *message,
     *                                              size_t message_len,
     *                                              const uint8_t *ctx,
     *                                              size_t ctx_len,
     *                                              const uint8_t *secret_key);
     * \param signature
     * \param signature_len_ret
     * \param message
     * \param message_len
     * \param ctx
     * \param ctx_len
     * \param secret_key
     * \return Status
     */
    private native int sign_with_ctx_str(byte[] signature, Mutable<Long> signature_len_ret,
                        byte[] message, long message_len, byte[] ctx, long ctx_len,
                        byte[] secret_key);

    /**
     * \brief Wrapper for OQS_API OQS_STATUS OQS_SIG_verify_with_ctx_str(const OQS_SIG *sig,
     *                                              const uint8_t *message,
     *                                              size_t message_len,
     *                                              const uint8_t *signature,
     *                                              size_t signature_len,
     *                                              const uint8_t *ctx,
     *                                              size_t ctx_len,
     *                                              const uint8_t *public_key);
     * \param message
     * \param message_len
     * \param signature
     * \param signature_len
     * \param ctx
     * \param ctx_len
     * \param public_key
     * \return True if the signature is valid, false otherwise
     */
    private native boolean verify_with_ctx_str(byte[] message, long message_len,
                                byte[] signature, long signature_len,
                                byte[] ctx, long ctx_len,
                                byte[] public_key);

    private native long keypair_with_timing_native(byte[] public_key, byte[] secret_key);
    private native long sign_with_timing_native(byte[] signatureOut, byte[] message, byte[] secretKey);
    private native long verify_with_timing_native(byte[] message, byte[] signature, byte[] publicKey);
}
