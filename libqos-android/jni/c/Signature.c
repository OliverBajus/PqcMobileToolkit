#include <oqs/oqs.h>
#include <time.h>
#include "Signature.h"
#include "handle.h"

/*
 * Class:     org_openquantumsafe_Signature
 * Method:    create_sig_new
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_example_libqos_1android_Signature_create_1sig_1new
  (JNIEnv *env, jobject obj, jstring jstr)
{
    // Create get a liboqs::OQS_SIG pointer
    const char *str_native = (*env)->GetStringUTFChars(env, jstr, 0);
    OQS_SIG *sig = OQS_SIG_new(str_native);
    (*env)->ReleaseStringUTFChars(env, jstr, str_native);
    // Stow the native OQS_SIG pointer in the Java handle.
    setHandle(env, obj, sig, "native_sig_handle_");
}

/*
 * Class:     org_openquantumsafe_Signature
 * Method:    free_sig
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_libqos_1android_Signature_free_1sig
  (JNIEnv *env, jobject obj)
{
    OQS_SIG *sig = (OQS_SIG *) getHandle(env, obj, "native_sig_handle_");
    OQS_SIG_free(sig);
}

/*
 * Class:     org_openquantumsafe_Signature
 * Method:    get_sig_details
 * Signature: ()Lorg/openquantumsafe/Signature/SignatureDetails;
 */
JNIEXPORT jobject JNICALL Java_com_example_libqos_1android_Signature_get_1sig_1details
  (JNIEnv *env, jobject obj)
{
    jclass cls = (*env)->FindClass(env, "com/example/libqos_android/Signature$SignatureDetails");
    if (cls == NULL) { fprintf(stderr, "\nCould not find class\n"); return NULL; }

    // Get the Method ID of the constructor
    jmethodID constructor_meth_id_ = (*env)->GetMethodID(env, cls, "<init>", "()V");
    if (NULL == constructor_meth_id_) { fprintf(stderr, "\nCould not initialize class\n"); return NULL; }

    // Call back constructor to allocate a new instance, with an int argument
    jobject _nativeKED = (*env)->NewObject(env, cls, constructor_meth_id_);

    OQS_SIG *sig = (OQS_SIG *) getHandle(env, obj, "native_sig_handle_");

    // Copy fields from C struct to Java class
    // String method_name;
    jfieldID _method_name = (*env)->GetFieldID(env, cls, "method_name", "Ljava/lang/String;");
    jstring j_method_name = (*env)->NewStringUTF(env, sig->method_name);
    (*env)->SetObjectField(env, _nativeKED, _method_name, j_method_name);

    // String alg_version;
    jfieldID _alg_version = (*env)->GetFieldID(env, cls, "alg_version", "Ljava/lang/String;");
    jstring j_alg_version = (*env)->NewStringUTF(env, sig->alg_version);
    (*env)->SetObjectField(env, _nativeKED, _alg_version, j_alg_version);

    // byte claimed_nist_level;
    jfieldID _claimed_nist_level = (*env)->GetFieldID(env, cls, "claimed_nist_level", "B");
    (*env)->SetByteField(env, _nativeKED, _claimed_nist_level, (jbyte) sig->claimed_nist_level);

    // boolean is_euf_cma;
    jfieldID _is_euf_cma = (*env)->GetFieldID(env, cls, "is_euf_cma", "Z");
    (*env)->SetBooleanField(env, _nativeKED, _is_euf_cma, (jboolean) sig->euf_cma);

    // long length_public_key;
    jfieldID _length_public_key = (*env)->GetFieldID(env, cls, "length_public_key", "J");
    (*env)->SetLongField(env, _nativeKED, _length_public_key, (jlong) sig->length_public_key);

    // long length_secret_key;
    jfieldID _length_secret_key = (*env)->GetFieldID(env, cls, "length_secret_key", "J");
    (*env)->SetLongField(env, _nativeKED, _length_secret_key, (jlong) sig->length_secret_key);

    // long max_length_signature;
    jfieldID _max_length_signature = (*env)->GetFieldID(env, cls, "max_length_signature", "J");
    (*env)->SetLongField(env, _nativeKED, _max_length_signature, (jlong) sig->length_signature);

    return _nativeKED;
}

/*
 * Class:     org_openquantumsafe_Signature
 * Method:    generate_keypair
 * Signature: ([B[B)I
 */
