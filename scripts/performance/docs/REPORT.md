# Performance Analysis Results

This document provides the complete set of extended performance visualizations referenced in **Chapter 5** of the thesis: *Analysis of Transition to Post-Quantum Cryptography on Mobile Platforms*.

The evaluation characterizes the runtime cost of Key Encapsulation Mechanisms (KEMs) and Digital Signature Algorithms (DSAs) at NIST security levels 3 and 5. Measurements on Android were collected using the Jetpack Microbenchmark framework, which automatically handles JVM warmup and thermal throttling. iOS measurements were collected using XCTest. For the complete evaluation methodology, hardware specifications, and detailed discussion of these results, please refer to the main thesis document.

---

## 1. Absolute Performance Heatmaps (Android)

The following heatmaps visualize the absolute median execution times of the evaluated post-quantum primitives on Android. The color gradient utilizes a logarithmic scale: darker green indicates faster execution times (microseconds), while darker red indicates slower execution times (milliseconds).

### Key Encapsulation Mechanisms (KEMs)

![Android liboqs KEM Heatmap](plots/heatmaps/heatmap_android_liboqs_kem.png)
*Figure S1: Absolute median execution times of KEM operations on Android utilizing the native `liboqs` C library.*

![Android Bouncy Castle KEM Heatmap](plots/heatmaps/heatmap_android_bouncycastle_kem.png)
*Figure S2: Absolute median execution times of KEM operations on Android utilizing the pure-Java `Bouncy Castle` library.*

### Digital Signature Algorithms (DSAs)

![Android liboqs DSA Heatmap](plots/heatmaps/heatmap_android_liboqs_dsa.png)
*Figure S3: Absolute median execution times of DSA operations on Android utilizing the native `liboqs` C library.*

![Android Bouncy Castle DSA Heatmap](plots/heatmaps/heatmap_android_bouncycastle_dsa.png)
*Figure S4: Absolute median execution times of DSA operations on Android utilizing the pure-Java `Bouncy Castle` library.*

---

## 2. Relative Performance to NIST Baselines (Android)

To better contextualize the overhead introduced by alternative and fallback algorithms, the following charts normalize execution times against the finalized NIST Category 3 standards (ML-KEM-768 for key encapsulation and ML-DSA-65 for digital signatures) within the same library.

The x-axis utilizes a logarithmic scale. The multiplier inside each bar indicates how many times slower the operation is compared to the baseline standard.

### Key Encapsulation Mechanisms (KEMs)

![Android liboqs KEM Relative](plots/normalized/normalized_android_liboqs_kem.png)
*Figure S5: Execution time of KEM operations normalized to ML-KEM-768 utilizing `liboqs`.*

![Android Bouncy Castle KEM Relative](plots/normalized/normalized_android_bouncycastle_kem.png)
*Figure S6: Execution time of KEM operations normalized to ML-KEM-768 utilizing `Bouncy Castle`.*

### Digital Signature Algorithms (DSAs)

![Android liboqs DSA Relative](plots/normalized/normalized_android_liboqs_dsa.png)
*Figure S7: Execution time of DSA operations normalized to ML-DSA-65 utilizing `liboqs`. CROSS and MAYO remain highly competitive, while UOV and hash-based schemes introduce severe latency penalties.*

![Android Bouncy Castle DSA Relative](plots/normalized/normalized_android_bouncycastle_dsa.png)
*Figure S8: Execution time of DSA operations normalized to ML-DSA-65 utilizing `Bouncy Castle`.*

---

## 3. Comparative Speedup Analysis (Android)

To evaluate the relative efficiency of the underlying software implementations, the following bar charts illustrate the performance speedup of the native `liboqs` library compared to `Bouncy Castle`.

The metric displayed is the speedup ratio ($time_{BC} / time_{liboqs}$).
* A ratio **> 1** indicates that `liboqs` is faster.
* A ratio **< 1** indicates that `Bouncy Castle` is faster.
* The red dashed line represents equal performance (1.0x).

![Android KEM Speedup Level 3](plots/speedup/speedup_android_kem_liboqs_vs_bouncycastle.png)
*Figure S9: Relative speedup of KEM operations at security level 3 on Android. Note the prominent performance advantage of `liboqs` for FrodoKEM-AES due to hardware-accelerated symmetric primitives, and Bouncy Castle's faster execution for HQC.*

![Android DSA Speedup Level 5](plots/speedup/speedup_android_dsa_liboqs_vs_bouncycastle.png)
*Figure S10: Relative speedup of DSA operations at security level 5 on Android. While `liboqs` generally outperforms the Java baseline, Bouncy Castle signs notably faster for Falcon-1024, likely due to a lack of strict constant-time protections.*

---

## 4. iOS Performance Evaluation

The iOS platform was evaluated using the ML-KEM and ML-DSA algorithms supported natively by Apple's `CryptoKit` framework (iOS 26.0+). The charts below compare the raw C performance of `liboqs` against the hardware-backed `CryptoKit` implementation.


![iOS KEM Speedup](plots/speedup/speedup_ios_kem_liboqs_vs_cryptokit.png)
*Figure S11: Relative speedup of KEM operations on iOS (`liboqs` vs `CryptoKit`).*

![iOS DSA Speedup](plots/speedup/speedup_ios_dsa_liboqs_vs_cryptokit.png)
*Figure S12: Relative speedup of DSA operations on iOS (`liboqs` vs `CryptoKit`).*