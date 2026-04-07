# 2026

## Library versions
```sbt
val hdfsVersion = "3.4.1"
val hiveExecVersion = "4.2.0"
val kantanVersion = "0.8.0"
val json4sVersion = "4.0.7"
val avro4sVersion = "4.1.2"
val confluent = "8.1.1"
val kafkaVersion = "4.2.0"
val scroogeVersion = "24.2.0"
val scalameterVersion = "0.21"
val finagleThriftVersion = "24.2.0"
val libthriftVersion = "0.22.0"
val orcVersion = "2.3.0"
val parquetVersion = "1.17.0"
val snappyVersion = "1.1.10.8"
val lz4Version = "1.8.1"
val apacheCommonCompressVersion = "1.28.0"
val msgpackVersion = "0.9.11"
val borerVersion = "1.8.0"
```

## Formats
1. Java
2. Json
3. Avro
4. Thrift
5. Protobuf
6. ORC
7. Parquet
8. Msgpack
9. CBOR

## System info
```
- CPU: Apple M4 Pro
- RAM: 48 GB
- macOS: 15.7
- External SSD: Adata 2.5" 500GB SE880 USB-C
- java: OpenJDK 64-Bit Server VM Temurin-21.0.5+11
- sbt: 1.12.6
- scala: 2.13.18
```

## Input data:
100k records:
- mixed data: **29063336** bytes
- only longs: **21479973** bytes
- only strings: **74100000** bytes

TODO: cbor (xz), orc (brotli), parquet (brotli)
```shell
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RECORDS_COUNT=100000 sbt generateDataSets

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC javaSerializingBench > javaSerializingBenchResults.txt
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC javaDeserializingBench > javaDeserializingBenchResults.txt

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC avroSerializingBench > avroSerializingBenchResults.txt
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC avroDeserializingBench > avroDeserializingBenchResults.txt

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC jsonSerializingBench > jsonSerializingBenchResults.txt
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC jsonDeserializingBench > jsonDeserializingBenchResults.txt

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC msgpackSerializingBench > msgpackSerializingBenchResults.txt
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC msgpackDeserializingBench > msgpackDeserializingBenchResults.txt

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC cborSerializingBench > cborSerializingBenchResults.txt
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC cborDeserializingBench > cborDeserializingBenchResults.txt

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC orcSerializingBench > orcSerializingBenchResults.txt
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC orcDeserializingBench > orcDeserializingBenchResults.txt

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC parquetSerializingBench > parquetSerializingBenchResults.txt
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC parquetDeserializingBench > parquetDeserializingBenchResults.txt

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC protobufSerializingBench > protobufSerializingBenchResults.txt
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC protobufDeserializingBench > protobufDeserializingBenchResults.txt

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC thriftSerializingBench > thriftSerializingBenchResults.txt
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_WARMUPS=1 BENCH_RUNS=1 BENCH_RECORDS_COUNT=100000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC thriftDeserializingBench > thriftDeserializingBenchResults.txt
```

## Results
### Java
|                               | none | gzip | snappy | lz4 | xz |
|-------------------------------|------|------|--------|-----|----|
| mixedData (Serialization)     |      |      |        |     |    |
| mixedData (Deserialization)   |      |      |        |     |    |
| onlyLongs (Serialization)     |      |      |        |     |    |
| onlyLongs (Deserialization)   |      |      |        |     |    |
| onlyStrings (Serialization)   |      |      |        |     |    |
| onlyStrings (Deserialization) |      |      |        |     |    |

### Json
|                               | none | gzip | snappy | lz4 | xz |
|-------------------------------|------|------|--------|-----|----|
| mixedData (Serialization)     |      |      |        |     |    |
| mixedData (Deserialization)   |      |      |        |     |    |
| onlyLongs (Serialization)     |      |      |        |     |    |
| onlyLongs (Deserialization)   |      |      |        |     |    |
| onlyStrings (Serialization)   |      |      |        |     |    |
| onlyStrings (Deserialization) |      |      |        |     |    |

