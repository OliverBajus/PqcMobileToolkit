package com.example.libqos_android.kem;

import static com.example.libqos_android.utils.CommonUtilsKt.wipe;

import com.example.libqos_android.api.kem.KemManager;
import com.example.libqos_android.api.kem.KemTimingManager;
import com.example.libqos_android.api.exceptions.MechanismNotEnabledError;
import com.example.libqos_android.api.exceptions.MechanismNotSupportedError;
import com.example.libqos_android.api.kem.model.KemCiphertext;
import com.example.libqos_android.api.model.KemAlgorithm;
import com.example.libqos_android.api.kem.model.KemDetails;
import com.example.libqos_android.api.kem.model.KemEncapsulationResult;
import com.example.libqos_android.api.kem.model.KemKeypair;
import com.example.libqos_android.api.kem.model.KemPrivateKey;
import com.example.libqos_android.api.kem.model.KemPublicKey;
import com.example.libqos_android.api.kem.model.KemSharedSecret;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;

/**
 * \brief Key Encapsulation Mechanisms
 */
class KeyEncapsulation implements KemManager, KemTimingManager {
    /**
     * Keep native pointer for Java to remember which C memory it is managing.
     */
    private long native_kem_handle_;

    private final byte[] publicKey;
    private final byte[] secretKey;

    private KeyEncapsulationDetails alg_details_;

    KeyEncapsulation(KemAlgorithm alg) throws RuntimeException {
        this(alg, null);
    }

    KeyEncapsulation(KemAlgorithm alg, byte[] secret_key)
            throws RuntimeException {
        String alg_name = alg.getId();
        // KEM not enabled
        if (!KEMs.INSTANCE.isEnabled(alg)) {
            // perhaps it's supported
            if (KEMs.INSTANCE.isSupported(alg)) {
                throw new MechanismNotEnabledError(alg_name);
            } else {
                throw new MechanismNotSupportedError(alg_name);
            }
        }
        create_KEM_new(alg_name);
        alg_details_ = get_KEM_details();
        if (alg_details_ == null) {
            free_KEM();
            throw new IllegalStateException("Failed to load KEM details");
        }

        // initialize keys
        if (secret_key != null) {
            this.secretKey = Arrays.copyOf(secret_key, secret_key.length);
        } else {
            this.secretKey = new byte[(int) alg_details_.length_secret_key];
        }
        this.publicKey = new byte[(int) alg_details_.length_public_key];
    }

    @Override
    public void close() {
        disposeKem();
    }

    @Override
    @NotNull
    public KemKeypair generateKeyPair() {
        int rv_ = generate_keypair(this.publicKey, this.secretKey);
        if (rv_ != 0) throw new RuntimeException("Cannot generate keypair");

        return new KemKeypair(
                new KemPublicKey(Arrays.copyOf(this.publicKey, this.publicKey.length)),
                new KemPrivateKey(Arrays.copyOf(this.secretKey, this.secretKey.length))
        );
    }

    @Override
    @NotNull
    public KemEncapsulationResult encapsulate(@NotNull KemPublicKey kemPublicKey) {
        if (kemPublicKey.getBytes().length != alg_details_.length_public_key) {
            throw new RuntimeException("Incorrect public key length");
        }

        byte[] ciphertext = new byte[(int) alg_details_.length_ciphertext];
        byte[] shared_secret = new byte[(int) alg_details_.length_shared_secret];

        int rv_= encap_secret(ciphertext, shared_secret, kemPublicKey.getBytes());

        if (rv_ != 0) throw new RuntimeException("Cannot encapsulate secret");

        return new KemEncapsulationResult(
                new KemCiphertext(ciphertext),
                new KemSharedSecret(shared_secret)
        );
    }

    @Override
    @NotNull
    public KemSharedSecret decapsulate(@NotNull KemCiphertext kemCiphertext) {
        if (kemCiphertext.getBytes().length != alg_details_.length_ciphertext) {
            throw new RuntimeException("Incorrect ciphertext length");
        }
        if (this.secretKey.length != alg_details_.length_secret_key) {
            throw new RuntimeException("Incorrect secret key length, " +
                    "make sure you specify one in the " +
                    "constructor or run generate_keypair()");
        }
        byte[] shared_secret = new byte[(int)alg_details_.length_shared_secret];
        int rv_ = decap_secret(shared_secret, kemCiphertext.getBytes(), this.secretKey);
        if (rv_ != 0) throw new RuntimeException("Cannot decapsulate secret");
        return new KemSharedSecret(shared_secret);
    }

    @Override
    public @NotNull KemDetails getKemDetails() {
        return new KemDetails(
                alg_details_.method_name,
                alg_details_.alg_version,
                alg_details_.claimed_nist_level,
                alg_details_.ind_cca,
                alg_details_.length_public_key,
                alg_details_.length_secret_key,
                alg_details_.length_ciphertext,
                alg_details_.length_shared_secret
        );
    }

