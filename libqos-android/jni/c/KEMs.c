#include <oqs/oqs.h>
#include "KEMs.h"

/*
 * Class:     org_openquantumsafe_KEMs
 * Method:    max_number_KEMs
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_libqos_1android_kem_KEMs_maxNumberKEMs
  (JNIEnv *env, jclass cls)
{
    return (jint) OQS_KEM_alg_count();
}

/*
 * Class:     org_openquantumsafe_KEMs
 * Method:    is_KEM_enabled
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_libqos_1android_kem_KEMs_isKemEnabled
  (JNIEnv *env, jclass cls, jstring java_str)
{
	const char *str_native = (*env)->GetStringUTFChars(env, java_str, 0);
    int is_enabled = OQS_KEM_alg_is_enabled (str_native);
	(*env)->ReleaseStringUTFChars(env, java_str, str_native);
    return (is_enabled) ? JNI_TRUE : JNI_FALSE;
}

/*
 * Class:     org_openquantumsafe_KEMs
 * Method:    get_KEM_name
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_example_libqos_1android_kem_KEMs_getKemName
  (JNIEnv *env, jclass cls, jlong alg_id)
{
    const char *str_native = OQS_KEM_alg_identifier((size_t) alg_id);
    return (*env)->NewStringUTF(env, str_native);
}