JNIEXPORT jint JNICALL Java_com_example_libqos_1android_Signature_generate_1keypair
  (JNIEnv *env, jobject obj, jbyteArray jpublic_key, jbyteArray jsecret_key)
{
    jbyte *public_key_native = (*env)->GetByteArrayElements(env, jpublic_key, 0);
    jbyte *secret_key_native = (*env)->GetByteArrayElements(env, jsecret_key, 0);

    // Get pointer to sig
    OQS_SIG *sig = (OQS_SIG *) getHandle(env, obj, "native_sig_handle_");

    // Invoke liboqs sig keypair generation function
    OQS_STATUS rv_ = OQS_SIG_keypair(sig, (uint8_t*) public_key_native, (uint8_t*) secret_key_native);

    (*env)->ReleaseByteArrayElements(env, jpublic_key, public_key_native, 0);
    (*env)->ReleaseByteArrayElements(env, jsecret_key, secret_key_native, 0);
    return (rv_ == OQS_SUCCESS) ? 0 : -1;
}

/*
 * Class:     org_openquantumsafe_Signature
 * Method:    sign
 * Signature: ([BLjava/lang/Long;[BJ[B)I
 */
JNIEXPORT jint JNICALL Java_com_example_libqos_1android_Signature_sign
  (JNIEnv * env, jobject obj, jbyteArray jsignature, jobject sig_len_obj,
      jbyteArray jmessage, jlong message_len, jbyteArray jsecret_key)
{
    // Convert to jbyte arrays
    jbyte *signature_native = (*env)->GetByteArrayElements(env, jsignature, 0);
    jbyte *message_native = (*env)->GetByteArrayElements(env, jmessage, 0);
    jbyte *secret_key_native = (*env)->GetByteArrayElements(env, jsecret_key, 0);

    OQS_SIG *sig = (OQS_SIG *) getHandle(env, obj, "native_sig_handle_");
    size_t len_sig;
    
    OQS_STATUS rv_ = OQS_SIG_sign(sig, (uint8_t*)signature_native, &len_sig,
                                    (uint8_t*)message_native, message_len,
                                    (uint8_t*)secret_key_native);

    // fill java signature bytes
    (*env)->SetByteArrayRegion(env, jsignature, 0, len_sig, (jbyte*) signature_native);

    // fill java object signature length
    jfieldID value_fid = (*env)->GetFieldID(env,
                                    (*env)->GetObjectClass(env, sig_len_obj),
                                    "value", "Ljava/lang/Object;");
    jclass cls = (*env)->FindClass(env, "java/lang/Long");
    jobject jlong_obj = (*env)->NewObject(env, cls,
                                (*env)->GetMethodID(env, cls, "<init>", "(J)V"),
                                (jlong) len_sig);
    (*env)->SetObjectField(env, sig_len_obj, value_fid, jlong_obj);

    // Release C memory
    (*env)->ReleaseByteArrayElements(env, jsignature, signature_native, 0);
    (*env)->ReleaseByteArrayElements(env, jmessage, message_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jsecret_key, secret_key_native, JNI_ABORT);

    return (rv_ == OQS_SUCCESS) ? 0 : -1;
}

/*
 * Class:     org_openquantumsafe_Signature
 * Method:    verify
 * Signature: ([BJ[BJ[B)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_libqos_1android_Signature_verify
  (JNIEnv *env, jobject obj, jbyteArray jmessage, jlong message_len,
      jbyteArray jsignature, jlong signature_len, jbyteArray jpublic_key)
{
    // Convert to jbyte arrays
    jbyte *message_native = (*env)->GetByteArrayElements(env, jmessage, 0);
    jbyte *signature_native = (*env)->GetByteArrayElements(env, jsignature, 0);
    jbyte *public_key_native = (*env)->GetByteArrayElements(env, jpublic_key, 0);

    OQS_SIG *sig = (OQS_SIG *) getHandle(env, obj, "native_sig_handle_");
    OQS_STATUS rv_ = OQS_SIG_verify(sig, (uint8_t*) message_native, message_len,
                                    (uint8_t*) signature_native, signature_len,
                                    (uint8_t*) public_key_native);

    // Release C memory
    (*env)->ReleaseByteArrayElements(env, jsignature, signature_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jmessage, message_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jpublic_key, public_key_native, JNI_ABORT);

    return (rv_ == OQS_SUCCESS) ? JNI_TRUE : JNI_FALSE;
}

/*
 * Class:     org_openquantumsafe_Signature
 * Method:    sign_with_ctx_str
 * Signature: ([BLjava/lang/Long;[BJ[B)I
 */
