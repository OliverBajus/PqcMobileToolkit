#!/usr/bin/env python3

from __future__ import annotations

import argparse
import os
import re
from pathlib import Path
from typing import List, Optional, Tuple

import seaborn as sns
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
from matplotlib.colors import LogNorm
from matplotlib.patches import Patch
from matplotlib import patheffects

# --------------------------
# Column normalization helpers (unchanged from v1)
# --------------------------

def _norm(s: str) -> str:
    return re.sub(r"[^a-z0-9]+", "", s.strip().lower())

def _pick_col(df: pd.DataFrame, candidates: List[str]) -> Optional[str]:
    norm_map = {_norm(c): c for c in df.columns}
    for cand in candidates:
        key = _norm(cand)
        if key in norm_map:
            return norm_map[key]
    return None

def _ensure_cols(df: pd.DataFrame) -> pd.DataFrame:
    col_platform  = _pick_col(df, ["platform", "os", "target"])
    col_library   = _pick_col(df, ["library", "lib"])
    col_primitive = _pick_col(df, ["primitive", "type", "category", "kind"])
    col_algo      = _pick_col(df, ["algorithm", "alg", "name", "scheme"])
    col_op        = _pick_col(df, ["operation", "op", "function"])

    if col_platform is None:
        df["platform"] = "unknown"
    else:
        df["platform"] = df[col_platform]

    if col_library is None:
        df["library"] = "unknown"
    else:
        df["library"] = df[col_library]

    if col_primitive is None:
        df["primitive"] = "unknown"
    else:
        df["primitive"] = df[col_primitive]

    if col_algo is None:
        raise ValueError("Could not find algorithm column.")
    if col_op is None:
        raise ValueError("Could not find operation column.")

    df["algorithm"] = df[col_algo]
    df["operation"] = df[col_op]
    return df

def _detect_mean_col(df: pd.DataFrame) -> Tuple[str, str]:
    unit_candidates = [
        ("ns", ["mean_ns", "meanns", "avg_ns", "avgns", "time_ns", "timens", "meanTimeNs", "meantimens"]),
        ("us", ["median_us", "meanus", "avg_us", "avgus", "time_us", "timeus", "meanTimeUs", "meantimeus"]),
        ("ms", ["mean_ms", "meanms", "avg_ms", "avgms", "time_ms", "timems", "meanTimeMs", "meantimems"]),
        ("s",  ["mean_s", "means", "avg_s", "avgs", "time_s", "times", "meanTimeS", "meantimes"]),
    ]
    for unit, cands in unit_candidates:
        col = _pick_col(df, cands)
        if col is not None:
            return col, unit
    for col in df.columns:
        c = col.lower()
        if "mean" in c or "avg" in c:
            if "ns" in c: return col, "ns"
            if "us" in c or "µs" in c: return col, "us"
            if "ms" in c: return col, "ms"
            if re.search(r"\bsec\b|seconds|_s\b", c): return col, "s"
    col = _pick_col(df, ["mean", "avg", "average", "mean_time", "meantime", "meanTime"])
    if col is not None:
        return col, "unknown"
    raise ValueError("Could not find mean column.")

def _to_us(values: pd.Series, unit: str, assume_unit: str) -> pd.Series:
    unit_eff = unit if unit != "unknown" else assume_unit
    mult = {"ns": 1e-3, "us": 1.0, "ms": 1e3, "s": 1e6}.get(unit_eff.lower())
    if mult is None:
        raise ValueError(f"Unknown time unit '{unit_eff}'.")
    return pd.to_numeric(values, errors="coerce") * mult

def _clean_strings(df: pd.DataFrame) -> pd.DataFrame:
    df["platform"] = df["platform"].astype(str).str.strip().str.lower()
    df["library"] = df["library"].astype(str).str.strip().str.lower()
    df["primitive"] = df["primitive"].astype(str).str.strip().str.lower()
    repl_platform = {"ios": "ios", "iphone": "ios", "ipad": "ios", "android": "android"}
    df["platform"] = df["platform"].map(lambda x: repl_platform.get(x, x))
    repl_prim = {"kem": "kem", "dsa": "dsa", "sig": "dsa", "signature": "dsa", "signatures": "dsa"}
    df["primitive"] = df["primitive"].map(lambda x: repl_prim.get(x, x))
    df["operation"] = df["operation"].astype(str).str.strip()
    df["algorithm"] = df["algorithm"].astype(str).str.strip()
    return df


# --------------------------
# Helpers
# --------------------------