### Avro
|                               | none | deflate | snappy | xz | bzip2 | zstd |
|-------------------------------|------|---------|--------|----|-------|------|
| mixedData (Serialization)     |      |         |        |    |       |      |
| mixedData (Deserialization)   |      |         |        |    |       |      |
| onlyLongs (Serialization)     |      |         |        |    |       |      |
| onlyLongs (Deserialization)   |      |         |        |    |       |      |
| onlyStrings (Serialization)   |      |         |        |    |       |      |
| onlyStrings (Deserialization) |      |         |        |    |       |      |

### Thrift
|                               | none | gzip | snappy | lz4 | xz |
|-------------------------------|------|------|--------|-----|----|
| mixedData (Serialization)     |      |      |        |     |    |
| mixedData (Deserialization)   |      |      |        |     |    |
| onlyLongs (Serialization)     |      |      |        |     |    |
| onlyLongs (Deserialization)   |      |      |        |     |    |
| onlyStrings (Serialization)   |      |      |        |     |    |
| onlyStrings (Deserialization) |      |      |        |     |    |

### Protobuf
|                               | none | gzip | snappy | lz4 | xz |
|-------------------------------|------|------|--------|-----|----|
| mixedData (Serialization)     |      |      |        |     |    |
| mixedData (Deserialization)   |      |      |        |     |    |
| onlyLongs (Serialization)     |      |      |        |     |    |
| onlyLongs (Deserialization)   |      |      |        |     |    |
| onlyStrings (Serialization)   |      |      |        |     |    |
| onlyStrings (Deserialization) |      |      |        |     |    |

### ORC
|                               | none | snappy | zlib | lz0 | lz4 | zstd | brotli |
|-------------------------------|------|--------|------|-----|-----|------|--------|
| mixedData (Serialization)     |      |        |      |     |     |      |        |
| mixedData (Deserialization)   |      |        |      |     |     |      |        |
| onlyLongs (Serialization)     |      |        |      |     |     |      |        |
| onlyLongs (Deserialization)   |      |        |      |     |     |      |        |
| onlyStrings (Serialization)   |      |        |      |     |     |      |        |
| onlyStrings (Deserialization) |      |        |      |     |     |      |        |

### Parquet
|                               | none | snappy | gzip | lz4 | zstd |
|-------------------------------|------|--------|------|-----|------|
| mixedData (Serialization)     |      |        |      |     |      |
| mixedData (Deserialization)   |      |        |      |     |      |
| onlyLongs (Serialization)     |      |        |      |     |      |
| onlyLongs (Deserialization)   |      |        |      |     |      |
| onlyStrings (Serialization)   |      |        |      |     |      |
| onlyStrings (Deserialization) |      |        |      |     |      |

### Msgpack
|                               | none | gzip | snappy | lz4 | xz |
|-------------------------------|------|------|--------|-----|----|
| mixedData (Serialization)     |      |      |        |     |    |
| mixedData (Deserialization)   |      |      |        |     |    |
| onlyLongs (Serialization)     |      |      |        |     |    |
| onlyLongs (Deserialization)   |      |      |        |     |    |
| onlyStrings (Serialization)   |      |      |        |     |    |
| onlyStrings (Deserialization) |      |      |        |     |    |

### CBOR
|                               | none | gzip | snappy | lz4 | xz |
|-------------------------------|------|------|--------|-----|----|
| mixedData (Serialization)     |      |      |        |     |    |
| mixedData (Deserialization)   |      |      |        |     |    |
| onlyLongs (Serialization)     |      |      |        |     |    |
| onlyLongs (Deserialization)   |      |      |        |     |    |
| onlyStrings (Serialization)   |      |      |        |     |    |
| onlyStrings (Deserialization) |      |      |        |     |    |