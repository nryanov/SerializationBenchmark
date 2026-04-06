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
1kk records:
- mixed data: **290736953** bytes
- only longs: **214794129** bytes
- only strings: **741000000** bytes


```shell
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RECORDS_COUNT=1000000 sbt generateDataSets

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC javaSerializingBench
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC javaDeserializingBench

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC avroSerializingBench
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC avroDeserializingBench

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC cborSerializingBench
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC cborDeserializingBench

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC jsonSerializingBench
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC jsonDeserializingBench

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC msgpackSerializingBench
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC msgpackDeserializingBench

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC orcSerializingBench
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC orcDeserializingBench

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC parquetSerializingBench
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC parquetDeserializingBench

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC protobufSerializingBench
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC protobufDeserializingBench

BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC thriftSerializingBench
BENCH_DATA_DIR=/Volumes/ADATA/bench BENCH_RUNS=3 BENCH_RECORDS_COUNT=1000000 sbt -J-Xmx16G -J-Xms16G -J-XX:+UseParallelGC thriftDeserializingBench
```

## Results
### Java
|             | none | gzip | snappy | lz4 | xz |
|-------------|------|------|--------|-----|----|
| mixedData   |      |      |        |     |    |
| onlyLong    |      |      |        |     |    |
| onlyStrings |      |      |        |     |    |

### Json
### Avro
### Thrift
### Protobuf
### ORC
### Parquet
### Msgpack
### CBOR