def _human_time(us: float) -> str:
    """Convert microseconds to human-readable string."""
    if us >= 1_000_000:
        return f"{us/1_000_000:.1f} s"
    elif us >= 1000:
        return f"{us/1000:.1f} ms"
    else:
        return f"{us:.0f} µs"

# Consistent operation ordering
KEM_OPS = ["keygen", "encaps", "decaps"]
DSA_OPS = ["keygen", "sign", "verify"]
OP_COLORS = {"keygen": "#2196F3", "encaps": "#4CAF50", "decaps": "#FF9800",
             "sign": "#4CAF50", "verify": "#FF9800"}

# Default baselines for normalized charts
DEFAULT_BASELINES = {"kem": "ML-KEM-768", "dsa": "ML-KEM-1024"}

# ── Chapter-level heatmaps: one representative per algorithm family ──
# These are used for the filtered Level 3 heatmaps that go into the thesis chapter.
# Edit these lists to choose which algorithms appear in the chapter figures.
CHAPTER_KEM_LEVEL3 = [
    "ML-KEM-768\nLevel 3",
    "FrodoKEM-976-AES\nLevel 3",
    "FrodoKEM-976-SHAKE\nLevel 3",
    "HQC-192\nLevel 3",
]

CHAPTER_DSA_LEVEL3 = [
    "ML-DSA-65\nLevel 3",
    "Falcon-1024\nLevel 5",
    "MAYO-3\nLevel 3",
    "OV-III\nLevel 3",
    "cross-rsdp-192-fast\nLevel 3",
    "cross-rsdpg-192-fast\nLevel 3",
    "SLH-DSA-SHA2-192f\nLevel 3",
    "SLH-DSA-SHAKE-192f\nLevel 3",
    "SNOVA_24_5_5_ESK\nLevel 3",
]


# --------------------------
# CHART 1: Heatmap with log-normalized colors
# --------------------------

def save_heatmap(pivot: pd.DataFrame, title: str, outpath: Path):
    """Heatmap with LogNorm color scale and human-readable time labels."""
    if pivot.empty:
        return

    fig_w = max(8, 0.8 * len(pivot.columns) + 3)
    fig_h = max(5, 0.45 * len(pivot.index) + 2)
    fig, ax = plt.subplots(figsize=(fig_w, fig_h))

    vmin = pivot.values[pivot.values > 0].min() if (pivot.values > 0).any() else 1
    vmax = pivot.values.max()

    # Use imshow with LogNorm for proper log color scaling
    im = ax.imshow(pivot.values, cmap="RdYlGn_r",
                   norm=LogNorm(vmin=vmin, vmax=vmax), aspect="auto")

    ax.set_xticks(range(len(pivot.columns)))
    ax.set_xticklabels(pivot.columns, rotation=30, ha="right")
    ax.set_yticks(range(len(pivot.index)))
    ax.set_yticklabels(pivot.index)

    # Human-readable annotations — black text with white outline for readability
    for i in range(len(pivot.index)):
        for j in range(len(pivot.columns)):
            val = pivot.iloc[i, j]
            if pd.isna(val):
                continue
            text = _human_time(val)
            txt = ax.text(j, i, text, ha="center", va="center",
                          fontsize=8, fontweight="bold", color="black")
            txt.set_path_effects([
                patheffects.withStroke(linewidth=3, foreground="white")
            ])

    plt.colorbar(im, ax=ax, label="Median time (µs) — log scale", shrink=0.8)
    ax.set_title(title, fontsize=12, fontweight="bold")
    ax.set_xlabel("Operation")
    ax.set_ylabel("Algorithm", fontweight="bold")

    plt.tight_layout()
    outpath.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(outpath, dpi=200, bbox_inches="tight")
    plt.close(fig)


# --------------------------
# CHART 2: Speedup factor chart (cross-library comparison)
# --------------------------

