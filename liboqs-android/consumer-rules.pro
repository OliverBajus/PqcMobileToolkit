# liboqs-android consumer ProGuard rules
# These rules are automatically applied to apps that depend on this library.

# Keep all classes with native methods (JNI)
-keepclasseswithmembers class * {
    native <methods>;
}

# Keep the public API interfaces and models
-keep class io.github.oliverbajus.liboqs_android.api.** { *; }

# Keep the Oqs singleton entry point
-keep class io.github.oliverbajus.liboqs_android.Oqs { *; }

# Keep KEM/Sig discovery singletons
-keep class io.github.oliverbajus.liboqs_android.kem.KEMs { *; }
-keep class io.github.oliverbajus.liboqs_android.sig.Sigs { *; }

# Keep exception classes (needed for catch blocks)
-keep class io.github.oliverbajus.liboqs_android.api.exceptions.** { *; }

# Keep algorithm sealed interface implementations (used via reflection in some cases)
-keep class io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm$Kem$* { *; }
-keep class io.github.oliverbajus.liboqs_android.api.model.PqcAlgorithm$Sig$* { *; }
