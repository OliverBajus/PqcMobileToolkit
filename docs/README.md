# PqcMobileToolkit

An open-source Android toolkit consolidating all PQC engineering artifacts
produced in the thesis *Analysis of Transition to Post-Quantum Cryptography on
Mobile Platforms*. It serves as a practical reference for developers and
researchers undertaking PQC migration on Android.

> ⚠️ **Note:** This toolkit is intended for **research and prototyping purposes only**. It is not recommended for use in production systems.

> **Note:** iOS artifacts (Swift wrapper, build scripts, prototype application)
> are maintained in a separate repository: [OliverBajus/iOSPqcDemoApp](https://github.com/OliverBajus/iOSPqcDemoApp).

## Main Component: `liboqs-android`

[![Maven Central](https://img.shields.io/maven-central/v/io.github.oliverbajus/liboqs-android)](https://central.sonatype.com/artifact/io.github.oliverbajus/liboqs-android)
[![CI](https://github.com/OliverBajus/PqcDemoApp/actions/workflows/liboqs-android-ci.yml/badge.svg?branch=main)](https://github.com/OliverBajus/PqcDemoApp/actions/workflows/liboqs-android-ci.yml)
[![Coverage](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/OliverBajus/8df5727e960d985d6ac8b5351057b923/raw/coverage.json)](https://github.com/OliverBajus/PqcDemoApp/actions/workflows/liboqs-android-ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![liboqs](https://img.shields.io/badge/liboqs-0.15.0-blue)](https://github.com/open-quantum-safe/liboqs/releases/tag/0.15.0)
[![Min SDK](https://img.shields.io/badge/minSdk-26-brightgreen)](https://developer.android.com/about/versions/oreo)
[![Compile SDK](https://img.shields.io/badge/compileSdk-36-brightgreen)](https://developer.android.com/about/versions/16)

The primary artifact of this toolkit is **`liboqs-android`**, an Android library (AAR) wrapping the [Open Quantum Safe (liboqs)](https://github.com/open-quantum-safe/liboqs) C library. It is a substantial rewrite of the official Java wrapper [liboqs-java](https://github.com/open-quantum-safe/liboqs-java), designed specifically for Android mobile environments.

**Library documentation is available at [liboqs-android/docs/README.md](../liboqs-android/docs/README.md).**

### Installation

**Kotlin DSL** (`build.gradle.kts`):
```kotlin
implementation("io.github.oliverbajus:liboqs-android:0.1.0")
```

**Groovy DSL** (`build.gradle`):
```groovy
implementation 'io.github.oliverbajus:liboqs-android:0.1.0'
```
## Other Components:
The toolkit also integrates the following components:

- **Build pipeline** -- reproducible Android NDK cross-compilation scripts for
  liboqs and OpenSSL.
- **Benchmark module** -- Jetpack Microbenchmark harness with pre-configured
  performance and TVLA test suites covering all evaluated algorithms across
  security levels 3 and 5.
- **TVLA pipeline** -- timing-based leakage assessment framework, including the
  native JNI timing harness, interleaved measurement loop, and incremental
  t-test analysis scripts.
- **Prototype application** -- Android application (Kotlin/Compose)
  demonstrating end-to-end PQC integration with both liboqs and Bouncy Castle.

## Project structure

```
PqcMobileToolkit/
├── app/                         # Prototype application (MVVM + Clean Architecture)
│   └── docs/README.md
├── liboqs-android/              # Android library wrapping liboqs via JNI
│   └── docs/README.md
├── benchmark/                   # Performance & TVLA instrumented test suites
├── scripts/
│   ├── compilation/             # liboqs NDK cross-compilation scripts
│   │   └── docs/README.md
│   ├── performance/             # Performance analysis Python scripts
│   │   └── docs/README.md
│   └── tvla/                    # TVLA analysis Python scripts
│       └── docs/README.md
├── docs/                        # This directory (project-level docs)
│   └── README.md
├── build.gradle.kts
└── settings.gradle.kts
```

## Module documentation

| Module                                                           | Description | Docs                                                                        |
|------------------------------------------------------------------|---|-----------------------------------------------------------------------------|
| **[liboqs-android](../liboqs-android/docs/README.md)**           | Kotlin/Java bindings for the Open Quantum Safe (liboqs) C library. Exposes KEM and signature APIs with native timing support. Distributed as an AAR. | [liboqs-android/docs/README.md](../liboqs-android/docs/README.md)           |
| **[app](../app/docs/README.md)**                                 | Jetpack Compose prototype app demonstrating end-to-end PQC integration using both liboqs and Bouncy Castle backends. | [app/docs/README.md](../app/docs/README.md)                                 |
| **benchmark**                                                    | Jetpack Microbenchmark module with performance and TVLA test suites for both libraries. | —                                                                           |
| **[scripts/compilation](../scripts/compilation/docs/README.md)** | Shell scripts for cross-compiling liboqs (with optional OpenSSL) for Android via NDK. | [scripts/compilation/docs/README.md](../scripts/compilation/docs/README.md) |
| **[scripts/performance](../scripts/performance/docs/README.md)** | Python script generating benchmark visualization charts from PQC performance data. | [scripts/performance/docs/README.md](../scripts/performance/docs/README.md) |
| **[scripts/tvla](../scripts/tvla/docs/README.md)**               | Python script performing Welch's t-test on PQC timing traces to detect timing side-channel leakage. | [scripts/tvla/docs/README.md](../scripts/tvla/docs/README.md)               |

## License

This project is licensed under the **MIT License** — see [LICENSE](../LICENSE) for details.

The `liboqs-android` module bundles pre-compiled binaries from [liboqs](https://github.com/open-quantum-safe/liboqs), which is primarily MIT-licensed but includes third-party components under various open-source licenses (Apache 2.0, BSD 3-Clause, CC0, and others). See the [liboqs LICENSE](https://github.com/open-quantum-safe/liboqs/blob/main/LICENSE.txt), [liboqs README](https://github.com/open-quantum-safe/liboqs/blob/main/README.md#license) and [liboqs-java README](https://github.com/open-quantum-safe/liboqs-java?tab=readme-ov-file#license) for full details.

## Requirements
- Android NDK 28.0.13004108
- Min SDK 26 (Android 8.0)
- Target device: `arm64-v8a`, `x86_64`, `armeabi-v7a`
