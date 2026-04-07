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
|                               | none          | gzip           | snappy        | lz4           | xz             |
|-------------------------------|---------------|----------------|---------------|---------------|----------------|
| mixedData (Serialization)     | 525.317708 ms | 1765.220042 ms | 558.592875 ms | 563.622583 ms | 15000.2835 ms  |
| mixedData (Deserialization)   | 217.578333 ms | 1895.090375 ms | 256.130375 ms | 277.730208 ms | 1325.384625 ms |
| onlyLongs (Serialization)     | 424.284833 ms | 1276.209208 ms | 437.758375 ms | 447.201125 ms | 7951.579291 ms |
| onlyLongs (Deserialization)   | 165.8765 ms   | 1639.562375 ms | 185.383875 ms | 196.64275 ms  | 860.0375 ms    |
| onlyStrings (Serialization)   | 604.6075 ms   | 2958.380042 ms | 693.96075 ms  | 736.021125 ms | 58269.09675 ms |
| onlyStrings (Deserialization) | 346.262834 ms | 2581.052667 ms | 353.713792 ms | 399.143833 ms | 3226.977166 ms |

### Json
|                               | none          | gzip           | snappy         | lz4           | xz              |
|-------------------------------|---------------|----------------|----------------|---------------|-----------------|
| mixedData (Serialization)     | 891.55275 ms  | 2424.37325 ms  | 946.911541 ms  | 804.975834 ms | 28948.656292 ms |
| mixedData (Deserialization)   | 494.890208 ms | 642.374458 ms  | 599.186167 ms  | 533.824916 ms | 1509.561166 ms  |
| onlyLongs (Serialization)     | 844.350084 ms | 2470.836542 ms | 893.988541 ms  | 732.317625 ms | 21289.656375 ms |
| onlyLongs (Deserialization)   | 444.705583 ms | 530.605917 ms  | 552.272541 ms  | 468.657084 ms | 1199.108542 ms  |
| onlyStrings (Serialization)   | 928.621416 ms | 4164.862666 ms | 1046.281459 ms | 1005.97775 ms | 72094.750417 ms |
| onlyStrings (Deserialization) | 565.82275 ms  | 905.338417 ms  | 676.0855 ms    | 665.339333 ms | 2576.815792 ms  |

### Avro
|                               | none          | deflate        | snappy        | xz              | bzip2          | zstd          |
|-------------------------------|---------------|----------------|---------------|-----------------|----------------|---------------|
| mixedData (Serialization)     | 353.40025 ms  | 858.89925 ms   | 319.215917 ms | 3324.686125 ms  | 1834.378125 ms | 446.0565 ms   |
| mixedData (Deserialization)   | 88.893625 ms  | 180.860958 ms  | 86.536333 ms  | 1055.159 ms     | 829.777583 ms  | 96.918417 ms  |
| onlyLongs (Serialization)     | 261.297958 ms | 574.797125 ms  | 274.075458 ms | 1760.892375 ms  | 1050.637833 ms | 356.014209 ms |
| onlyLongs (Deserialization)   | 103.2345 ms   | 128.639042 ms  | 81.320083 ms  | 658.537084 ms   | 512.222708 ms  | 100.177083 ms |
| onlyStrings (Serialization)   | 346.958375 ms | 2019.496458 ms | 393.788 ms    | 10517.786667 ms | 4792.762667 ms | 732.7095 ms   |
| onlyStrings (Deserialization) | 79.234292 ms  | 405.127917 ms  | 96.952541 ms  | 3031.618583 ms  | 2262.174792 ms | 168.246916 ms |

### Thrift (binary protocol)
|                               | none          | gzip           | snappy        | lz4           | xz              |
|-------------------------------|---------------|----------------|---------------|---------------|-----------------|
| mixedData (Serialization)     | 291.803541 ms | 1116.843333 ms | 304.548917 ms | 311.895458 ms | 11697.337833 ms |
| mixedData (Deserialization)   | 57.53175 ms   | 207.047875 ms  | 51.207208 ms  | 68.284 ms     | 934.610625 ms   |
| onlyLongs (Serialization)     | 247.794042 ms | 860.085875 ms  | 251.871167 ms | 259.2915 ms   | 4581.480208 ms  |
| onlyLongs (Deserialization)   | 40.950167 ms  | 122.654875 ms  | 40.288917 ms  | 44.065375 ms  | 518.7765 ms     |
| onlyStrings (Serialization)   | 373.1155 ms   | 2583.056959 ms | 443.04775 ms  | 475.688708 ms | 47414.303375 ms |
| onlyStrings (Deserialization) | 88.1925 ms    | 398.442416 ms  | 100.076 ms    | 143.671875 ms | 2782.062417 ms  |