def save_speedup_chart(df: pd.DataFrame, platform: str, primitive: str,
                       lib_a: str, lib_b: str, outpath: Path,
                       title_suffix: str = ""):
    """
    Bar chart showing speedup factor = lib_b_time / lib_a_time.
    >1 means lib_a is faster, <1 means lib_b is faster.
    Grouped by algorithm with color-coded operations.
    """
    sub = df[(df["platform"] == platform) & (df["primitive"] == primitive)].copy()
    sub = sub[sub["library"].isin([lib_a, lib_b])]
    if sub.empty:
        return

    # Pivot to get both libraries side by side
    piv = sub.pivot_table(index=["algorithm", "operation"],
                          columns="library", values="median_us", aggfunc="median")
    piv = piv.dropna()
    if lib_a not in piv.columns or lib_b not in piv.columns:
        return

    piv["speedup"] = piv[lib_b] / piv[lib_a]

    ops = KEM_OPS if primitive == "kem" else DSA_OPS
    algos = sorted(piv.index.get_level_values("algorithm").unique())

    fig, ax = plt.subplots(figsize=(max(10, len(algos) * 2.5), 6))
    x_positions = []
    x_labels = []
    pos = 0

    for algo in algos:
        if algo not in piv.index.get_level_values("algorithm"):
            continue
        algo_data = piv.loc[algo] if algo in piv.index else pd.DataFrame()
        for op in ops:
            if op not in algo_data.index:
                continue
            val = algo_data.loc[op, "speedup"]
            color = OP_COLORS.get(op, "#999999")
            ax.bar(pos, val, color=color, width=0.7, edgecolor="white", linewidth=0.5)
            # Value label — always above the bar
            label_y = val + 0.1
            ax.text(pos, label_y, f"{val:.1f}×", ha="center", va="bottom",
                    fontsize=12, fontweight="bold")
            x_positions.append(pos)
            x_labels.append(op[0].upper())
            pos += 1
        pos += 0.8  # gap between algorithms

    # Algorithm labels below
    idx = 0
    for algo in algos:
        if algo not in piv.index.get_level_values("algorithm"):
            continue
        n_ops = sum(1 for op in ops if op in piv.loc[algo].index)
        center = idx + (n_ops - 1) / 2
        ax.text(center, -0.12, algo, ha="center", va="top", fontsize=12,
                fontweight="bold", transform=ax.get_xaxis_transform())
        idx += n_ops + 0.8

    ax.set_xticks(x_positions)
    ax.set_xticklabels(x_labels, fontsize=7)
    ax.axhline(y=1, color="red", linestyle="--", linewidth=1, alpha=0.7)
    ax.grid(axis='y', linestyle='--', alpha=0.3)

    # Legend
    legend_ops = ops
    legend_elements = [Patch(facecolor=OP_COLORS[op], label=op) for op in legend_ops]
    legend_elements.append(plt.Line2D([0], [0], color="red", linestyle="--", label="Equal"))
    ax.legend(handles=legend_elements, loc="upper right", fontsize=12)

    ax.set_ylabel(f"Speedup ({lib_b} time / {lib_a} time)", fontsize=12, fontweight="bold")
    title_line1 = f"{platform.upper()} {primitive.upper()}: {lib_a} vs {lib_b}"
    if title_suffix:
        title_line1 += f" — {title_suffix}"
    ax.set_title(f"{title_line1}\n"
                 f"(>1 = {lib_a} faster, <1 = {lib_b} faster)", fontweight="bold", fontsize=15)
    max_val = piv["speedup"].max()
    ax.set_ylim(0, max(max_val * 1.15, 1.5))

    plt.tight_layout()
    plt.subplots_adjust(bottom=0.15)
    outpath.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(outpath, dpi=200, bbox_inches="tight")
    plt.close(fig)


# --------------------------
# CHART 3: Horizontal log-scale bars (single library overview)
# --------------------------

def save_horizontal_log(df: pd.DataFrame, platform: str, library: str,
                        primitive: str, outpath: Path):
    """Horizontal bar chart with log x-axis and human-readable time labels."""
    sub = df[(df["platform"] == platform) & (df["library"] == library)
             & (df["primitive"] == primitive)].copy()
    if sub.empty:
        return

    ops = KEM_OPS if primitive == "kem" else DSA_OPS
    ops_present = [o for o in ops if o in sub["operation"].unique()]
    if not ops_present:
        return

    n_ops = len(ops_present)
    n_algos = sub["algorithm"].nunique()
    fig_h = max(4, n_algos * 0.4 + 1.5)

    fig, axes = plt.subplots(1, n_ops, figsize=(5 * n_ops + 2, fig_h), sharey=True)
    if n_ops == 1:
        axes = [axes]

    for ax_idx, op in enumerate(ops_present):
        subset = sub[sub["operation"] == op].sort_values("median_us")
        ax = axes[ax_idx]
        bars = ax.barh(subset["algorithm"], subset["median_us"],
                       color=OP_COLORS.get(op, "#2196F3"), edgecolor="white")
        ax.set_xscale("log")
        ax.set_title(op.capitalize(), fontsize=12, fontweight="bold")
        ax.set_xlabel("Median time (µs) — log scale")

        for bar, val in zip(bars, subset["median_us"]):
            ax.text(bar.get_width() * 1.15, bar.get_y() + bar.get_height() / 2,
                    _human_time(val), va="center", fontsize=7)

        # Extend x range for labels
        ax.set_xlim(left=max(1, subset["median_us"].min() * 0.5),
                    right=subset["median_us"].max() * 5)

    axes[0].set_ylabel("Algorithm", fontweight="bold")
    fig.suptitle(f"{platform.upper()} — {library} {primitive.upper()} (log scale)",
                 fontsize=13, fontweight="bold")
    plt.tight_layout()
    outpath.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(outpath, dpi=200, bbox_inches="tight")
    plt.close(fig)


