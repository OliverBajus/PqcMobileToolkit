# TVLA Analysis Script

Runs Welch's t-test on PQC timing traces to detect timing side-channel leakage.

## Prerequisites

```
pip install numpy matplotlib scipy
```

## Usage

```bash
cd scripts/tvla
python automatic_tvla.py
python automatic_tvla.py -i data -o outputs -t 4.5
```

| Flag | Default | Description |
|---|---|---|
| `-i` / `--input` | `data` | Directory containing input CSV files |
| `-o` / `--output` | `outputs` | Output directory for plots and summary |
| `-t` / `--threshold` | `4.5` | |t|-value threshold for leak detection |

## Collecting Test Data

The `benchmark` module contains TVLA instrumented tests that produce CSV files in the
format expected by this script. Each test takes a long time, so run specific tests
rather than the entire suite. To collect data and run analysis:

1. Run a specific TVLA test on a connected Android device, e.g. ML-DSA message/key test via liboqs:
   ```bash
   ./gradlew :benchmark:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=cz.monetplus.pqc.benchmark.liboqs.tvla.LibOqsTvlaDsaTest#test_ML_DSA_3
   ```
2. Pull the results from the device:
   ```bash
   adb pull /sdcard/Android/data/cz.monetplus.pqc.benchmark.test/files/Download/ ./data/
   ```
3. Run the analysis:
   ```bash
   python automatic_tvla.py
   ```

## Input Format

The script expects **pairs** of CSV files in the input directory. Each pair represents one TVLA test case.

### File naming

```
{Library}_{SchemeType}_{AnalysisType}_TVLA_{AlgName}_fixed_timings.csv
{Library}_{SchemeType}_{AnalysisType}_TVLA_{AlgName}_random_timings.csv
```

With optional test variant:

```
{Library}_{SchemeType}_{AnalysisType}_TVLA_{variant}_{AlgName}_fixed_timings.csv
{Library}_{SchemeType}_{AnalysisType}_TVLA_{variant}_{AlgName}_random_timings.csv
```

**Parts:**

| Part | Values | Example |
|---|---|---|
| `Library` | `LibOQS`, `BC` | `LibOQS` |
| `SchemeType` | `KEM`, `DSA` | `DSA` |
| `AnalysisType` | `message`, `key`, `ciphertext` | `message` |
| `variant` (optional) | `fixed_vs_random`, `valid_vs_invalid` | `fixed_vs_random` |
| `AlgName` | algorithm name | `ML-DSA-65` |

**Examples:**

```
LibOQS_DSA_message_TVLA_ML-DSA-65_fixed_timings.csv
LibOQS_DSA_message_TVLA_ML-DSA-65_random_timings.csv
BC_KEM_ciphertext_TVLA_fixed_vs_random_ML-KEM-768_fixed_timings.csv
BC_KEM_ciphertext_TVLA_fixed_vs_random_ML-KEM-768_random_timings.csv
```

### File contents

Each CSV file is a single column of timing values (one per line, no header). Each row is one trace measurement in nanoseconds.

```
12345
12389
12401
...
```

The `_fixed_timings.csv` file contains timings for the fixed (constant) input. The `_random_timings.csv` file contains timings for random inputs.

## Output

```
outputs/
  tvla_summary.txt                  # text summary, leaks listed first
  plots/
    {Library}_{Alg}_{analysis}_TVLA_plot.png   # incremental t-value plot per test
```

### Summary format

Lists all test results with leaks first (sorted by highest |t|), then passes:

```
TVLA Analysis Summary
============================================================
Threshold: 4.5
Total tests: 12  |  Leaks: 3  |  Pass: 9
============================================================

LEAK DETECTED (3)
------------------------------------------------------------
  ML-DSA-65 — liboqs — message [fixed vs random]
    |t| = 7.2341  |  traces = 200000

PASS (9)
------------------------------------------------------------
  ML-KEM-768 — BouncyCastle — ciphertext [fixed vs random]
    |t| = 1.2045  |  traces = 100000
```

### Plots

Each plot shows |t|-value over increasing number of traces with threshold line. If the curve crosses the threshold, timing leakage is detected.

## Analysis Report

Results, findings, and leakage assessment commentary from running this script on the thesis TVLA data are documented in [REPORT.md](REPORT.md).