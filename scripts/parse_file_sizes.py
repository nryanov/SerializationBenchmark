#!/usr/bin/env python3
"""Parse results.txt (size + filename) into per-format file-size tables for results/2026/file-sizes.md."""

from __future__ import annotations

import sys
from pathlib import Path

DATASETS = ("mixedData", "onlyLongs", "onlyStrings")

JAVA_STYLE_SUFFIX = {
    "": "none",
    "Gzip": "gzip",
    "Lz4": "lz4",
    "Snappy": "snappy",
    "Xz": "xz",
}

AVRO_CODECS_ORDER = ("none", "deflate", "snappy", "xz", "bzip2", "zstd")

ORC_SUFFIX_MAP = {
    "NONE": "none",
    "SNAPPY": "snappy",
    "ZLIB": "zlib",
    "LZO": "lz0",
    "LZ4": "lz4",
    "ZSTD": "zstd",
    "BROTLI": "brotli",
}

PARQUET_SUFFIX_MAP = {
    "UNCOMPRESSED": "none",
    "SNAPPY": "snappy",
    "GZIP": "gzip",
    "LZ4": "lz4",
    "ZSTD": "zstd",
}

# (markdown section title, slug for data dict, column headers in order)
FORMAT_ORDER: list[tuple[str, str, tuple[str, ...]]] = [
    ("Java", "java", ("none", "gzip", "snappy", "lz4", "xz")),
    ("Json", "json", ("none", "gzip", "snappy", "lz4", "xz")),
    ("Avro", "avro", AVRO_CODECS_ORDER),
    ("Thrift (binary protocol)", "thrift_binary", ("none", "gzip", "snappy", "lz4", "xz")),
    ("Thrift (compact protocol)", "thrift_compact", ("none", "gzip", "snappy", "lz4", "xz")),
    ("Protobuf", "protobuf", ("none", "gzip", "snappy", "lz4", "xz")),
    ("ORC", "orc", ("none", "snappy", "zlib", "lz0", "lz4", "zstd", "brotli")),
    ("Parquet (parquet-avro)", "parquet_avro", ("none", "snappy", "gzip", "lz4", "zstd")),
    ("Parquet (parquet-thrift)", "parquet_thrift", ("none", "snappy", "gzip", "lz4", "zstd")),
    ("Msgpack", "msgpack", ("none", "gzip", "snappy", "lz4", "xz")),
    ("CBOR", "cbor", ("none", "gzip", "snappy", "lz4", "xz")),
]


def parse_size_token(raw: str) -> tuple[int | None, bool]:
    """Return (int value, valid). Invalid if multi-digit with leading zero (e.g. 02800000)."""
    s = raw.strip()
    if not s.isdigit():
        return None, False
    if len(s) > 1 and s[0] == "0":
        return int(s, 10), False
    return int(s, 10), True


def split_dataset(basename: str) -> tuple[str | None, str]:
    for d in DATASETS:
        if basename.startswith(d):
            return d, basename[len(d) :]
    return None, basename


def parse_line(basename: str) -> tuple[str, str, str] | None:
    """Return (dataset, format_slug, codec) or None if skipped."""
    if basename.endswith("Input.csv"):
        return None
    dataset, rest = split_dataset(basename)
    if dataset is None:
        return None

    if rest.endswith(".out"):
        core = rest[: -len(".out")]
    elif rest.endswith(".orc"):
        core = rest[: -len(".orc")]
    else:
        return None

    # Longest-prefix match for format (Parquet* before shorter names).
    if core.startswith("ParquetThriftSerialization"):
        suf = core[len("ParquetThriftSerialization") :]
        codec = PARQUET_SUFFIX_MAP.get(suf)
        if codec is None:
            return None
        return dataset, "parquet_thrift", codec

    if core.startswith("ParquetAvroSerialization"):
        suf = core[len("ParquetAvroSerialization") :]
        codec = PARQUET_SUFFIX_MAP.get(suf)
        if codec is None:
            return None
        return dataset, "parquet_avro", codec

    if core.startswith("OrcSerialization"):
        suf = core[len("OrcSerialization") :]
        codec = ORC_SUFFIX_MAP.get(suf)
        if codec is None:
            return None
        return dataset, "orc", codec

    if core.startswith("AvroDataSerialization"):
        tail = core[len("AvroDataSerialization") :]
        if tail not in AVRO_CODECS_ORDER:
            return None
        return dataset, "avro", tail

    if core.startswith("BinaryThriftSerialization"):
        suf = core[len("BinaryThriftSerialization") :]
        codec = JAVA_STYLE_SUFFIX.get(suf)
        if codec is None:
            return None
        return dataset, "thrift_binary", codec

    if core.startswith("CompactThriftSerialization"):
        suf = core[len("CompactThriftSerialization") :]
        codec = JAVA_STYLE_SUFFIX.get(suf)
        if codec is None:
            return None
        return dataset, "thrift_compact", codec

    if core.startswith("CborManualSerialization"):
        suf = core[len("CborManualSerialization") :]
        codec = JAVA_STYLE_SUFFIX.get(suf)
        if codec is None:
            return None
        return dataset, "cbor", codec

    if core.startswith("JavaSerialization"):
        suf = core[len("JavaSerialization") :]
        codec = JAVA_STYLE_SUFFIX.get(suf)
        if codec is None:
            return None
        return dataset, "java", codec

    if core.startswith("JsonSerialization"):
        suf = core[len("JsonSerialization") :]
        codec = JAVA_STYLE_SUFFIX.get(suf)
        if codec is None:
            return None
        return dataset, "json", codec

    if core.startswith("MsgpackSerialization"):
        suf = core[len("MsgpackSerialization") :]
        codec = JAVA_STYLE_SUFFIX.get(suf)
        if codec is None:
            return None
        return dataset, "msgpack", codec

    if core.startswith("ProtobufSerialization"):
        suf = core[len("ProtobufSerialization") :]
        codec = JAVA_STYLE_SUFFIX.get(suf)
        if codec is None:
            return None
        return dataset, "protobuf", codec

    return None