    @Override
    public long timeKeygenNs() {
        if (alg_details_ == null) alg_details_ = get_KEM_details();

        // re-use object buffers (no allocations here)
        long t = keypair_with_timing_native(this.publicKey, this.secretKey);
        if (t < 0) throw new RuntimeException("Native keypair timing failed");
        return t;
    }

    @Override
    public long timeEncapsNs(@NotNull KemPublicKey publicKey) {
        if (alg_details_ == null) alg_details_ = get_KEM_details();

        if (publicKey.getBytes().length != alg_details_.length_public_key) {
            throw new RuntimeException("Invalid public key for timing test");
        }

        byte[] ciphertext = new byte[(int) alg_details_.length_ciphertext];
        byte[] shared_secret = new byte[(int) alg_details_.length_shared_secret];

        long t = encaps_with_timing_native(ciphertext, shared_secret, publicKey.getBytes());
        if (t < 0) throw new RuntimeException("Native encaps timing failed");
        return t;
    }

    @Override
    public long timeDecapsNs(@NotNull KemCiphertext ciphertext) {
        if (alg_details_ == null) alg_details_ = get_KEM_details();

        if (ciphertext.getBytes().length != alg_details_.length_ciphertext) {
            throw new RuntimeException("Invalid ciphertext length");
        }
        if (this.secretKey == null || this.secretKey.length != alg_details_.length_secret_key) {
            throw new RuntimeException("Invalid secret key length");
        }

        byte[] shared_secret = new byte[(int) alg_details_.length_shared_secret];

        long t = decaps_with_timing_native(shared_secret, ciphertext.getBytes(), this.secretKey);
        if (t < 0) throw new RuntimeException("Native decaps timing failed");
        return t;
    }

    @Override
    public @Nullable KemPublicKey getPublicKey() {
        return new KemPublicKey(Arrays.copyOf(this.publicKey, this.publicKey.length));
    }

    @Override
    @Nullable
    public KemPrivateKey getPrivateKey() {
        return new KemPrivateKey(Arrays.copyOf(this.secretKey, this.secretKey.length));
    }

    /**
     * \brief KEM algorithm details
     */
    private static class KeyEncapsulationDetails {

        String method_name;
        String alg_version;
        byte claimed_nist_level;
        boolean ind_cca;
        long length_public_key;
        long length_secret_key;
        long length_ciphertext;
        long length_shared_secret;
    }

    private void disposeKem() {
        wipe(this.secretKey);
        free_KEM();
    }

    //////////////////////
    /// NATIVE METHODS ///
    //////////////////////

    /**
     * \brief Wrapper for OQS_API OQS_KEM *OQS_KEM_new(const char *method_name).
     * Calls OQS_KEM_new and stores return value to native_kem_handle_.
     */
    private native void create_KEM_new(String method_name);

    /**
     * \brief Wrapper for OQS_API void OQS_KEM_free(OQS_KEM *kem);
     * Frees an OQS_KEM object that was constructed by OQS_KEM_new.
     */
    private native void free_KEM();

    /**
     * \brief Initialize and fill a KeyEncapsulationDetails object from the
     * native C struct pointed by native_kem_handle_.
     */
    private native KeyEncapsulationDetails get_KEM_details();

    /**
     * \brief Wrapper for OQS_API OQS_STATUS OQS_KEM_keypair(const OQS_KEM *kem,
     *                              uint8_t *public_key, uint8_t *secret_key);
     * \param Public key
     * \param Secret key
     * \return Status
     */
    private native int generate_keypair(byte[] public_key, byte[] secret_key);

    /**
     * \brief Wrapper for OQS_API OQS_STATUS OQS_KEM_encaps(const OQS_KEM *kem,
     *                                               uint8_t *ciphertext,
     *                                               uint8_t *shared_secret,
     *                                               const uint8_t *public_key);
     * \param ciphertext
     * \param shared secret>
     * \param Public key
     * \return Status
     */
    private native int encap_secret(byte[] ciphertext, byte[] shared_secret,
                                    byte[] public_key);

    /**
     * \brief Wrapper for OQS_API OQS_STATUS OQS_KEM_decaps(const OQS_KEM *kem,
     *                                          uint8_t *shared_secret,
     *                                          const unsigned char *ciphertext,
     *                                          const uint8_t *secret_key);
     * \param shared_secret
     * \param ciphertext
     * \param secret_key
     * \return Status
     */
    private native int decap_secret(byte[] shared_secret, byte[] ciphertext,
                                    byte[] secret_key);
    private native long keypair_with_timing_native(byte[] public_key, byte[] secret_key);
    private native long encaps_with_timing_native(byte[] ciphertext, byte[] shared_secret,
                                                  byte[] public_key);
    private native long decaps_with_timing_native(byte[] shared_secret, byte[] ciphertext,
                                                  byte[] secret_key);
}