# --------------------------
# CHART 4: Baseline-normalized chart (× reference algorithm)
# --------------------------

def save_normalized_chart(df: pd.DataFrame, platform: str, library: str,
                          primitive: str, baseline_algo: str,
                          algorithms: Optional[List[str]], outpath: Path):
    """
    Horizontal bars showing performance relative to a baseline algorithm.
    Colors: green (<5×), orange (5-50×), red (>50×).
    Labels show both absolute time and multiplier.
    """
    sub = df[(df["platform"] == platform) & (df["library"] == library)
             & (df["primitive"] == primitive)].copy()
    if algorithms:
        sub = sub[sub["algorithm"].isin(algorithms)]
    if sub.empty:
        return

    ops = KEM_OPS if primitive == "kem" else DSA_OPS
    ops_present = [o for o in ops if o in sub["operation"].unique()]
    if not ops_present:
        return

    n_ops = len(ops_present)
    n_algos = sub["algorithm"].nunique()
    fig_h = max(4, n_algos * 0.4 + 1.5)

    fig, axes = plt.subplots(1, n_ops, figsize=(5 * n_ops + 2, fig_h), sharey=True)
    if n_ops == 1:
        axes = [axes]

    baseline_name = baseline_algo.split("\n")[0]

    for ax_idx, op in enumerate(ops_present):
        subset = sub[sub["operation"] == op].sort_values("median_us")
        ax = axes[ax_idx]

        # Get baseline value
        baseline_row = subset[subset["algorithm"] == baseline_algo]
        baseline_val = baseline_row["median_us"].values[0] if len(baseline_row) > 0 else 1.0

        normalized = subset["median_us"].values / baseline_val
        colors = ["#4CAF50" if v < 5 else "#FF9800" if v < 50 else "#F44336" for v in normalized]

        bars = ax.barh(subset["algorithm"], normalized, color=colors, edgecolor="white")
        ax.set_xscale("log")
        ax.set_title(op.capitalize(), fontsize=12, fontweight="bold")
        ax.set_xlabel(f"× {baseline_name} (log scale)")
        ax.axvline(x=1, color="black", linestyle=":", linewidth=1.5, alpha=0.9)

        max_norm = max(normalized)
        for bar, val, norm in zip(bars, subset["median_us"], normalized):
            label = f"{_human_time(val)} ({norm:.1f}×)" if norm < 10 else f"{_human_time(val)} ({norm:.0f}×)"
            # Place label inside bar for the longest bars to avoid overflow
            if norm > max_norm * 0.4:
                ax.text(bar.get_width() * 0.85, bar.get_y() + bar.get_height() / 2,
                        label, va="center", ha="right", fontsize=7,
                        color="white", fontweight="bold",
                        path_effects=[patheffects.withStroke(linewidth=2, foreground="black")])
            else:
                ax.text(bar.get_width() * 1.15, bar.get_y() + bar.get_height() / 2,
                        label, va="center", fontsize=7)

        ax.set_xlim(right=max_norm * 8)

    axes[0].set_ylabel("Algorithm", fontweight="bold")
    fig.suptitle(f"{platform.upper()} — {library} {primitive.upper()}: "
                 f"relative to {baseline_name}",
                 fontsize=13, fontweight="bold")
    plt.tight_layout()
    outpath.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(outpath, dpi=200, bbox_inches="tight")
    plt.close(fig)


# --------------------------
# CHART 5: Per-algorithm double bar (kept for appendix / supplementary)
# --------------------------

