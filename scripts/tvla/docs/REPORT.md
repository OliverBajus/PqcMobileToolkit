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

<table>
<tr>
<td align="center"><img src="plots/LibOQS_ML-KEM-768_ciphertext_fixed_vs_random_TVLA_plot.png" /><br><em>Figure S1a: Fixed-vs-random ciphertext TVLA for ML-KEM-768 decapsulation utilizing <code>liboqs</code>.</em></td>
<td align="center"><img src="plots/LibOQS_ML-KEM-768_ciphertext_valid_vs_invalid_TVLA_plot.png" /><br><em>Figure S1b: Valid-vs-invalid ciphertext TVLA for ML-KEM-768 decapsulation utilizing <code>liboqs</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/LibOQS_FrodoKEM-976-SHAKE_ciphertext_fixed_vs_random_TVLA_plot.png" /><br><em>Figure S2a: Fixed-vs-random ciphertext TVLA for FrodoKEM-976-SHAKE decapsulation utilizing <code>liboqs</code>.</em></td>
<td align="center"><img src="plots/LibOQS_FrodoKEM-976-SHAKE_ciphertext_valid_vs_invalid_TVLA_plot.png" /><br><em>Figure S2b: Valid-vs-invalid ciphertext TVLA for FrodoKEM-976-SHAKE decapsulation utilizing <code>liboqs</code>.</em></td>
</tr>
</table>

![liboqs HQC-192 Decapsulation](plots/LibOQS_HQC-192_ciphertext_fixed_vs_random_TVLA_plot.png)
*Figure S3: Fixed-vs-random ciphertext TVLA for HQC-192 decapsulation utilizing `liboqs`.*

---

### 1.2 Digital Signature Algorithms (DSAs)

The DSA signing tests evaluate potential leakage dependent on the message being signed or the secret key being used.

<table>
<tr>
<td align="center"><img src="plots/LibOQS_ML-DSA-65_key_TVLA_plot.png" /><br><em>Figure S4a: Key-dependent TVLA for ML-DSA-65 signing utilizing <code>liboqs</code>.</em></td>
<td align="center"><img src="plots/LibOQS_ML-DSA-65_message_TVLA_plot.png" /><br><em>Figure S4b: Message-dependent TVLA for ML-DSA-65 signing utilizing <code>liboqs</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/LibOQS_Falcon-1024_key_TVLA_plot_final.png" /><br><em>Figure S5a: Key-dependent TVLA for Falcon-1024 signing utilizing <code>liboqs</code>.</em></td>
<td align="center"><img src="plots/LibOQS_Falcon-1024_message_TVLA_plot.png" /><br><em>Figure S5b: Message-dependent TVLA for Falcon-1024 signing utilizing <code>liboqs</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/LibOQS_MAYO-3_key_TVLA_plot.png" /><br><em>Figure S6a: Key-dependent TVLA for MAYO-3 signing utilizing <code>liboqs</code>.</em></td>
<td align="center"><img src="plots/LibOQS_MAYO-3_message_TVLA_plot.png" /><br><em>Figure S6b: Message-dependent TVLA for MAYO-3 signing utilizing <code>liboqs</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/LibOQS_OV-III_key_TVLA_plot_final_50k.png" /><br><em>Figure S7a: Key-dependent TVLA for OV-III signing utilizing <code>liboqs</code>.</em></td>
<td align="center"><img src="plots/LibOQS_OV-III_message_TVLA_plot.png" /><br><em>Figure S7b: Message-dependent TVLA for OV-III signing utilizing <code>liboqs</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/LibOQS_SLH-DSA-PURE-SHA2-192F_key_TVLA_plot.png" /><br><em>Figure S8a: Key-dependent TVLA for SLH-DSA-SHA2-192F signing utilizing <code>liboqs</code>.</em></td>
<td align="center"><img src="plots/LibOQS_SLH-DSA-PURE-SHA2-192F_message_TVLA_plot.png" /><br><em>Figure S8b: Message-dependent TVLA for SLH-DSA-SHA2-192F signing utilizing <code>liboqs</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/LibOQS_cross-rsdpg-192-fast_key_TVLA_plot.png" /><br><em>Figure S9a: Key-dependent TVLA for CROSS-RSDPG-192-fast signing utilizing <code>liboqs</code>.</em></td>
<td align="center"><img src="plots/LibOQS_cross-rsdpg-192-fast_message_TVLA_plot.png" /><br><em>Figure S9b: Message-dependent TVLA for CROSS-RSDPG-192-fast signing utilizing <code>liboqs</code>.</em></td>
</tr>
</table>