JNIEXPORT jint JNICALL Java_com_example_libqos_1android_Signature_sign_1with_1ctx_1str
  (JNIEnv * env, jobject obj, jbyteArray jsignature, jobject sig_len_obj,
      jbyteArray jmessage, jlong message_len, jbyteArray jctx, jlong ctx_len,
      jbyteArray jsecret_key)
{
    // Convert to jbyte arrays
    jbyte *signature_native = (*env)->GetByteArrayElements(env, jsignature, 0);
    jbyte *message_native = (*env)->GetByteArrayElements(env, jmessage, 0);
    jbyte *ctx_native = (*env)->GetByteArrayElements(env, jctx, 0);
    jbyte *secret_key_native = (*env)->GetByteArrayElements(env, jsecret_key, 0);

    OQS_SIG *sig = (OQS_SIG *) getHandle(env, obj, "native_sig_handle_");
    size_t len_sig;
    OQS_STATUS rv_ = OQS_SIG_sign_with_ctx_str(sig, (uint8_t*)signature_native, &len_sig,
                                    (uint8_t*)message_native, message_len,
                                    (uint8_t*)ctx_native, ctx_len,
                                    (uint8_t*)secret_key_native);
                                    
    // fill java signature bytes
    (*env)->SetByteArrayRegion(env, jsignature, 0, len_sig, (jbyte*) signature_native);

    // fill java object signature length
    jfieldID value_fid = (*env)->GetFieldID(env,
                                    (*env)->GetObjectClass(env, sig_len_obj),
                                    "value", "Ljava/lang/Object;");
    jclass cls = (*env)->FindClass(env, "java/lang/Long");
    jobject jlong_obj = (*env)->NewObject(env, cls,
                                (*env)->GetMethodID(env, cls, "<init>", "(J)V"),
                                (jlong) len_sig);
    (*env)->SetObjectField(env, sig_len_obj, value_fid, jlong_obj);

    // Release C memory
    (*env)->ReleaseByteArrayElements(env, jsignature, signature_native, 0);
    (*env)->ReleaseByteArrayElements(env, jmessage, message_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jctx, ctx_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jsecret_key, secret_key_native, JNI_ABORT);

    return (rv_ == OQS_SUCCESS) ? 0 : -1;
}

/*
 * Class:     org_openquantumsafe_Signature
 * Method:    verify_with_ctx_str
 * Signature: ([BJ[BJ[B)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_libqos_1android_Signature_verify_1with_1ctx_1str
  (JNIEnv *env, jobject obj, jbyteArray jmessage, jlong message_len,
      jbyteArray jsignature, jlong signature_len, jbyteArray jctx, jlong ctx_len,
      jbyteArray jpublic_key)
{
    // Convert to jbyte arrays
    jbyte *message_native = (*env)->GetByteArrayElements(env, jmessage, 0);
    jbyte *signature_native = (*env)->GetByteArrayElements(env, jsignature, 0);
    jbyte *ctx_native = (*env)->GetByteArrayElements(env, jctx, 0);
    jbyte *public_key_native = (*env)->GetByteArrayElements(env, jpublic_key, 0);

    OQS_SIG *sig = (OQS_SIG *) getHandle(env, obj, "native_sig_handle_");
    OQS_STATUS rv_ = OQS_SIG_verify_with_ctx_str(sig, (uint8_t*) message_native, message_len,
                                    (uint8_t*) signature_native, signature_len,
                                    (uint8_t*) ctx_native, ctx_len,
                                    (uint8_t*) public_key_native);
 
    // Release C memory
    (*env)->ReleaseByteArrayElements(env, jsignature, signature_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jmessage, message_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jctx, ctx_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jpublic_key, public_key_native, JNI_ABORT);

    return (rv_ == OQS_SUCCESS) ? JNI_TRUE : JNI_FALSE;
}


// ------------------------------------------------------------
// TIMING HARNESS IMPLEMENTATION
// ------------------------------------------------------------

long get_nanos_diff(struct timespec start, struct timespec end) {
    return (long)((end.tv_sec - start.tv_sec) * 1000000000 + (end.tv_nsec - start.tv_nsec));
}

/*
 * Class:     com_example_libqos_1android_Signature
 * Method:    sign_with_timing_native  <-- NOTE THE NAME CHANGE
 * Signature: ([B[B)J
 */
