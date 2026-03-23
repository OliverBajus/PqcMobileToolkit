# PqcMobileToolkit

An open-source Android toolkit consolidating all PQC engineering artifacts
produced in the thesis *Analysis of Transition to Post-Quantum Cryptography on
Mobile Platforms*. It serves as a practical reference for developers and
researchers undertaking PQC migration on Android.

The toolkit integrates the following components:

- **liboqs-android** -- [Open Quantum Safe (liboqs)](https://github.com/open-quantum-safe/liboqs)  wrapper as Android library (AAR) combining the
  cross-compiled native liboqs binary with a rewritten [liboqs-java](https://github.com/open-quantum-safe/liboqs-java) wrapper,
  consumable as a single Gradle dependency.
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

> **Note:** iOS artifacts (Swift wrapper, build scripts, prototype application)
> are maintained in a separate repository.

## Project structure

```
PqcMobileToolkit/
├── app/                         # Prototype application (MVVM + Clean Architecture)
│   └── docs/README.md
├── libqos-android/              # Android library wrapping liboqs via JNI
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

| Module | Description | Docs |
|---|---|---|
| **[libqos-android](../libqos-android/docs/README.md)** | Kotlin/Java bindings for the Open Quantum Safe (liboqs) C library. Exposes KEM and signature APIs with native timing support. Distributed as an AAR. | [libqos-android/docs/README.md](../libqos-android/docs/README.md) |
| **[app](../app/docs/README.md)** | Jetpack Compose prototype app demonstrating end-to-end PQC integration using both liboqs and Bouncy Castle backends. | [app/docs/README.md](../app/docs/README.md) |
| **benchmark** | Jetpack Microbenchmark module with performance and TVLA test suites for both libraries. | — |
| **[scripts/compilation](../scripts/compilation/docs/README.md)** | Shell scripts for cross-compiling liboqs (with optional OpenSSL) for Android via NDK. | [scripts/compilation/docs/README.md](../scripts/compilation/docs/README.md) |
| **[scripts/performance](../scripts/performance/docs/README.md)** | Python script generating benchmark visualization charts from PQC performance data. | [scripts/performance/docs/README.md](../scripts/performance/docs/README.md) |
| **[scripts/tvla](../scripts/tvla/docs/README.md)** | Python script performing Welch's t-test on PQC timing traces to detect timing side-channel leakage. | [scripts/tvla/docs/README.md](../scripts/tvla/docs/README.md) |

## License

This project is licensed under the **MIT License** — see [LICENSE](../LICENSE) for details.

The `liboqs-android` module bundles pre-compiled binaries from [liboqs](https://github.com/open-quantum-safe/liboqs), which is primarily MIT-licensed but includes third-party components under various open-source licenses (Apache 2.0, BSD 3-Clause, CC0, and others). See the [liboqs LICENSE](https://github.com/open-quantum-safe/liboqs/blob/main/LICENSE.txt) and [liboqs README](https://github.com/open-quantum-safe/liboqs/blob/main/README.md#license) for full details.

## Requirements

- Android Studio Hedgehog or newer
- Android NDK 28.0.13004108
- Min SDK 26 (Android 8.0)
- Target device: `arm64-v8a` or `x86_64`
