# app -- PQC Demo Application

Android demo application for benchmarking post-quantum cryptographic algorithms.
The app lets users select a PQC library backend (**liboqs** via JNI or
**Bouncy Castle** via pure Java) and run KEM or digital-signature flows with
real-time timing measurements displayed in a log view.

## Architecture

The application follows **MVVM + Clean Architecture** with three layers:

```
┌─────────────────────────────────┐
│  Presentation                   │
│  MainActivity, MainScreen,      │
│  MainViewModel (Jetpack Compose)│
└──────────────┬──────────────────┘
               │ StateFlow / Use Cases
┌──────────────▼──────────────────┐
│  Domain                         │
│  PqcRepository, Use Cases,      │
│  PqcRepositoryRouter, Models    │
└──────────────┬──────────────────┘
               │ Hilt DI
┌──────────────▼──────────────────┐
│  Data                           │
│  OqsPqcRepository (liboqs JNI)  │
│  BcPqcRepository  (Bouncy Castle│
└─────────────────────────────────┘
```

### Presentation layer

| Component | Description |
|---|---|
| `MainActivity` | Hilt-injected entry point; hosts the Compose UI |
| `MainScreen` | Material 3 Compose screen with library picker, algorithm pickers, log view, and action buttons |
| `MainViewModel` | Holds UI state as `StateFlow`; dispatches use cases on `Dispatchers.Default` |

### Domain layer

| Component | Description |
|---|---|
| `PqcRepository` | Interface defining `supportedKems()`, `supportedSigs()`, `runKemFlow()`, `runSigFlow()` |
| `PqcRepositoryRouter` | Routes calls to the OQS or BC repository based on the selected `PqcLibrary` |
| `RunKemFlow` / `RunSigFlow` | Use cases that invoke the repository and return `Either<Failure, Result>` |
| `GetSupportedKemAlgorithms` / `GetSupportedSigAlgorithms` | Use cases for algorithm discovery |
| `AlgChoice` | Simple `id + name` model exposed to the UI |
| `KemResult` / `SigResult` | Timing results (keygen, encaps/sign, decaps/verify in nanoseconds) + success flag |
| `PqcFailure` | Sealed class (`UnsupportedAlgorithm`, `PqcError`) for error handling |

### Data layer

| Component | Description |
|---|---|
| `OqsPqcRepository` | Delegates to `Oqs.createKemManager()` / `createSignatureManager()` from the **liboqs-android** module |
| `BcPqcRepository` | Uses Bouncy Castle (`org.bouncycastle.pqc.crypto.*`) for KEM and signature operations |
| `BcFactory` | Factory that creates `BcKemManager` / `BcSigManager` for a given BC algorithm enum |
| `BcKemManager` / `BcSigManager` | Wrappers around Bouncy Castle's generator/signer APIs |

## Dependency injection (Hilt)

```
@HiltAndroidApp  App
│
├── DataModule    Binds OqsPqcRepository (@OqsRepo) and BcPqcRepository (@BcRepo)
└── PQCModule     Provides SecureRandom singleton
```

The `@OqsRepo` and `@BcRepo` qualifiers distinguish the two `PqcRepository` bindings.

## Data flow

```
User taps "Test KEM"
  → MainViewModel.runFullKemFlow()
    → RunKemFlow(library, algorithmId)
      → PqcRepositoryRouter.repoFor(library)
        ├── OQS: OqsPqcRepository.runKemFlow()
        │         → Oqs.createKemManager() → JNI → liboqs C
        └── BC:  BcPqcRepository.runKemFlow()
                  → BcFactory.createKemManager() → Bouncy Castle Java
      → Either<PqcFailure, KemResult>
    → ViewModel appends timing log to logText StateFlow
  → MainScreen observes and renders
```

## Supported algorithms

### Via liboqs (JNI)

All algorithms listed in `KEMs.supportedAlgorithms()` and `Sigs.supportedAlgorithms()`.
See [liboqs-android docs](../libqos-android/docs/README.md) for the full list.

### Via Bouncy Castle (Java)

**KEM**: ML-KEM-768/1024, HQC-192/256, FrodoKEM-976/1344 (AES & SHAKE)

**Signatures**: ML-DSA-65/87, Falcon-1024, MAYO-3/5, SNOVA variants, SLH-DSA SHA2/SHAKE (192/256, fast/small)

## Build configuration

| Property | Value |
|---|---|
| Package | `cz.monetplus.pqcdemoapp` |
| Compile SDK | 35 |
| Min SDK | 26 |
| Java target | 21 |
| Compose | BOM-managed |
| DI | Hilt (kapt) |

### Key dependencies

- `project(":libqos-android")` -- local liboqs JNI bindings
- `org.bouncycastle:bcpkix-jdk18on` -- Bouncy Castle PQC
- Jetpack Compose + Material 3
- Hilt for dependency injection
- `ahead.functional` / `ahead.domain` -- `Either` monad and `BaseUseCase`