### Thrift (compact protocol)
|                               | none          | gzip           | snappy        | lz4           | xz              |
|-------------------------------|---------------|----------------|---------------|---------------|-----------------|
| mixedData (Serialization)     | 288.602125 ms | 975.608583 ms  | 298.058708 ms | 306.376458 ms | 10259.964875 ms |
| mixedData (Deserialization)   | 52.714292 ms  | 195.127792 ms  | 51.226292 ms  | 68.483625 ms  | 920.832042 ms   |
| onlyLongs (Serialization)     | 250.652375 ms | 601.097875 ms  | 254.570542 ms | 255.161208 ms | 4236.633416 ms  |
| onlyLongs (Deserialization)   | 51.52475 ms   | 132.3475 ms    | 46.290375 ms  | 49.445542 ms  | 569.856875 ms   |
| onlyStrings (Serialization)   | 370.851375 ms | 2431.398666 ms | 421.859625 ms | 462.167667 ms | 44550.373167 ms |
| onlyStrings (Deserialization) | 81.873292 ms  | 371.270834 ms  | 93.611084 ms  | 131.578459 ms | 2631.721584 ms  |

### Protobuf
|                               | none          | gzip           | snappy        | lz4           | xz              |
|-------------------------------|---------------|----------------|---------------|---------------|-----------------|
| mixedData (Serialization)     | 281.438333 ms | 1063.169375 ms | 276.650542 ms | 298.362208 ms | 9960.234125 ms  |
| mixedData (Deserialization)   | 30.77675 ms   | 166.449375 ms  | 25.624458 ms  | 35.657584 ms  | 849.569542 ms   |
| onlyLongs (Serialization)     | 234.889708 ms | 627.430458 ms  | 233.467584 ms | 236.58975 ms  | 2921.27975 ms   |
| onlyLongs (Deserialization)   | 18.334458 ms  | 94.885 ms      | 17.5045 ms    | 20.963625 ms  | 537.799417 ms   |
| onlyStrings (Serialization)   | 370.554334 ms | 2584.271542 ms | 370.923292 ms | 462.642708 ms | 44239.930875 ms |
| onlyStrings (Deserialization) | 42.067208 ms  | 353.556166 ms  | 41.993375 ms  | 89.152792 ms  | 2554.633917 ms  |

### ORC
|                               | none          | snappy        | zlib           | lz0           | lz4           | zstd          | brotli        |
|-------------------------------|---------------|---------------|----------------|---------------|---------------|---------------|---------------|
| mixedData (Serialization)     | 449.3675 ms   | 492.817083 ms | 694.49525 ms   | 468.987667 ms | 466.57025 ms  | 572.556041 ms | 481.931792 ms |
| mixedData (Deserialization)   | 60.418542 ms  | 65.091208 ms  | 91.969875 ms   | 39.00975 ms   | 48.920417 ms  | 56.596542 ms  | 93.53175 ms   |
| onlyLongs (Serialization)     | 232.07425 ms  | 246.529083 ms | 378.719333 ms  | 247.086917 ms | 252.816833 ms | 283.815708 ms | 282.683125 ms |
| onlyLongs (Deserialization)   | 21.534583 ms  | 32.15375 ms   | 51.313375 ms   | 26.978458 ms  | 23.348292 ms  | 26.459 ms     | 52.476375 ms  |
| onlyStrings (Serialization)   | 438.731375 ms | 568.206667 ms | 1014.746958 ms | 477.503666 ms | 462.617875 ms | 854.867791 ms | 553.061375 ms |
| onlyStrings (Deserialization) | 33.63125 ms   | 127.602625 ms | 186.927666 ms  | 45.4295 ms    | 34.435083 ms  | 112.196167 ms | 181.760084 ms |

