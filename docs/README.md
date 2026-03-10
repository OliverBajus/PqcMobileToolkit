# PqcDemoApp -- Post-Quantum Cryptography Demo for Android

A multi-module Android application for benchmarking post-quantum cryptographic
algorithms on mobile devices. The project compares two independent PQC backends
-- **liboqs** (C via JNI) and **Bouncy Castle** (pure Java) -- through a unified
Kotlin API.

## Project structure

```
PqcDemoApp/
├── app/                     # Demo application (MVVM + Clean Architecture)
│   └── docs/README.md       # App-level documentation
├── libqos-android/          # Android library wrapping liboqs via JNI
│   └── docs/README.md       # Library-level documentation
├── docs/                    # This directory (project-level docs)
│   └── README.md
├── build.gradle.kts         # Root Gradle build
└── settings.gradle.kts
```

## Module documentation

| Module | Description | Docs |
|---|---|---|
| **[libqos-android](../libqos-android/docs/README.md)** | Kotlin/Java bindings for the Open Quantum Safe (liboqs) C library. Exposes KEM and signature APIs with native timing support. | [libqos-android/docs/README.md](../libqos-android/docs/README.md) |
| **[app](../app/docs/README.md)** | Jetpack Compose demo app that benchmarks PQC algorithms using both liboqs and Bouncy Castle backends. | [app/docs/README.md](../app/docs/README.md) |

## Requirements

- Android Studio Hedgehog or newer
- Android NDK 28.0.13004108
- Min SDK 26 (Android 8.0)
- Target device: `arm64-v8a` or `x86_64`
