#!/usr/bin/env python3
"""Parse ScalaMeter stash benchmark logs into structured timings."""

from __future__ import annotations

import re
from pathlib import Path
from typing import Callable

ROOT = Path(__file__).resolve().parents[1]
STASH = ROOT / "stash"

PARAM_RE = re.compile(r"Parameters\(([^)]+)\):\s*([\d.]+)\s*ms")
BENCH_RE = re.compile(r"::Benchmark\s+(.+?)::")

# (table_key, data_type, ser|de, codec_column) -> "123.45 ms"
Store = dict[tuple[str, str, str, str], str]


def parse_kv(inner: str) -> dict[str, str]:
    out: dict[str, str] = {}
    for segment in inner.split(", "):
        if " -> " in segment:
            k, v = segment.split(" -> ", 1)
            out[k.strip()] = v.strip()
    return out


def orc_codec(raw: str) -> str:
    m = {"none": "none", "snappy": "snappy", "zlib": "zlib", "lzo": "lz0", "lz4": "lz4", "zstd": "zstd", "brotli": "brotli"}
    return m[raw.lower()]


def parquet_codec(raw: str) -> str:
    m = {"uncompressed": "none", "snappy": "snappy", "gzip": "gzip", "lz4": "lz4", "zstd": "zstd"}
    return m[raw.lower()]


def lower_codec(raw: str) -> str:
    return raw.lower()


def parse_java_json(store: Store, path: Path, table: str, is_de: bool) -> None:
    mode = "de" if is_de else "ser"
    text = path.read_text(encoding="utf-8", errors="replace")
    for line in text.splitlines():
        m = PARAM_RE.search(line)
        if not m:
            continue
        kv = parse_kv(m.group(1))
        dt = kv.get("input file", "")
        codec = lower_codec(kv.get("compression", ""))
        val = f"{m.group(2)} ms"
        store[(table, dt, mode, codec)] = val


def parse_avro(store: Store, path: Path, is_de: bool) -> None:
    mode = "de" if is_de else "ser"
    text = path.read_text(encoding="utf-8", errors="replace")
    for line in text.splitlines():
        m = PARAM_RE.search(line)
        if not m:
            continue
        kv = parse_kv(m.group(1))
        dt = kv.get("data type", "")
        codec = lower_codec(kv.get("codec", ""))
        val = f"{m.group(2)} ms"
        store[("avro", dt, mode, codec)] = val


def parse_title_context(title: str) -> tuple[str | None, str | None]:
    """Returns (data_type, variant) where variant is thrift_binary, thrift_compact, parquet_avro, parquet_thrift."""
    t = title
    # Thrift serialize
    if "thrift serialization.serialize using binary protocol" in t:
        if " - mixed data" in t or t.rstrip().endswith("mixed data"):
            return "mixedData", "thrift_binary"
        if "only strings" in t:
            return "onlyStrings", "thrift_binary"
        if "only longs" in t:
            return "onlyLongs", "thrift_binary"
    if "thrift serialization.serialize using compact protocol" in t:
        if "mixed data" in t:
            return "mixedData", "thrift_compact"
        if "only strings" in t:
            return "onlyStrings", "thrift_compact"
        if "only longs" in t:
            return "onlyLongs", "thrift_compact"
    # Thrift deserialize
    if "thrift deserialization.binary deserialization" in t:
        if "mixed data" in t:
            return "mixedData", "thrift_binary"
        if "only strings" in t:
            return "onlyStrings", "thrift_binary"
        if "only longs" in t:
            return "onlyLongs", "thrift_binary"
    if "thrift deserialization.compact deserialization" in t:
        if "mixed data" in t:
            return "mixedData", "thrift_compact"
        if "only strings" in t:
            return "onlyStrings", "thrift_compact"
        if "only longs" in t:
            return "onlyLongs", "thrift_compact"
    # Parquet serialize
    if "parquet serialization.parquet-avro serialize" in t:
        if "mixed data" in t:
            return "mixedData", "parquet_avro"
        if "only strings" in t:
            return "onlyStrings", "parquet_avro"
        if "only longs" in t:
            return "onlyLongs", "parquet_avro"
    if "parquet serialization.parquet-thrift serialize" in t:
        if "mixed data" in t:
            return "mixedData", "parquet_thrift"
        if "only strings" in t:
            return "onlyStrings", "parquet_thrift"
        if "only longs" in t:
            return "onlyLongs", "parquet_thrift"
    # Parquet deserialize
    if "parquet deserialization.deserialize-avro" in t:
        if "mixed data" in t:
            return "mixedData", "parquet_avro"
        if "only strings" in t:
            return "onlyStrings", "parquet_avro"
        if "only longs" in t:
            return "onlyLongs", "parquet_avro"
    if "parquet deserialization.deserialize-thrift" in t:
        if "mixed data" in t:
            return "mixedData", "parquet_thrift"
        if "only strings" in t:
            return "onlyStrings", "parquet_thrift"
        if "only longs" in t:
            return "onlyLongs", "parquet_thrift"
    # ORC
    if "orc serialization.serialize" in t or "orc deserialization.deserialize" in t:
        if "mixed data" in t:
            return "mixedData", None
        if "only strings" in t:
            return "onlyStrings", None
        if "only longs" in t:
            return "onlyLongs", None
    # Protobuf / msgpack
    if "protobuf serialization.serialize" in t or "protobuf deserialization.deserialize" in t:
        if "mixed data" in t:
            return "mixedData", None
        if "only strings" in t:
            return "onlyStrings", None
        if "only longs" in t:
            return "onlyLongs", None
    if "msgpack serialization.serialize" in t or "msgpack deserialization.deserialize" in t:
        if "mixed data" in t:
            return "mixedData", None
        if "only strings" in t:
            return "onlyStrings", None
        if "only longs" in t:
            return "onlyLongs", None
    # CBOR: "mixedData cbor serialization.serialize" or "mixedData cbor deserialization.deserialize"
    if "cbor serialization.serialize" in t or "cbor deserialization.deserialize" in t:
        if t.startswith("mixedData ") or " mixedData " in t:
            return "mixedData", None
        if t.startswith("onlyStrings ") or " onlyStrings " in t:
            return "onlyStrings", None
        if t.startswith("onlyLongs ") or " onlyLongs " in t:
            return "onlyLongs", None
    return None, None


