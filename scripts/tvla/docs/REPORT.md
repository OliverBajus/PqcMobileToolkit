# Security Analysis (TVLA) Results

This document provides the complete set of incremental Test Vector Leakage Assessment (TVLA) plots referenced in **Chapter 6** of the thesis.

**Note:** The side-channel security evaluation was performed exclusively on the **Android** platform. Measurements were captured using a custom JNI timing harness for `liboqs` and standard JVM timing methods for `Bouncy Castle`. All tests utilize a refined "symmetric in-loop generation" methodology to eliminate microarchitectural cache-eviction artifacts caused by post-quantum key generation.

### How to Read the Visualizations

TVLA is a statistical methodology utilizing Welch's t-test to detect the presence of timing side-channel leakage without requiring knowledge of the secret values.
* **The x-axis** represents the number of analyzed timing traces.
* **The y-axis** represents the absolute t-value ($|t|$).
* **The dashed black line** indicates the standard leakage threshold of **$|t| = 4.5$**.

An implementation is considered to exhibit potential side-channel leakage if the t-value crosses the 4.5 threshold with a sustained, monotonically growing trend as the trace count increases. If the trace remains below the threshold, no statistically significant leakage is detected. *(Note: The high noise floor of the Android OS—driven by JIT compilation, garbage collection, and scheduling—approximates the capabilities of a local software attacker, but may mask known theoretical hardware-level leakages).*

---

## 1. Evaluation of `liboqs`

The following plots show the software-level timing leakage assessment for the native C implementations integrated via the `liboqs-android` JNI wrapper.

### 1.1 Key Encapsulation Mechanisms (KEMs)

The KEM decapsulation tests evaluate potential leakage dependent on the input ciphertext (valid vs. invalid, or fixed vs. random).

![liboqs ML-KEM Decapsulation](plots/)
*Figure S1: Ciphertext-dependent TVLA for ML-KEM-768 decapsulation utilizing `liboqs`.*

![liboqs FrodoKEM Decapsulation](plots/)
*Figure S2: Ciphertext-dependent TVLA for FrodoKEM-976 decapsulation utilizing `liboqs`.*

*(Add additional KEM plots for liboqs here...)*

### 1.2 Digital Signature Algorithms (DSAs)

The DSA signing tests evaluate potential leakage dependent on the message being signed or the secret key being used.

![liboqs ML-DSA Signing](plots/)
*Figure S3: Message/Key-dependent TVLA for ML-DSA-65 signing utilizing `liboqs`.*

![liboqs Falcon Signing](plots/)
*Figure S4: Key-dependent TVLA for Falcon-1024 signing utilizing `liboqs`. No sustained leakage is detected, though theoretical floating-point leakages may fall below the Android software noise floor.*

*(Add additional DSA plots for liboqs here, such as CROSS, MAYO, OV-III, SLH-DSA...)*

---

## 2. Evaluation of `Bouncy Castle`

The following plots show the software-level timing leakage assessment for the pure-Java implementations provided by the `Bouncy Castle` library. Note that the JVM measurement layer (`System.nanoTime()`) introduces substantially higher variance than native timing.

### 2.1 Key Encapsulation Mechanisms (KEMs)

![Bouncy Castle ML-KEM Decapsulation](plots/BouncyCML)
*Figure S5: Ciphertext-dependent TVLA for ML-KEM-768 decapsulation utilizing `Bouncy Castle`.*

![Bouncy Castle HQC Decapsulation](path/to/bc_hqc_kem.png)
*Figure S6: Ciphertext-dependent TVLA for HQC-192 decapsulation utilizing `Bouncy Castle`.*

*(Add additional KEM plots for Bouncy Castle here...)*

### 2.2 Digital Signature Algorithms (DSAs)

![Bouncy Castle ML-DSA Signing](path/to/bc_mldsa_dsa.png)
*Figure S7: Message/Key-dependent TVLA for ML-DSA-65 signing utilizing `Bouncy Castle`.*

![Bouncy Castle SLH-DSA Signing](path/to/bc_slhdsa_dsa.png)
*Figure S8: Message-dependent TVLA for SLH-DSA signing utilizing `Bouncy Castle`.*

*(Add additional DSA plots for Bouncy Castle here...)*