---

## 2. Evaluation of `Bouncy Castle`

The following plots show the software-level timing leakage assessment for the pure-Java implementations provided by the `Bouncy Castle` library. Note that the JVM measurement layer (`System.nanoTime()`) introduces substantially higher variance than native timing.

### 2.1 Key Encapsulation Mechanisms (KEMs)

<table>
<tr>
<td align="center"><img src="plots/BouncyCastle_ML-KEM-768_ciphertext_fixed_vs_random_TVLA_plot.png" /><br><em>Figure S10a: Fixed-vs-random ciphertext TVLA for ML-KEM-768 decapsulation utilizing <code>Bouncy Castle</code>.</em></td>
<td align="center"><img src="plots/BouncyCastle_ML-KEM-768_ciphertext_valid_vs_invalid_TVLA_plot.png" /><br><em>Figure S10b: Valid-vs-invalid ciphertext TVLA for ML-KEM-768 decapsulation utilizing <code>Bouncy Castle</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/BouncyCastle_frodokem976shake_ciphertext_fixed_vs_random_TVLA_plot.png" /><br><em>Figure S11a: Fixed-vs-random ciphertext TVLA for FrodoKEM-976-SHAKE decapsulation utilizing <code>Bouncy Castle</code>.</em></td>
<td align="center"><img src="plots/BouncyCastle_frodokem976shake_ciphertext_valid_vs_invalid_TVLA_plot.png" /><br><em>Figure S11b: Valid-vs-invalid ciphertext TVLA for FrodoKEM-976-SHAKE decapsulation utilizing <code>Bouncy Castle</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/BouncyCastle_hqc-192_ciphertext_fixed_vs_random_TVLA_plot.png" /><br><em>Figure S12a: Fixed-vs-random ciphertext TVLA for HQC-192 decapsulation utilizing <code>Bouncy Castle</code>.</em></td>
<td align="center"><img src="plots/BouncyCastle_hqc-192_ciphertext_valid_vs_invalid_TVLA_plot.png" /><br><em>Figure S12b: Valid-vs-invalid ciphertext TVLA for HQC-192 decapsulation utilizing <code>Bouncy Castle</code>.</em></td>
</tr>
</table>

---

### 2.2 Digital Signature Algorithms (DSAs)

<table>
<tr>
<td align="center"><img src="plots/BouncyCastle_ml-dsa-65_key_TVLA_plot.png" /><br><em>Figure S13a: Key-dependent TVLA for ML-DSA-65 signing utilizing <code>Bouncy Castle</code>.</em></td>
<td align="center"><img src="plots/BouncyCastle_ml-dsa-65_message_TVLA_plot.png" /><br><em>Figure S13b: Message-dependent TVLA for ML-DSA-65 signing utilizing <code>Bouncy Castle</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/BouncyCastle_falcon-512_key_TVLA_plot.png" /><br><em>Figure S14a: Key-dependent TVLA for Falcon-512 signing utilizing <code>Bouncy Castle</code>.</em></td>
<td align="center"><img src="plots/BouncyCastle_falcon-512_message_TVLA_plot.png" /><br><em>Figure S14b: Message-dependent TVLA for Falcon-512 signing utilizing <code>Bouncy Castle</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/BouncyCastle_MAYO-3_key_TVLA_plot.png" /><br><em>Figure S15a: Key-dependent TVLA for MAYO-3 signing utilizing <code>Bouncy Castle</code>.</em></td>
<td align="center"><img src="plots/BouncyCastle_MAYO-3_message_TVLA_plot.png" /><br><em>Figure S15b: Message-dependent TVLA for MAYO-3 signing utilizing <code>Bouncy Castle</code>.</em></td>
</tr>
</table>

<table>
<tr>
<td align="center"><img src="plots/BouncyCastle_sha2-192f_key_TVLA_plot.png" /><br><em>Figure S16a: Key-dependent TVLA for SLH-DSA-SHA2-192F signing utilizing <code>Bouncy Castle</code>.</em></td>
<td align="center"><img src="plots/BouncyCastle_sha2-192f_message_TVLA_plot.png" /><br><em>Figure S16b: Message-dependent TVLA for SLH-DSA-SHA2-192F signing utilizing <code>Bouncy Castle</code>.</em></td>
</tr>
</table>