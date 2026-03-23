# PQC Heuristic Cost Analysis - ALL Library Variants

This analysis compares all evaluated post-quantum algorithm and library variants using a simple heuristic cost metric that combines execution time and size overhead into a single score for easier practical comparison.


**Weights:** 0.5 × Normalized Time + 0.5 × Normalized Size  
**Baselines:** ML-KEM-768 liboqs (KEM), ML-DSA-65 liboqs (DSA)


## Column meanings

- **PK+CT (B):** sum of public key size and ciphertext size in bytes for KEMs
- **PK+Sig (B):** sum of public key size and signature size in bytes for signature schemes
- **Geomean:** geometric mean of the measured execution times across the evaluated operations for the given algorithm–library variant
- **NormT:** normalized execution time, computed relative to the selected baseline as `Geomean / Geomean_baseline`
- **NormS:** normalized size, computed relative to the selected baseline as `(PK+CT) / (PK+CT)_baseline` for KEMs and `(PK+Sig) / (PK+Sig)_baseline` for signature schemes
- **Cost:** final heuristic score computed as `0.5 × NormT + 0.5 × NormS`
- **Lower = better**

---

## KEM Algorithms - Heuristic Cost Ranking (All Libraries)

**Cost = 0.5 × Norm_Time + 0.5 × Norm_Size**  
**Lower = better**

| # | Algorithm | Lvl | Lib | PK+CT (B) | Geomean | NormT | NormS | Cost |
|---|-----------|-----|-----|----------:|--------:|------:|------:|-----:|
| 1 | ML-KEM-768 | 3 | liboqs | 2272 | 33.6 | 1.00 | 1.00 | 1.00 |
| 2 | ML-KEM-1024 | 5 | liboqs | 3136 | 49.3 | 1.47 | 1.38 | 1.42 |
| 3 | ML-KEM-768 | 3 | BC | 2272 | 97.8 | 2.91 | 1.00 | 1.96 |
| 4 | ML-KEM-1024 | 5 | BC | 3136 | 140.3 | 4.18 | 1.38 | 2.78 |
| 5 | FrodoKEM-976-AES | 3 | liboqs | 31424 | 967.9 | 28.83 | 13.83 | 21.33 |
| 6 | FrodoKEM-1344-AES | 5 | liboqs | 43216 | 1724.7 | 51.37 | 19.02 | 35.19 |
| 7 | FrodoKEM-976-SHAKE | 3 | liboqs | 31424 | 4897.9 | 145.88 | 13.83 | 79.85 |
| 8 | FrodoKEM-1344-SHAKE | 5 | liboqs | 43216 | 8724.7 | 259.85 | 19.02 | 139.44 |
| 9 | HQC-192 | 3 | BC | 13500 | 10951.0 | 326.16 | 5.94 | 166.05 |
| 10 | FrodoKEM-976-SHAKE | 3 | BC | 31424 | 22023.6 | 655.94 | 13.83 | 334.88 |
| 11 | FrodoKEM-976-AES | 3 | BC | 31424 | 24159.2 | 719.54 | 13.83 | 366.69 |
| 12 | HQC-256 | 5 | BC | 21666 | 25889.8 | 771.09 | 9.54 | 390.31 |
| 13 | FrodoKEM-1344-SHAKE | 5 | BC | 43216 | 41690.0 | 1241.67 | 19.02 | 630.34 |
| 14 | FrodoKEM-1344-AES | 5 | BC | 43216 | 46360.1 | 1380.76 | 19.02 | 699.89 |
| 15 | HQC-192 | 3 | liboqs | 13500 | 48304.6 | 1438.68 | 5.94 | 722.31 |
| 16 | HQC-256 | 5 | liboqs | 21666 | 91641.9 | 2729.41 | 9.54 | 1369.47 |

---

## DSA Algorithms - Heuristic Cost Ranking (All Libraries)

**Cost = 0.5 × Norm_Time + 0.5 × Norm_Size**  
**Lower = better**

