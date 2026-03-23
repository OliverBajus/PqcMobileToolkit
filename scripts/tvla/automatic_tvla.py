import argparse
import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import ttest_ind
import glob
import os
import re

def parse_args():
    p = argparse.ArgumentParser(
        description="Automated TVLA analysis for PQC timing data",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    p.add_argument("-i", "--input", default="data",
                   help="Directory containing *_fixed_timings.csv files (default: data)")
    p.add_argument("-o", "--output", default="outputs",
                   help="Directory to save TVLA plots and summary (default: outputs)")
    p.add_argument("-t", "--threshold", type=float, default=4.5,
                   help="TVLA t-value threshold (default: 4.5)")
    return p.parse_args()


args = parse_args()
input_dir = args.input
output_dir = args.output
threshold = args.threshold

os.makedirs(output_dir, exist_ok=True)

fixed_files = glob.glob(os.path.join(input_dir, '*_fixed_timings.csv'))

print(f"{'=' * 60}")
print(f"  TVLA Analysis")
print(f"  Input:     {input_dir}")
print(f"  Output:    {output_dir}")
print(f"  Threshold: {threshold}")
print(f"  Found:     {len(fixed_files)} test case(s)")
print(f"{'=' * 60}\n")

results = []  # collect (verdict, label, abs_t, traces) for summary

for i, fixed_path in enumerate(fixed_files, 1):
    random_path = fixed_path.replace('_fixed_timings.csv', '_random_timings.csv')

    # Sanity check
    if not os.path.exists(random_path):
        print(f"  [{i}/{len(fixed_files)}] SKIP — random file not found for {os.path.basename(fixed_path)}")
        continue

    # ── Parse filename (supports both old and new naming) ──
    basename = os.path.basename(fixed_path).replace('_fixed_timings.csv', '')
    # Split at _TVLA_ to separate header from the rest
    before_tvla, after_tvla = basename.split('_TVLA_', 1)

    parts_before = before_tvla.split('_')
    library = parts_before[0]
    if library == "BC":
        library = "BouncyCastle"
    scheme_type = parts_before[1]
    analysis_type = parts_before[2]

    # New naming has a test variant like "fixed_vs_random" or "valid_vs_invalid"
    variant_match = re.match(r'^(\w+_vs_\w+)_(.+)$', after_tvla)
    if variant_match:
        test_variant = variant_match.group(1)   # e.g. "fixed_vs_random"
        alg_name = variant_match.group(2)
    else:
        test_variant = None
        alg_name = after_tvla

    if analysis_type == "ciphertext" and scheme_type == "DSA":
        analysis_type = "message"

    variant_str = f" [{test_variant.replace('_', ' ')}]" if test_variant else ""
    print(f"  [{i}/{len(fixed_files)}] {alg_name} — {library} — {analysis_type}{variant_str}")

    fixed = np.loadtxt(fixed_path)
    random = np.loadtxt(random_path)

    # Make sure both arrays are same length
    trace_len = min(len(fixed), len(random))
    fixed = fixed[:trace_len]
    random = random[:trace_len]

    # Single t-test across all traces
    t_val, _ = ttest_ind(fixed, random, equal_var=False)
    abs_t_val = abs(t_val)

    verdict = "LEAK DETECTED" if abs_t_val > threshold else "PASS"
    label = f"{alg_name} — {library} — {analysis_type}{variant_str}"
    results.append((verdict, label, abs_t_val, trace_len))
    print(f"           Traces: {trace_len}  |  Final |t|: {abs_t_val:.4f}  |  {verdict}")

    trace_counts = np.arange(10, trace_len, 10)
    t_values = []

    for n in trace_counts:
        t, _ = ttest_ind(fixed[:n], random[:n], equal_var=False)
        t_values.append(abs(t))

    # Build title and filename
    variant_label = f" ({test_variant.replace('_', ' ')})" if test_variant else ""
    title = (r'$\bf{' + alg_name + ": " + library + '}$'
             + f'\nIncremental {analysis_type} leakage TVLA analysis{variant_label}')

    variant_tag = f"_{test_variant}" if test_variant else ""
    plots_dir = os.path.join(output_dir, "plots")
    os.makedirs(plots_dir, exist_ok=True)
    plot_filename = os.path.join(plots_dir,
                                 f'{library}_{alg_name}_{analysis_type}{variant_tag}_TVLA_plot.png')

    plt.figure(figsize=(8, 5))
    plt.plot(trace_counts, t_values, color='red')
    plt.axhline(y=threshold, color='black', linestyle='--', linewidth=2)
    plt.title(title, fontsize=14)
    plt.xlabel('Number of Traces Analyzed')
    plt.ylabel('|t|-value')
    plt.grid(True)
    plt.savefig(plot_filename, dpi=300, bbox_inches='tight')
    plt.close()

    print(f"           Saved:  {plot_filename}")

# ── Write summary file (leaks first, then passes) ──
summary_path = os.path.join(output_dir, "tvla_summary.txt")
leaks = [r for r in results if r[0] == "LEAK DETECTED"]
passes = [r for r in results if r[0] == "PASS"]

with open(summary_path, "w") as f:
    f.write(f"TVLA Analysis Summary\n")
    f.write(f"{'=' * 60}\n")
    f.write(f"Threshold: {threshold}\n")
    f.write(f"Total tests: {len(results)}  |  Leaks: {len(leaks)}  |  Pass: {len(passes)}\n")
    f.write(f"{'=' * 60}\n\n")

    if leaks:
        f.write(f"LEAK DETECTED ({len(leaks)})\n")
        f.write(f"{'-' * 60}\n")
        for _, label, t_val, traces in sorted(leaks, key=lambda x: -x[2]):
            f.write(f"  {label}\n")
            f.write(f"    |t| = {t_val:.4f}  |  traces = {traces}\n")
        f.write(f"\n")

    if passes:
        f.write(f"PASS ({len(passes)})\n")
        f.write(f"{'-' * 60}\n")
        for _, label, t_val, traces in sorted(passes, key=lambda x: -x[2]):
            f.write(f"  {label}\n")
            f.write(f"    |t| = {t_val:.4f}  |  traces = {traces}\n")

print(f"\n{'=' * 60}")
print(f"  Done. {len(fixed_files)} plot(s) saved to '{output_dir}/plots'")
print(f"  Summary: {summary_path}")
print(f"{'=' * 60}")