def parse_sectioned(
    store: Store,
    path: Path,
    table: str,
    is_de: bool,
    codec_fn: Callable[[str], str],
) -> None:
    mode = "de" if is_de else "ser"
    text = path.read_text(encoding="utf-8", errors="replace")
    variant: str | None = None
    data_type: str | None = None
    for line in text.splitlines():
        bm = BENCH_RE.search(line)
        if bm:
            title = bm.group(1).strip()
            dt, var = parse_title_context(title)
            if dt is not None:
                data_type = dt
            if var is not None:
                variant = var
            # protobuf/msgpack/orc don't set variant
            continue
        m = PARAM_RE.search(line)
        if not m:
            continue
        kv = parse_kv(m.group(1))
        if "compression" not in kv:
            continue
        raw = kv.get("compression", "")
        codec = codec_fn(raw)
        val = f"{m.group(2)} ms"
        key_table = variant if variant and variant.startswith(("thrift_", "parquet_")) else table
        if data_type is None:
            raise RuntimeError(f"missing data_type for {path}: {line}")
        store[(key_table, data_type, mode, codec)] = val


def parse_cbor(store: Store, path: Path, is_de: bool) -> None:
    mode = "de" if is_de else "ser"
    text = path.read_text(encoding="utf-8", errors="replace")
    data_type: str | None = None
    for line in text.splitlines():
        bm = BENCH_RE.search(line)
        if bm:
            title = bm.group(1).strip()
            if title.startswith("mixedData "):
                data_type = "mixedData"
            elif title.startswith("onlyStrings "):
                data_type = "onlyStrings"
            elif title.startswith("onlyLongs "):
                data_type = "onlyLongs"
            continue
        m = PARAM_RE.search(line)
        if not m:
            continue
        kv = parse_kv(m.group(1))
        codec = lower_codec(kv.get("compression", ""))
        val = f"{m.group(2)} ms"
        if data_type is None:
            raise RuntimeError(f"missing data_type for {path}: {line}")
        store[("cbor", data_type, mode, codec)] = val


def load_all() -> Store:
    store: Store = {}
    parse_java_json(store, STASH / "javaSerializingBenchResults.txt", "java", False)
    parse_java_json(store, STASH / "javaDeserializingBenchResults.txt", "java", True)
    parse_java_json(store, STASH / "jsonSerializingBenchResults.txt", "json", False)
    parse_java_json(store, STASH / "jsonDeserializingBenchResults.txt", "json", True)
    parse_avro(store, STASH / "avroSerializingBenchResults.txt", False)
    parse_avro(store, STASH / "avroDeserializingBenchResults.txt", True)

    for path, is_de in [
        (STASH / "thriftSerializingBenchResults.txt", False),
        (STASH / "thriftDeserializingBenchResults.txt", True),
    ]:
        parse_sectioned(store, path, "thrift", is_de, lower_codec)

    for path, is_de in [
        (STASH / "protobufSerializingBenchResults.txt", False),
        (STASH / "protobufDeserializingBenchResults.txt", True),
        (STASH / "msgpackSerializingBenchResults.txt", False),
        (STASH / "msgpackDeserializingBenchResults.txt", True),
    ]:
        parse_sectioned(store, path, "protobuf" if "protobuf" in path.name else "msgpack", is_de, lower_codec)

    for path, is_de in [
        (STASH / "orcSerializingBenchResults.txt", False),
        (STASH / "orcDeserializingBenchResults.txt", True),
    ]:
        parse_sectioned(store, path, "orc", is_de, orc_codec)

    for path, is_de in [
        (STASH / "parquetSerializingBenchResults.txt", False),
        (STASH / "parquetDeserializingBenchResults.txt", True),
    ]:
        parse_sectioned(store, path, "parquet", is_de, parquet_codec)

    parse_cbor(store, STASH / "cborSerializingBenchResults.txt", False)
    parse_cbor(store, STASH / "cborDeserializingBenchResults.txt", True)
    return store