### Parquet (parquet-avro)
|                               | none          | snappy        | gzip           | lz4           | zstd          |
|-------------------------------|---------------|---------------|----------------|---------------|---------------|
| mixedData (Serialization)     | 425.941584 ms | 446.326958 ms | 910.989666 ms  | 435.804375 ms | 496.065709 ms |
| mixedData (Deserialization)   | 57.245833 ms  | 187.234791 ms | 121.470583 ms  | 58.793584 ms  | 71.868375 ms  |
| onlyLongs (Serialization)     | 325.427709 ms | 327.327375 ms | 436.581 ms     | 331.411208 ms | 340.448125 ms |
| onlyLongs (Deserialization)   | 48.412583 ms  | 48.922375 ms  | 54.880416 ms   | 51.133833 ms  | 51.654 ms     |
| onlyStrings (Serialization)   | 660.316083 ms | 747.273583 ms | 2650.005083 ms | 701.93175 ms  | 937.820375 ms |
| onlyStrings (Deserialization) | 95.755667 ms  | 119.551416 ms | 359.492958 ms  | 100.653291 ms | 145.626583 ms |

### Parquet (parquet-thrift)
|                               | none          | snappy        | gzip           | lz4           | zstd          |
|-------------------------------|---------------|---------------|----------------|---------------|---------------|
| mixedData (Serialization)     | 476.912333 ms | 496.2225 ms   | 1025.429834 ms | 469.975458 ms | 544.324917 ms |
| mixedData (Deserialization)   | 117.728875 ms | 125.449666 ms | 187.889625 ms  | 121.963916 ms | 134.636042 ms |
| onlyLongs (Serialization)     | 474.174333 ms | 445.509667 ms | 723.26825 ms   | 453.885166 ms | 466.085 ms    |
| onlyLongs (Deserialization)   | 117.226875 ms | 117.748083 ms | 122.333958 ms  | 111.05825 ms  | 116.97675 ms  |
| onlyStrings (Serialization)   | 634.948042 ms | 733.3745 ms   | 2659.138542 ms | 701.290083 ms | 921.950125 ms |
| onlyStrings (Deserialization) | 166.875583 ms | 190.708791 ms | 435.912916 ms  | 170.559792 ms | 223.492166 ms |

### Msgpack
|                               | none          | gzip           | snappy        | lz4           | xz              |
|-------------------------------|---------------|----------------|---------------|---------------|-----------------|
| mixedData (Serialization)     | 257.4115 ms   | 870.583208 ms  | 258.927959 ms | 271.223375 ms | 9659.488792 ms  |
| mixedData (Deserialization)   | 62.424416 ms  | 146.34925 ms   | 67.707792 ms  | 79.617792 ms  | 850.028833 ms   |
| onlyLongs (Serialization)     | 222.666291 ms | 518.903792 ms  | 223.741916 ms | 226.11625 ms  | 2670.258292 ms  |
| onlyLongs (Deserialization)   | 23.790458 ms  | 55.870375 ms   | 25.675083 ms  | 28.498375 ms  | 498.35225 ms    |
| onlyStrings (Serialization)   | 326.300167 ms | 2332.785583 ms | 322.510708 ms | 409.66275 ms  | 44024.053208 ms |
| onlyStrings (Deserialization) | 80.8315 ms    | 334.868125 ms  | 87.956959 ms  | 131.925208 ms | 2611.694625 ms  |

### CBOR
|                               | none          | gzip           | snappy        | lz4           | xz              |
|-------------------------------|---------------|----------------|---------------|---------------|-----------------|
| mixedData (Serialization)     | 253.764708 ms | 1090.88975 ms  | 253.440167 ms | 276.037709 ms | 10479.243292 ms |
| mixedData (Deserialization)   | 47.199458 ms  | 188.402375 ms  | 37.972458 ms  | 52.940042 ms  | 822.294833 ms   |
| onlyLongs (Serialization)     | 216.420291 ms | 626.440625 ms  | 216.285458 ms | 222.444916 ms | 2742.637209 ms  |
| onlyLongs (Deserialization)   | 32.55625 ms   | 106.012375 ms  | 32.858459 ms  | 36.672583 ms  | 508.383583 ms   |
| onlyStrings (Serialization)   | 303.179375 ms | 2508.323166 ms | 316.428 ms    | 405.535417 ms | 43045.379916 ms |
| onlyStrings (Deserialization) | 62.763583 ms  | 377.677834 ms  | 60.806709 ms  | 109.494458 ms | 2249.011917 ms  |