def save_doublebar_per_algorithm(df, platform, primitive, lib_a, lib_b, outdir):
    """Kept from v1 for supplementary per-algorithm comparisons."""
    sub = df[(df["platform"] == platform) & (df["primitive"] == primitive)].copy()
    sub = sub[sub["library"].isin([lib_a, lib_b])]
    if sub.empty:
        return

    common = (
        sub.groupby(["algorithm", "operation"])["library"].nunique()
        .reset_index(name="nlibs")
    )
    common = common[common["nlibs"] == 2][["algorithm", "operation"]]
    sub = sub.merge(common, on=["algorithm", "operation"], how="inner")

    ops = KEM_OPS if primitive == "kem" else DSA_OPS
    ops_present = [o for o in ops if o in sub["operation"].unique()]
    if not ops_present:
        ops_present = sorted(sub["operation"].unique())

    Path(outdir).mkdir(parents=True, exist_ok=True)

    for alg, g in sub.groupby("algorithm"):
        fig, ax = plt.subplots(figsize=(7, 4))
        sns.barplot(data=g, x="operation", y="median_us", hue="library",
                    order=ops_present, hue_order=[lib_a, lib_b], ax=ax)
        ax.set_title(f"{platform.upper()} – {primitive.upper()} – {alg}: {lib_a} vs {lib_b}")
        ax.set_ylabel("Median time (µs)")
        ax.set_xlabel("")
        ax.grid(True, axis="y", alpha=0.3)
        sns.despine(ax=ax)
        plt.tight_layout()
        plt.savefig(Path(outdir) / f"compare_{platform}_{primitive}_{alg}.png", dpi=200)
        plt.close(fig)


# --------------------------
# Main
# --------------------------