ROW_ORDER = [
    ("mixedData", "ser", "Serialization"),
    ("mixedData", "de", "Deserialization"),
    ("onlyLongs", "ser", "Serialization"),
    ("onlyLongs", "de", "Deserialization"),
    ("onlyStrings", "ser", "Serialization"),
    ("onlyStrings", "de", "Deserialization"),
]


FC = 31


def md_table(table_key: str, columns: list[str], store: Store) -> str:
    widths = [len(c) for c in columns]
    body: list[tuple[str, list[str]]] = []
    for dt, smode, slabel in ROW_ORDER:
        vals = [store.get((table_key, dt, smode, c), "") for c in columns]
        body.append((f"{dt} ({slabel})", vals))
        for i, v in enumerate(vals):
            widths[i] = max(widths[i], len(v))
    for i, c in enumerate(columns):
        widths[i] = max(widths[i], len(c))
    lines: list[str] = []
    lines.append("|" + " " * FC + "| " + " | ".join(f"{columns[i]:<{widths[i]}}" for i in range(len(columns))) + " |")
    lines.append("|" + "-" * FC + "|" + "".join("-" * (widths[i] + 2) + "|" for i in range(len(columns))))
    for lbl, vals in body:
        pad = FC - 1 - len(lbl)
        lines.append("| " + lbl + " " * pad + "| " + " | ".join(f"{vals[i]:<{widths[i]}}" for i in range(len(columns))) + " |")
    return "\n".join(lines)


def render_results_section(store: Store) -> str:
    blocks: list[str] = []
    blocks.append("## Results")
    blocks.append("### Java")
    blocks.append(md_table("java", ["none", "gzip", "snappy", "lz4", "xz"], store))
    blocks.append("")
    blocks.append("### Json")
    blocks.append(md_table("json", ["none", "gzip", "snappy", "lz4", "xz"], store))
    blocks.append("")
    blocks.append("### Avro")
    blocks.append(md_table("avro", ["none", "deflate", "snappy", "xz", "bzip2", "zstd"], store))
    blocks.append("")
    blocks.append("### Thrift (binary protocol)")
    blocks.append(md_table("thrift_binary", ["none", "gzip", "snappy", "lz4", "xz"], store))
    blocks.append("")
    blocks.append("### Thrift (compact protocol)")
    blocks.append(md_table("thrift_compact", ["none", "gzip", "snappy", "lz4", "xz"], store))
    blocks.append("")
    blocks.append("### Protobuf")
    blocks.append(md_table("protobuf", ["none", "gzip", "snappy", "lz4", "xz"], store))
    blocks.append("")
    blocks.append("### ORC")
    blocks.append(md_table("orc", ["none", "snappy", "zlib", "lz0", "lz4", "zstd", "brotli"], store))
    blocks.append("")
    blocks.append("### Parquet (parquet-avro)")
    blocks.append(md_table("parquet_avro", ["none", "snappy", "gzip", "lz4", "zstd"], store))
    blocks.append("")
    blocks.append("### Parquet (parquet-thrift)")
    blocks.append(md_table("parquet_thrift", ["none", "snappy", "gzip", "lz4", "zstd"], store))
    blocks.append("")
    blocks.append("### Msgpack")
    blocks.append(md_table("msgpack", ["none", "gzip", "snappy", "lz4", "xz"], store))
    blocks.append("")
    blocks.append("### CBOR")
    blocks.append(md_table("cbor", ["none", "gzip", "snappy", "lz4", "xz"], store))
    return "\n".join(blocks)


def write_index_md(store: Store) -> None:
    path = ROOT / "results" / "2026" / "index.md"
    head = path.read_text(encoding="utf-8").split("## Results")[0].rstrip()
    new_body = render_results_section(store)
    path.write_text(head + "\n\n" + new_body + "\n", encoding="utf-8")


def main() -> None:
    store = load_all()
    keys_by_table: dict[str, int] = {}
    for k in store:
        keys_by_table[k[0]] = keys_by_table.get(k[0], 0) + 1
    for t in sorted(keys_by_table):
        print(f"{t}: {keys_by_table[t]} cells")
    assert store[("orc", "mixedData", "ser", "lz0")]
    assert store[("parquet_avro", "mixedData", "ser", "none")]
    write_index_md(store)
    print("Wrote results/2026/index.md")


if __name__ == "__main__":
    main()