def load_results(path: Path) -> dict[str, dict[str, dict[str, tuple[int, bool]]]]:
    """format_slug -> dataset -> codec -> (bytes, valid)."""
    out: dict[str, dict[str, dict[str, tuple[int, bool]]]] = {}
    for line in path.read_text().splitlines():
        line = line.strip()
        if not line:
            continue
        parts = line.split(None, 1)
        if len(parts) != 2:
            continue
        size_tok, name = parts
        size_val, size_valid = parse_size_token(size_tok)
        if size_val is None:
            continue
        parsed = parse_line(name)
        if parsed is None:
            continue
        dataset, fmt, codec = parsed
        if fmt not in out:
            out[fmt] = {}
        if dataset not in out[fmt]:
            out[fmt][dataset] = {}
        out[fmt][dataset][codec] = (size_val, size_valid)
    return out


def fmt_num(n: int) -> str:
    return f"{n:,}"


def cell_value(size: int, valid: bool) -> str:
    if not valid:
        return "*(invalid in source)*"
    return fmt_num(size)


def markdown_table(title_cols: tuple[str, ...], rows: list[tuple[str, list[str]]]) -> str:
    """rows: (row_label, cell strings aligned to title_cols)."""
    label_w = max(max(len(r[0]) for r in rows), 12) if rows else 12
    col_ws = [max(len(c), 8) for c in title_cols]
    for _, cells in rows:
        for i, c in enumerate(cells):
            col_ws[i] = max(col_ws[i], len(c))

    def pad(s: str, w: int) -> str:
        return s + " " * (w - len(s))

    header = (
        "| "
        + pad("", label_w)
        + " | "
        + " | ".join(pad(title_cols[i], col_ws[i]) for i in range(len(title_cols)))
        + " |"
    )
    sep = (
        "|"
        + "-" * (label_w + 2)
        + "|"
        + "|".join("-" * (col_ws[i] + 2) for i in range(len(title_cols)))
        + "|"
    )
    lines = [header, sep]
    for label, cells in rows:
        lines.append(
            "| "
            + pad(label, label_w)
            + " | "
            + " | ".join(pad(cells[i], col_ws[i]) for i in range(len(title_cols)))
            + " |"
        )
    return "\n".join(lines)


def apply_logical_qc(
    data: dict[str, dict[str, dict[str, tuple[int, bool]]]],
) -> tuple[dict[str, dict[str, dict[str, tuple[int, bool]]]], list[str]]:
    """
    Mark cells invalid when gzip compressed size > uncompressed (impossible).
    Returns (mutated copy) and notes.
    """
    import copy

    d = copy.deepcopy(data)
    notes: list[str] = []

    def invalidate_json_only_strings():
        fmt = "json"
        ds = "onlyStrings"
        if fmt not in d or ds not in d[fmt]:
            return
        row = d[fmt][ds]
        none_v = row.get("none")
        gzip_v = row.get("gzip")
        if not none_v or not gzip_v:
            return
        n_size, n_ok = none_v
        g_size, g_ok = gzip_v
        if n_ok and g_ok and g_size > n_size:
            for k in list(row.keys()):
                row[k] = (row[k][0], False)
            notes.append(
                "Json / onlyStrings: gzip size exceeds uncompressed in source; "
                "all codecs for that row marked invalid."
            )

    invalidate_json_only_strings()
    return d, notes


def build_markdown(data: Path) -> str:
    raw = load_results(data)
    tables, qc_notes = apply_logical_qc(raw)

    lines: list[str] = [
        "# Serialized output file sizes (2026)",
        "",
        "Sizes are **bytes on disk** for serialized output (100k records), from `ls -l` / `wc -c` style listing in [results.txt](../../results.txt).",
        "",
        "Input CSV sizes (reference): mixedData **29,063,336**; onlyLongs **21,479,973**; onlyStrings **74,100,000** bytes (see [index.md](index.md)).",
        "",
    ]

    lines.append("## Data quality")
    for n in qc_notes:
        lines.append(f"- {n}")
    lines.append(
        "- Sizes with a leading zero on the token (e.g. `02800000` for **onlyStrings** Thrift binary/compact `none`) are shown as *(invalid in source)*; re-measure those files."
    )
    lines.append("")

    lines.append("## Results by format")
    lines.append("")

    for section_title, slug, cols in FORMAT_ORDER:
        lines.append(f"### {section_title}")
        lines.append("")
        fmt_data = tables.get(slug, {})
        row_labels = ["mixedData", "onlyLongs", "onlyStrings"]
        md_rows: list[tuple[str, list[str]]] = []
        for ds in row_labels:
            cells = []
            for c in cols:
                tup = fmt_data.get(ds, {}).get(c)
                if tup is None:
                    cells.append("—")
                else:
                    size, ok = tup
                    cells.append(cell_value(size, ok))
            md_rows.append((ds, cells))
        lines.append(markdown_table(cols, md_rows))
        lines.append("")

    return "\n".join(lines).rstrip() + "\n"


def main() -> None:
    root = Path(__file__).resolve().parents[1]
    results = root / "results.txt"
    out = root / "results" / "2026" / "file-sizes.md"
    if not results.is_file():
        print(f"Missing {results}", file=sys.stderr)
        sys.exit(1)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(build_markdown(results))
    print(f"Wrote {out}")


if __name__ == "__main__":
    main()
