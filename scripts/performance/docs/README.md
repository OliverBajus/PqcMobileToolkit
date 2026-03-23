# Performance Analysis Script

Generates benchmark visualization charts from PQC performance data: heatmaps, speedup comparisons, horizontal log-scale bars, baseline-normalized charts, and per-algorithm comparisons.

## Prerequisites

```
pip install pandas numpy matplotlib seaborn
```

## Usage

```bash
cd scripts/performance
python performance_analysis.py
python performance_analysis.py --inputs data/data.csv --out outputs --assume_unit ns
```

| Flag | Default | Description |
|---|---|---|
| `--inputs` | `data/data.csv` | One or more CSV files (space-separated) |
| `--out` | `outputs` | Output directory |
| `--assume_unit` | `ns` | Time unit if column name has no unit suffix (`ns`, `us`, `ms`, `s`) |
| `--no-heatmaps` | | Disable heatmap generation |
| `--no-speedup` | | Disable speedup factor charts |
| `--no-horizontal` | | Disable horizontal log-scale charts |
| `--no-normalized` | | Disable baseline-normalized charts |
| `--no-per-algorithm` | | Disable per-algorithm comparison charts |
| `--kem-baseline` | `ML-KEM-768\nLevel 3` | Baseline algorithm for KEM normalized charts |
| `--dsa-baseline` | `ML-DSA-65\nLevel 3` | Baseline algorithm for DSA normalized charts |

All chart types are enabled by default.

## Input Format

CSV file(s) with the following columns. Column names are matched flexibly (case-insensitive, multiple aliases supported).

### Required columns

| Column | Aliases | Example |
|---|---|---|
| algorithm | `algorithm`, `alg`, `name`, `scheme` | `ML-KEM-768\nLevel 3` |
| operation | `operation`, `op`, `function` | `keygen`, `sign`, `encaps` |
| mean time | `mean_ns`, `avg_ns`, `meanTimeNs`, `mean_us`, `mean_ms`, etc. | `145230` |

### Optional columns (default to `"unknown"` if missing)

| Column | Aliases | Example |
|---|---|---|
| platform | `platform`, `os`, `target` | `android`, `ios` |
| library | `library`, `lib` | `liboqs`, `bouncycastle` |
| primitive | `primitive`, `type`, `category` | `kem`, `dsa` |

The time unit is auto-detected from the column name (e.g. `mean_ns` = nanoseconds). If the column name has no unit (e.g. just `mean`), `--assume_unit` is used.

### Example CSV

```csv
platform,library,primitive,algorithm,operation,mean_ns
android,liboqs,kem,ML-KEM-768\nLevel 3,keygen,145230
android,liboqs,kem,ML-KEM-768\nLevel 3,encaps,178450
android,liboqs,kem,ML-KEM-768\nLevel 3,decaps,165890
android,bouncycastle,kem,ML-KEM-768\nLevel 3,keygen,312500
```

## Output

```
outputs/
  merged_normalized_means_us.csv           # all input data merged, normalized to microseconds
  heatmaps/
    heatmap_{platform}_{library}_{primitive}.png
    heatmap_{platform}_{library}_{primitive}_selected.png
  speedup/
    speedup_{platform}_{primitive}_{libA}_vs_{libB}.png
    speedup_{platform}_{primitive}_{libA}_vs_{libB}_{level}.png
  horizontal/
    horizontal_{platform}_{library}_{primitive}.png
  normalized/
    normalized_{platform}_{library}_{primitive}.png
  per_algorithm/
    {platform}_{primitive}/
      compare_{platform}_{primitive}_{algorithm}.png
```

### Chart types

- **Heatmaps**: Log-normalized color scale with human-readable time labels per algorithm/operation
- **Speedup**: Bar chart showing `lib_b_time / lib_a_time` per operation. >1 means lib_a is faster
- **Horizontal**: Log-scale horizontal bars for single-library overview, one subplot per operation
- **Normalized**: Performance relative to a baseline algorithm (color-coded: green <5x, orange 5-50x, red >50x)
- **Per-algorithm**: Side-by-side bar comparison of two libraries for each algorithm
