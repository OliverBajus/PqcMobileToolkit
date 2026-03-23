# app -- PQC Demo Application

Android application demonstrating end-to-end post-quantum cryptographic
integration with a modular architecture that allows swapping between two
independent PQC library backends -- **liboqs** (C via JNI) and **Bouncy Castle**
(pure Java). Users select a backend and algorithm, then run KEM or
digital-signature flows with real-time timing measurements. The app served as
the execution platform for all performance benchmarks and TVLA measurements.

## Architecture

The application follows [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
combined with the MVVM presentation pattern.

<p align="center">
  <img src="CleanArchitecture.jpg" alt="Clean Architecture diagram" width="60%" />
</p>

The core idea is the **Dependency Rule**: source code dependencies always point
inward. The inner layers (Domain) know nothing about the outer layers (Data,
Presentation). This makes the business logic independent of frameworks, UI, and
external libraries.

In this application the layers map as follows:

- **Domain (Entities + Use Cases)** -- defines the `PqcRepository` interface and
  use cases (`RunKemFlow`, `RunSigFlow`, `GetSupportedKemAlgorithms`, …).
  Domain models (`KemResult`, `SigResult`, `PqcFailure`) live here. This layer
  has zero Android or library dependencies.

- **Data (Interface Adapters)** -- contains concrete `PqcRepository`
  implementations: `OqsPqcRepository` delegates to the liboqs-android JNI
  module, `BcPqcRepository` delegates to Bouncy Castle. A
  `PqcRepositoryRouter` selects the active backend at runtime. Hilt wires the
  implementations via `@OqsRepo` / `@BcRepo` qualifiers.

- **Presentation (Frameworks & Drivers)** -- Jetpack Compose UI with a
  `MainViewModel` exposing state via `StateFlow`. The ViewModel dispatches
  use cases and the Compose screen observes and renders results.

Because the domain layer only depends on its own interfaces, adding a new PQC
backend (e.g. a future Android Keystore provider) requires only a new
`PqcRepository` implementation without touching business logic or UI.

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
