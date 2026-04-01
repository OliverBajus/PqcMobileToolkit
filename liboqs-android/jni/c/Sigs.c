#include <oqs/oqs.h>
#include "Sigs.h"

/*
 * Class:     org_openquantumsafe_Sigs
 * Method:    max_number_sigs
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_io_github_oliverbajus_liboqs_1android_sig_Sigs_maxNumberSigs
  (JNIEnv *env, jclass cls)
{
    return (jint) OQS_SIG_alg_count();
}

/*
 * Class:     org_openquantumsafe_Sigs
 * Method:    is_sig_enabled
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_io_github_oliverbajus_liboqs_1android_sig_Sigs_isSigEnabled
  (JNIEnv *env, jclass cls, jstring jstr)
{
    const char *str_native = (*env)->GetStringUTFChars(env, jstr, 0);
    int is_enabled = OQS_SIG_alg_is_enabled (str_native);
	(*env)->ReleaseStringUTFChars(env, jstr, str_native);
    return (is_enabled) ? JNI_TRUE : JNI_FALSE;
}

/*
 * Class:     org_openquantumsafe_Sigs
 * Method:    get_sig_name
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_github_oliverbajus_liboqs_1android_sig_Sigs_getSigName
  (JNIEnv *env, jclass cls, jlong alg_id)
{
    const char *str_native = OQS_SIG_alg_identifier((size_t) alg_id);
    return (*env)->NewStringUTF(env, str_native);
}