def main():
    ap = argparse.ArgumentParser(description="PQC benchmark visualization v2")
    ap.add_argument("--inputs", nargs="+", default=["data/data.csv"])
    ap.add_argument("--out", default="outputs")
    ap.add_argument("--assume_unit", default="ns", choices=["ns", "us", "ms", "s"])
    ap.add_argument("--heatmaps", action="store_true", default=True)
    ap.add_argument("--no-heatmaps", action="store_false", dest="heatmaps")
    ap.add_argument("--speedup", action="store_true", default=True,
                    help="Generate speedup factor charts (default on).")
    ap.add_argument("--no-speedup", action="store_false", dest="speedup")
    ap.add_argument("--horizontal", action="store_true", default=True,
                    help="Generate horizontal log-scale overview charts (default on).")
    ap.add_argument("--no-horizontal", action="store_false", dest="horizontal")
    ap.add_argument("--normalized", action="store_true", default=True,
                    help="Generate baseline-normalized charts (default on).")
    ap.add_argument("--no-normalized", action="store_false", dest="normalized")
    ap.add_argument("--per-algorithm", action="store_true", default=True,
                    help="Generate per-algorithm comparison charts (default on).")
    ap.add_argument("--no-per-algorithm", action="store_false", dest="per_algorithm")
    ap.add_argument("--kem-baseline", default="ML-KEM-768\nLevel 3",
                    help="Baseline algorithm for KEM normalized charts.")
    ap.add_argument("--dsa-baseline", default="ML-DSA-65\nLevel 3",
                    help="Baseline algorithm for DSA normalized charts.")
    args = ap.parse_args()

    outdir = Path(args.out)
    outdir.mkdir(parents=True, exist_ok=True)

    # Load and normalize data
    frames = []
    for p in args.inputs:
        df = pd.read_csv(p)
        df = _ensure_cols(df)
        mean_col, unit = _detect_mean_col(df)
        df = _clean_strings(df)
        df["median_us"] = _to_us(df[mean_col], unit=unit, assume_unit=args.assume_unit)
        df = df.dropna(subset=["median_us", "algorithm", "operation", "library", "platform", "primitive"])
        frames.append(df[["platform", "library", "primitive", "algorithm", "operation", "median_us"]])

    all_df = pd.concat(frames, ignore_index=True)

    print("Platforms:", sorted(all_df["platform"].unique()))
    print("Primitives:", sorted(all_df["primitive"].unique()))
    print("Libraries:", sorted(all_df["library"].unique()))
    print(f"Total records: {len(all_df)}")

    # ---- Heatmaps (log-normalized) ----
    if args.heatmaps:
        for (platform, library, primitive), g in all_df.groupby(["platform", "library", "primitive"]):
            pivot = g.pivot_table(index="algorithm", columns="operation",
                                  values="median_us", aggfunc="median")
            # Sort: fastest algorithms first
            pivot["_sort"] = pivot.median(axis=1)
            pivot = pivot.sort_values("_sort").drop(columns="_sort")
            ops_order = KEM_OPS if primitive == "kem" else DSA_OPS
            pivot = pivot[[c for c in ops_order if c in pivot.columns] + [c for c in pivot.columns if c not in ops_order]]

            title = f"{platform.upper()} — {library} — {primitive.upper()}"
            filename = f"heatmap_{platform}_{library}_{primitive}.png"
            save_heatmap(pivot, title, outdir / "heatmaps" / filename)

    # ---- Chapter heatmaps (selected algorithms from hardcoded arrays) ----
    if args.heatmaps:
        chapter_algos = {"kem": CHAPTER_KEM_LEVEL3, "dsa": CHAPTER_DSA_LEVEL3}
        for (platform, library, primitive), g in all_df.groupby(["platform", "library", "primitive"]):
            algo_list = chapter_algos.get(primitive)
            if algo_list is None:
                continue
            chapter_df = g[g["algorithm"].isin(algo_list)]
            if chapter_df.empty:
                continue
            pivot = chapter_df.pivot_table(index="algorithm", columns="operation",
                                           values="median_us", aggfunc="median")
            pivot["_sort"] = pivot.median(axis=1)
            pivot = pivot.sort_values("_sort").drop(columns="_sort")
            ops_order = KEM_OPS if primitive == "kem" else DSA_OPS
            pivot = pivot[[c for c in ops_order if c in pivot.columns] + [c for c in pivot.columns if c not in ops_order]]

            title = f"{platform.upper()} — {library} — {primitive.upper()} — Selected"
            filename = f"heatmap_{platform}_{library}_{primitive}_selected.png"
            save_heatmap(pivot, title, outdir / "heatmaps" / filename)

    # ---- Speedup factor charts ----
    if args.speedup:
        # Define library pairs per platform
        pairs = [
            ("android", "liboqs", "bouncycastle"),
            ("ios", "liboqs", "cryptokit"),
        ]
        for platform, lib_a, lib_b in pairs:
            for prim in ["kem", "dsa"]:
                # Full chart (all levels combined)
                save_speedup_chart(
                    all_df, platform, prim, lib_a, lib_b,
                    outdir / "speedup" / f"speedup_{platform}_{prim}_{lib_a}_vs_{lib_b}.png"
                )

        # Per-security-level speedup charts (liboqs comparisons only)
        for platform, lib_a, lib_b in pairs:
            for prim in ["kem", "dsa"]:
                for level in ["Level 3", "Level 5"]:
                    level_df = all_df[all_df["algorithm"].str.contains(level, na=False)]
                    if level_df.empty:
                        continue
                    level_tag = level.replace(" ", "").lower()  # "level3"
                    save_speedup_chart(
                        level_df, platform, prim, lib_a, lib_b,
                        outdir / "speedup" / f"speedup_{platform}_{prim}_{lib_a}_vs_{lib_b}_{level_tag}.png",
                        title_suffix=level,
                        )

    # ---- Horizontal log-scale overviews ----
    if args.horizontal:
        for (platform, library, primitive), _ in all_df.groupby(["platform", "library", "primitive"]):
            save_horizontal_log(
                all_df, platform, library, primitive,
                outdir / "horizontal" / f"horizontal_{platform}_{library}_{primitive}.png"
            )

    # ---- Baseline-normalized charts ----
    if args.normalized:
        baselines = {"kem": args.kem_baseline, "dsa": args.dsa_baseline}
        for (platform, library, primitive), _ in all_df.groupby(["platform", "library", "primitive"]):
            baseline = baselines.get(primitive)
            if baseline is None:
                continue
            save_normalized_chart(
                all_df, platform, library, primitive,
                baseline_algo=baseline, algorithms=None,
                outpath=outdir / "normalized" / f"normalized_{platform}_{library}_{primitive}.png"
            )

    # ---- Per-algorithm double bars (supplementary) ----
    if args.per_algorithm:
        pairs = [("android", "liboqs", "bouncycastle"), ("ios", "liboqs", "cryptokit")]
        for platform, lib_a, lib_b in pairs:
            for prim in ["kem", "dsa"]:
                save_doublebar_per_algorithm(
                    all_df, platform, prim, lib_a, lib_b,
                    outdir / "per_algorithm" / f"{platform}_{prim}"
                )

    # Save merged data
    all_df.to_csv(outdir / "merged_normalized_medians_us.csv", index=False)
    print(f"\nDone. All plots in: {outdir}")


if __name__ == "__main__":
    main()