JNIEXPORT jlong JNICALL Java_com_example_libqos_1android_Signature_sign_1with_1timing_1native
        (JNIEnv *env, jobject obj, jbyteArray jmessage, jbyteArray jsecret_key)
{
    // ... (The rest of the body is EXACTLY the same as before) ...

    // 1. Get Handle
    OQS_SIG *sig = (OQS_SIG *) getHandle(env, obj, "native_sig_handle_");

    // 2. Prepare Data
    jbyte *message_native = (*env)->GetByteArrayElements(env, jmessage, 0);
    jbyte *secret_key_native = (*env)->GetByteArrayElements(env, jsecret_key, 0);
    jsize message_len = (*env)->GetArrayLength(env, jmessage);

    uint8_t *signature_buffer = malloc(sig->length_signature);
    size_t signature_len_out;

    // 3. TIMER START
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC_RAW, &start);

    // 4. Crypto
    OQS_SIG_sign(sig, signature_buffer, &signature_len_out,
                 (uint8_t*)message_native, message_len,
                 (uint8_t*)secret_key_native);

    // 5. TIMER STOP
    clock_gettime(CLOCK_MONOTONIC_RAW, &end);

    // 6. Cleanup
    (*env)->ReleaseByteArrayElements(env, jmessage, message_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jsecret_key, secret_key_native, JNI_ABORT);
    free(signature_buffer);

    return (jlong) get_nanos_diff(start, end);
}

/*
 * Class:     com_example_libqos_1android_Signature
 * Method:    keypair_with_timing_native
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_example_libqos_1android_Signature_keypair_1with_1timing_1native
        (JNIEnv *env, jobject obj)
{
    OQS_SIG *sig = (OQS_SIG *) getHandle(env, obj, "native_sig_handle_");

    // 1. Allocate Temporary Memory (We discard keys after measuring)
    uint8_t *public_key = malloc(sig->length_public_key);
    uint8_t *secret_key = malloc(sig->length_secret_key);

    // Safety check
    if (public_key == NULL || secret_key == NULL) {
        if (public_key) free(public_key);
        if (secret_key) free(secret_key);
        return -1;
    }

    // 2. TIMING
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC_RAW, &start);

    OQS_SIG_keypair(sig, public_key, secret_key);

    clock_gettime(CLOCK_MONOTONIC_RAW, &end);

    // 3. Cleanup
    free(public_key);
    free(secret_key);

    return (jlong) get_nanos_diff(start, end);
}

/*
 * Class:     com_example_libqos_1android_Signature
 * Method:    verify_with_timing_native
 * Signature: ([B[B[B)J
 */
JNIEXPORT jlong JNICALL Java_com_example_libqos_1android_Signature_verify_1with_1timing_1native
        (JNIEnv *env, jobject obj, jbyteArray jmessage, jbyteArray jsignature, jbyteArray jpublic_key)
{
    OQS_SIG *sig = (OQS_SIG *) getHandle(env, obj, "native_sig_handle_");

    // 1. Prepare Pointers
    // (Doing this OUTSIDE the timer to exclude JNI overhead)
    jbyte *message_native = (*env)->GetByteArrayElements(env, jmessage, 0);
    jbyte *signature_native = (*env)->GetByteArrayElements(env, jsignature, 0);
    jbyte *public_key_native = (*env)->GetByteArrayElements(env, jpublic_key, 0);

    jsize message_len = (*env)->GetArrayLength(env, jmessage);
    jsize signature_len = (*env)->GetArrayLength(env, jsignature);

    // 2. TIMING
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC_RAW, &start);

    // We don't check the return value (success/fail) because we only care about SPEED.
    OQS_SIG_verify(sig,
                   (uint8_t*)message_native, message_len,
                   (uint8_t*)signature_native, signature_len,
                   (uint8_t*)public_key_native);

    clock_gettime(CLOCK_MONOTONIC_RAW, &end);

    // 3. Cleanup
    (*env)->ReleaseByteArrayElements(env, jmessage, message_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jsignature, signature_native, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, jpublic_key, public_key_native, JNI_ABORT);

    return (jlong) get_nanos_diff(start, end);
}