| # | Algorithm            | Lvl | Lib | PK+Sig (B) | Geomean | NormT | NormS | Cost |
|---|----------------------|-----|-----|-----------:|--------:|------:|------:|-----:|
| 1 | ML-DSA-65            | 3 | liboqs | 5261 | 168.1 | 1.00 | 1.00 | 1.00 |
| 2 | ML-DSA-87            | 5 | liboqs | 7219 | 242.8 | 1.44 | 1.37 | 1.41 |
| 3 | ML-DSA-65            | 3 | BC | 5261 | 436.6 | 2.60 | 1.00 | 1.80 |
| 4 | ML-DSA-87            | 5 | BC | 7219 | 616.1 | 3.67 | 1.37 | 2.52 |
| 5 | cross-rsdpg-192-balanced | 3 | liboqs | 22547 | 291.5 | 1.73 | 4.29 | 3.01 |
| 6 | cross-rsdpg-192-fast | 3 | liboqs | 26855 | 253.9 | 1.51 | 5.10 | 3.31 |
| 7 | MAYO-3               | 3 | liboqs | 3667 | 995.8 | 5.92 | 0.70 | 3.31 |
| 8 | cross-rsdp-192-balanced | 3 | liboqs | 29968 | 505.7 | 3.01 | 5.70 | 4.35 |
| 9 | cross-rsdp-192-fast  | 3 | liboqs | 41521 | 355.6 | 2.12 | 7.89 | 5.00 |
| 10 | Falcon-1024          | 5 | BC | 3123 | 1609.1 | 9.57 | 0.59 | 5.08 |
| 11 | Falcon-padded-1024   | 5 | BC | 3073 | 1609.1 | 9.57 | 0.58 | 5.08 |
| 12 | cross-rsdpg-256-balanced | 5 | liboqs | 40206 | 500.7 | 2.98 | 7.64 | 5.31 |
| 13 | cross-rsdpg-256-fast | 5 | liboqs | 48208 | 437.5 | 2.60 | 9.16 | 5.88 |
| 14 | cross-rsdp-256-balanced | 5 | liboqs | 53680 | 500.7 | 2.98 | 10.20 | 6.59 |
| 15 | Falcon-1024          | 5 | liboqs | 3123 | 2896.4 | 17.23 | 0.59 | 8.91 |
| 16 | Falcon-padded-1024   | 5 | liboqs | 3073 | 2896.4 | 17.23 | 0.58 | 8.91 |
| 17 | cross-rsdp-256-fast  | 5 | liboqs | 74743 | 685.8 | 4.08 | 14.21 | 9.14 |
| 18 | SLH-DSA-SHA2-192f    | 3 | liboqs | 35712 | 4675.4 | 27.81 | 6.79 | 17.30 |
| 19 | MAYO-3               | 3 | BC | 3667 | 6085.8 | 36.20 | 0.70 | 18.45 |
| 20 | OV-III-pkc           | 3 | liboqs | 72904 | 4267.2 | 25.38 | 13.86 | 19.62 |
| 21 | SLH-DSA-SHAKE-192f   | 3 | liboqs | 35712 | 7293.6 | 43.39 | 6.79 | 25.09 |
| 22 | OV-III               | 3 | liboqs | 189432 | 3357.4 | 19.97 | 36.01 | 27.99 |
| 23 | SLH-DSA-SHA2-256f    | 5 | liboqs | 49920 | 8232.5 | 48.97 | 9.49 | 29.23 |
| 24 | SLH-DSA-SHA2-192f    | 3 | BC | 35712 | 12753.7 | 75.87 | 6.79 | 41.33 |
| 25 | MAYO-5               | 5 | BC | 6518 | 13952.3 | 83.00 | 1.24 | 42.12 |
| 26 | SLH-DSA-SHAKE-256f   | 5 | liboqs | 49920 | 12901.4 | 76.74 | 9.49 | 43.12 |
| 27 | SLH-DSA-SHAKE-192f   | 3 | BC | 35712 | 15697.3 | 93.38 | 6.79 | 50.08 |
| 28 | OV-V                 | 5 | liboqs | 447252 | 8564.5 | 50.95 | 85.01 | 67.98 |
| 29 | SLH-DSA-SHA2-256f    | 5 | BC | 49920 | 23441.4 | 139.44 | 9.49 | 74.47 |
| 30 | SLH-DSA-SHAKE-256f   | 5 | BC | 49920 | 28279.1 | 168.22 | 9.49 | 88.85 |
| 31 | SLH-DSA-SHA2-256s    | 5 | liboqs | 29856 | 35156.3 | 209.13 | 5.67 | 107.40 |
| 32 | SLH-DSA-SHA2-192s    | 3 | liboqs | 16272 | 37104.9 | 220.72 | 3.09 | 111.91 |
| 33 | SLH-DSA-SHAKE-192s   | 3 | liboqs | 16272 | 56015.3 | 333.21 | 3.09 | 168.15 |
| 34 | OV-V-pkc             | 5 | liboqs | 158692 | 51624.3 | 307.09 | 30.16 | 168.63 |
| 35 | SLH-DSA-SHAKE-256s   | 5 | liboqs | 29856 | 56088.3 | 333.64 | 5.67 | 169.66 |
| 36 | SLH-DSA-SHA2-256s    | 5 | BC | 29856 | 101715.5 | 605.06 | 5.67 | 305.37 |
| 37 | SLH-DSA-SHA2-192s    | 3 | BC | 16272 | 108400.5 | 644.83 | 3.09 | 323.96 |
| 38 | SLH-DSA-SHAKE-256s   | 5 | BC | 29856 | 117884.4 | 701.24 | 5.67 | 353.46 |
| 39 | SLH-DSA-SHAKE-192s   | 3 | BC | 16272 | 124280.9 | 739.29 | 3.09 | 371.19 |
