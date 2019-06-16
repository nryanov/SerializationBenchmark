# SerializationBenchmark

`sbt clean compile` - will generate Thrift and Protobuf classes

# Formats
1. Java
2. Json
3. Avro
4. Thrift
5. Protobuf
6. ORC
7. Parquet
8. Msgpack

# System info
```cores: 4
hostname: MAC
name: Java HotSpot(TM) 64-Bit Server VM
osArch: x86_64
osName: Mac OS X
vendor: Oracle Corporation
version: 25,144-b01
```

# Input data:
100k records (mixed data - 28m, only longs - 20m, only strings - 71m)

# Results

## Java
```
::Benchmark java serialization of mixed data,serialize::
Parameters(input file -> onlyLongs, compression -> none): 2307,343483
Parameters(input file -> onlyLongs, compression -> gzip): 5330,220676
Parameters(input file -> onlyLongs, compression -> snappy): 2358,128772
Parameters(input file -> onlyLongs, compression -> lz4): 2365,895879
Parameters(input file -> mixedData, compression -> none): 3126,791457
Parameters(input file -> mixedData, compression -> gzip): 6081,32952
Parameters(input file -> mixedData, compression -> snappy): 3247,205597
Parameters(input file -> mixedData, compression -> lz4): 3259,827716
Parameters(input file -> onlyStrings, compression -> none): 4860,952922
Parameters(input file -> onlyStrings, compression -> gzip): 13676,610679
Parameters(input file -> onlyStrings, compression -> snappy): 5235,227873
Parameters(input file -> onlyStrings, compression -> lz4): 5081,706156

::Benchmark java deserialization,deserialize::
Parameters(input file -> onlyLongs, compression -> none): 518,977382
Parameters(input file -> onlyLongs, compression -> gzip): 4209,220863
Parameters(input file -> onlyLongs, compression -> snappy): 574,319878
Parameters(input file -> onlyLongs, compression -> lz4): 533,385892
Parameters(input file -> mixedData, compression -> none): 830,198135
Parameters(input file -> mixedData, compression -> gzip): 6175,447826
Parameters(input file -> mixedData, compression -> snappy): 916,241318
Parameters(input file -> mixedData, compression -> lz4): 849,70505
Parameters(input file -> onlyStrings, compression -> none): 1702,41183
Parameters(input file -> onlyStrings, compression -> gzip): 7570,618521
Parameters(input file -> onlyStrings, compression -> snappy): 1740,563435
Parameters(input file -> onlyStrings, compression -> lz4): 1835,862634

28M mixedDataInput,csv
36M mixedDataJavaSerialization,out
17M mixedDataJavaSerializationGzip,out
24M mixedDataJavaSerializationLz4,out
25M mixedDataJavaSerializationSnappy,out

20M onlyLongsInput,csv
24M onlyLongsJavaSerialization,out
9,7M onlyLongsJavaSerializationGzip,out
12M onlyLongsJavaSerializationLz4,out
12M onlyLongsJavaSerializationSnappy,out

71M onlyStringsInput,csv
86M onlyStringsJavaSerialization,out
42M onlyStringsJavaSerializationGzip,out
67M onlyStringsJavaSerializationLz4,out
70M onlyStringsJavaSerializationSnappy,out
```

## Json
```
::Benchmark json serialization,serialize::
Parameters(input file -> onlyLongs, compression -> none): 3378,21702
Parameters(input file -> onlyLongs, compression -> gzip): 7515,758612
Parameters(input file -> onlyLongs, compression -> snappy): 3970,879088
Parameters(input file -> onlyLongs, compression -> lz4): 3462,407938
Parameters(input file -> mixedData, compression -> none): 4152,190077
Parameters(input file -> mixedData, compression -> gzip): 10341,347296
Parameters(input file -> mixedData, compression -> snappy): 4825,457419
Parameters(input file -> mixedData, compression -> lz4): 4215,609799
Parameters(input file -> onlyStrings, compression -> none): 4996,537694
Parameters(input file -> onlyStrings, compression -> gzip): 12896,301143
Parameters(input file -> onlyStrings, compression -> snappy): 7153,249829
Parameters(input file -> onlyStrings, compression -> lz4): 6208,99438

::Benchmark json deserialization,deserialize::
Parameters(input file -> onlyLongs, compression -> none): 1927,306516
Parameters(input file -> onlyLongs, compression -> gzip): 2128,294964
Parameters(input file -> onlyLongs, compression -> snappy): 2206,879477
Parameters(input file -> onlyLongs, compression -> lz4): 2693,439676
Parameters(input file -> mixedData, compression -> none): 2301,310475
Parameters(input file -> mixedData, compression -> gzip): 2673,78582
Parameters(input file -> mixedData, compression -> snappy): 3302,446934
Parameters(input file -> mixedData, compression -> lz4): 2325,333123
Parameters(input file -> onlyStrings, compression -> none): 2826,935487
Parameters(input file -> onlyStrings, compression -> gzip): 3533,28738
Parameters(input file -> onlyStrings, compression -> snappy): 3172,354901
Parameters(input file -> onlyStrings, compression -> lz4): 2906,763224

28M mixedDataInput,csv
52M mixedDataJsonSerialization,out
21M mixedDataJsonSerializationGzip,out
35M mixedDataJsonSerializationLz4,out
50M mixedDataJsonSerializationSnappy,out

20M onlyLongsInput,csv
36M onlyLongsJsonSerialization,out
13M onlyLongsJsonSerializationGzip,out
23M onlyLongsJsonSerializationLz4,out
35M onlyLongsJsonSerializationSnappy,out

71M onlyStringsInput,csv
124M onlyStringsJsonSerialization,out
55M onlyStringsJsonSerializationGzip,out
87M onlyStringsJsonSerializationLz4,out
111M onlyStringsJsonSerializationSnappy,out

```

## Avro
```
::Benchmark avro serialization,serialize with schema::
Parameters(data type -> onlyLongs, codec -> none, format -> data): 1023,797601 
Parameters(data type -> onlyLongs, codec -> snappy, format -> data): 1092,880638 
Parameters(data type -> onlyLongs, codec -> deflate, format -> data): 1768,109666 
Parameters(data type -> onlyLongs, codec -> bzip2, format -> data): 3030,31074 
Parameters(data type -> onlyLongs, codec -> xz, format -> data): 6767,382899 
Parameters(data type -> mixedData, codec -> none, format -> data): 1467,348812 
Parameters(data type -> mixedData, codec -> snappy, format -> data): 1529,233246 
Parameters(data type -> mixedData, codec -> deflate, format -> data): 2646,90537 
Parameters(data type -> mixedData, codec -> bzip2, format -> data): 5239,479994 
Parameters(data type -> mixedData, codec -> xz, format -> data): 13762,371548 
Parameters(data type -> onlyStrings, codec -> none, format -> data): 2022,198691 
Parameters(data type -> onlyStrings, codec -> snappy, format -> data): 2128,305766 
Parameters(data type -> onlyStrings, codec -> deflate, format -> data): 6091,337871 
Parameters(data type -> onlyStrings, codec -> bzip2, format -> data): 15218,592615 
Parameters(data type -> onlyStrings, codec -> xz, format -> data): 53867,517186 

::Benchmark deserialization avro,deserialize - data::
Parameters(data type -> onlyLongs, codec -> none, format -> data): 528,70283 
Parameters(data type -> onlyLongs, codec -> snappy, format -> data): 545,836483 
Parameters(data type -> onlyLongs, codec -> deflate, format -> data): 697,207265 
Parameters(data type -> onlyLongs, codec -> bzip2, format -> data): 1666,161023 
Parameters(data type -> onlyLongs, codec -> xz, format -> data): 2216,472777 
Parameters(data type -> mixedData, codec -> none, format -> data): 629,234235 
Parameters(data type -> mixedData, codec -> snappy, format -> data): 626,129412 
Parameters(data type -> mixedData, codec -> deflate, format -> data): 1051,847536 
Parameters(data type -> mixedData, codec -> bzip2, format -> data): 2641,435113 
Parameters(data type -> mixedData, codec -> xz, format -> data): 3560,629288 
Parameters(data type -> onlyStrings, codec -> none, format -> data): 833,421674 
Parameters(data type -> onlyStrings, codec -> snappy, format -> data): 937,640857 
Parameters(data type -> onlyStrings, codec -> deflate, format -> data): 2011,511644 
Parameters(data type -> onlyStrings, codec -> bzip2, format -> data): 6732,656743 
Parameters(data type -> onlyStrings, codec -> xz, format -> data): 10326,75797

14M mixedDataAvroDataSerializationbzip2,out
16M mixedDataAvroDataSerializationdeflate,out
22M mixedDataAvroDataSerializationnone,out
21M mixedDataAvroDataSerializationsnappy,out
14M mixedDataAvroDataSerializationxz,out
28M mixedDataInput,csv

8,9M onlyLongsAvroDataSerializationbzip2,out
9,3M onlyLongsAvroDataSerializationdeflate,out
11M onlyLongsAvroDataSerializationnone,out
11M onlyLongsAvroDataSerializationsnappy,out
8,9M onlyLongsAvroDataSerializationxz,out
20M onlyLongsInput,csv

37M onlyStringsAvroDataSerializationbzip2,out
41M onlyStringsAvroDataSerializationdeflate,out
73M onlyStringsAvroDataSerializationnone,out
71M onlyStringsAvroDataSerializationsnappy,out
36M onlyStringsAvroDataSerializationxz,out
71M onlyStringsInput,csv
```

## Thrift
```
::Benchmark thrift serialization.serialize using binary protocol - mixed data::
Parameters(compression -> none): 1125.383898 ms
Parameters(compression -> gzip): 3801.1242 ms
Parameters(compression -> snappy): 1361.62375 ms
Parameters(compression -> lz4): 1227.557248 ms

::Benchmark thrift serialization.serialize using compact protocol - mixed data::
Parameters(compression -> none): 989.945646 ms
Parameters(compression -> gzip): 2850.871034 ms
Parameters(compression -> snappy): 1049.290478 ms
Parameters(compression -> lz4): 1034.213286 ms

::Benchmark thrift serialization.serialize using binary protocol - only strings::
Parameters(compression -> none): 1736.157983 ms
Parameters(compression -> gzip): 7854.017803 ms
Parameters(compression -> snappy): 2061.513139 ms
Parameters(compression -> lz4): 2520.092522 ms

::Benchmark thrift serialization.serialize using compact protocol - only strings::
Parameters(compression -> none): 2083.990485 ms
Parameters(compression -> gzip): 9119.918932 ms
Parameters(compression -> snappy): 2562.690542 ms
Parameters(compression -> lz4): 2477.448584 ms

::Benchmark thrift serialization.serialize using binary protocol - only longs::
Parameters(compression -> none): 850.172428 ms
Parameters(compression -> gzip): 3191.32861 ms
Parameters(compression -> snappy): 900.007347 ms
Parameters(compression -> lz4): 891.543165 ms

::Benchmark thrift serialization.serialize using compact protocol - only longs::
Parameters(compression -> none): 855.884258 ms
Parameters(compression -> gzip): 2024.724032 ms
Parameters(compression -> snappy): 929.506194 ms
Parameters(compression -> lz4): 927.156058 ms

::Benchmark thrift deserialization.binary deserialization - mixed data::
Parameters(compression -> none): 213.600809 ms
Parameters(compression -> gzip): 712.985832 ms
Parameters(compression -> snappy): 248.259036 ms
Parameters(compression -> lz4): 227.736503 ms

::Benchmark thrift deserialization.compact deserialization - mixed data::
Parameters(compression -> none): 205.388832 ms
Parameters(compression -> gzip): 770.899966 ms
Parameters(compression -> snappy): 236.259813 ms
Parameters(compression -> lz4): 254.548418 ms

::Benchmark thrift deserialization.binary deserialization - only strings::
Parameters(compression -> none): 459.104972 ms
Parameters(compression -> gzip): 1375.754233 ms
Parameters(compression -> snappy): 604.896944 ms
Parameters(compression -> lz4): 561.856199 ms

::Benchmark thrift deserialization.compact deserialization - only strings::
Parameters(compression -> none): 444.190074 ms
Parameters(compression -> gzip): 1298.840792 ms
Parameters(compression -> snappy): 567.996401 ms
Parameters(compression -> lz4): 622.159067 ms

::Benchmark thrift deserialization.binary deserialization - only longs::
Parameters(compression -> none): 119.657143 ms
Parameters(compression -> gzip): 474.217573 ms
Parameters(compression -> snappy): 123.937332 ms
Parameters(compression -> lz4): 130.392506 ms

::Benchmark thrift deserialization.compact deserialization - only longs::
Parameters(compression -> none): 234.736586 ms
Parameters(compression -> gzip): 623.688881 ms
Parameters(compression -> snappy): 254.769316 ms
Parameters(compression -> lz4): 252.935347 ms


37M mixedDataBinaryThriftSerialization.out
17M mixedDataBinaryThriftSerializationGzip.out
22M mixedDataBinaryThriftSerializationLz4.out
23M mixedDataBinaryThriftSerializationSnappy.out
31M mixedDataCompactThriftSerialization.out
17M mixedDataCompactThriftSerializationGzip.out
21M mixedDataCompactThriftSerializationLz4.out
22M mixedDataCompactThriftSerializationSnappy.out
28M mixedDataInput.csv

16M onlyLongsBinaryThriftSerialization.out
9.7M onlyLongsBinaryThriftSerializationGzip.out
11M onlyLongsBinaryThriftSerializationLz4.out
11M onlyLongsBinaryThriftSerializationSnappy.out
14M onlyLongsCompactThriftSerialization.out
9.4M onlyLongsCompactThriftSerializationGzip.out
11M onlyLongsCompactThriftSerializationLz4.out
11M onlyLongsCompactThriftSerializationSnappy.out
20M onlyLongsInput.csv

98M onlyStringsBinaryThriftSerialization.out
44M onlyStringsBinaryThriftSerializationGzip.out
70M onlyStringsBinaryThriftSerializationLz4.out
74M onlyStringsBinaryThriftSerializationSnappy.out
98M onlyStringsCompactThriftSerialization.out
42M onlyStringsCompactThriftSerializationGzip.out
66M onlyStringsCompactThriftSerializationLz4.out
72M onlyStringsCompactThriftSerializationSnappy.out
71M onlyStringsInput.csv
```

## Protobuf
```
::Benchmark protobuf serialization.serialize - mixed data::
Parameters(compression -> none): 1102.173422 ms
Parameters(compression -> gzip): 3683.866389 ms
Parameters(compression -> snappy): 1120.608083 ms
Parameters(compression -> lz4): 1155.255245 ms

::Benchmark protobuf serialization.serialize - only strings::
Parameters(compression -> none): 1929.046419 ms
Parameters(compression -> gzip): 9241.951999 ms
Parameters(compression -> snappy): 1935.746472 ms
Parameters(compression -> lz4): 2118.653265 ms

::Benchmark protobuf serialization.serialize - only longs::
Parameters(compression -> none): 768.131208 ms
Parameters(compression -> gzip): 2170.330163 ms
Parameters(compression -> snappy): 856.351572 ms
Parameters(compression -> lz4): 815.517398 ms


::Benchmark protobuf deserialization.deserialize - mixed data::
Parameters(compression -> none): 90.701471 ms
Parameters(compression -> gzip): 675.418776 ms
Parameters(compression -> snappy): 111.94735 ms
Parameters(compression -> lz4): 118.566453 ms

::Benchmark protobuf deserialization.deserialize - only strings::
Parameters(compression -> none): 265.139096 ms
Parameters(compression -> gzip): 1285.807188 ms
Parameters(compression -> snappy): 272.894545 ms
Parameters(compression -> lz4): 324.312626 ms

::Benchmark protobuf deserialization.deserialize - only longs::
Parameters(compression -> none): 62.238189 ms
Parameters(compression -> gzip): 498.403267 ms
Parameters(compression -> snappy): 64.990243 ms
Parameters(compression -> lz4): 65.485835 ms

28M mixedDataInput.csv
22M mixedDataProtobufSerialization.out
16M mixedDataProtobufSerializationGzip.out
21M mixedDataProtobufSerializationLz4.out
22M mixedDataProtobufSerializationSnappy.out

20M onlyLongsInput.csv
11M onlyLongsProtobufSerialization.out
9.6M onlyLongsProtobufSerializationGzip.out
11M onlyLongsProtobufSerializationLz4.out
11M onlyLongsProtobufSerializationSnappy.out

71M onlyStringsInput.csv
73M onlyStringsProtobufSerialization.out
42M onlyStringsProtobufSerializationGzip.out
68M onlyStringsProtobufSerializationLz4.out
73M onlyStringsProtobufSerializationSnappy.out

```

## ORC
```
::Benchmark orc serialization.serialize - mixed data::
Parameters(compression -> NONE): 1912.585564 ms
Parameters(compression -> SNAPPY): 2288.6607 ms
Parameters(compression -> ZLIB): 2891.391571 ms
Parameters(compression -> LZO): 2237.013887 ms
Parameters(compression -> LZ4): 1930.257375 ms

::Benchmark orc serialization.serialize - only strings::
Parameters(compression -> NONE): 2344.838182 ms
Parameters(compression -> SNAPPY): 2887.628871 ms
Parameters(compression -> ZLIB): 4633.018129 ms
Parameters(compression -> LZO): 2541.053979 ms
Parameters(compression -> LZ4): 2541.089112 ms

::Benchmark orc serialization.serialize - only longs::
Parameters(compression -> NONE): 936.24197 ms
Parameters(compression -> SNAPPY): 961.510494 ms
Parameters(compression -> ZLIB): 1275.094674 ms
Parameters(compression -> LZO): 1028.821117 ms
Parameters(compression -> LZ4): 1003.875842 ms

::Benchmark orc deserialization.deserialize - mixed data::
Parameters(compression -> NONE): 189.255381 ms
Parameters(compression -> SNAPPY): 256.261948 ms
Parameters(compression -> ZLIB): 340.04471 ms
Parameters(compression -> LZO): 199.372492 ms
Parameters(compression -> LZ4): 206.638675 ms

::Benchmark orc deserialization.deserialize - only strings::
Parameters(compression -> NONE): 316.492181 ms
Parameters(compression -> SNAPPY): 536.2908 ms
Parameters(compression -> ZLIB): 773.823267 ms
Parameters(compression -> LZO): 367.960728 ms
Parameters(compression -> LZ4): 331.145197 ms

::Benchmark orc deserialization.deserialize - only longs::
Parameters(compression -> NONE): 95.842699 ms
Parameters(compression -> SNAPPY): 116.003717 ms
Parameters(compression -> ZLIB): 154.301854 ms
Parameters(compression -> LZO): 105.768183 ms
Parameters(compression -> LZ4): 103.962426 ms


28M mixedDataInput.csv
21M mixedDataOrcSerializationLZ4.orc
21M mixedDataOrcSerializationLZO.orc
21M mixedDataOrcSerializationNONE.orc
20M mixedDataOrcSerializationSNAPPY.orc
14M mixedDataOrcSerializationZLIB.orc

20M onlyLongsInput.csv
9.5M onlyLongsOrcSerializationLZ4.orc
9.5M onlyLongsOrcSerializationLZO.orc
12M onlyLongsOrcSerializationNONE.orc
9.3M onlyLongsOrcSerializationSNAPPY.orc
8.8M onlyLongsOrcSerializationZLIB.orc

71M onlyStringsInput.csv
69M onlyStringsOrcSerializationLZ4.orc
69M onlyStringsOrcSerializationLZO.orc
69M onlyStringsOrcSerializationNONE.orc
65M onlyStringsOrcSerializationSNAPPY.orc
40M onlyStringsOrcSerializationZLIB.orc

```

## Parquet
```
::Benchmark parquet serialization.parquet-avro serialize - mixed data::
Parameters(compression -> UNCOMPRESSED): 10218.814451 ms
Parameters(compression -> SNAPPY): 10442.95336 ms
Parameters(compression -> GZIP): 11533.360107 ms

::Benchmark parquet serialization.parquet-thrift serialize - mixed data::
Parameters(compression -> UNCOMPRESSED): 13831.127764 ms
Parameters(compression -> SNAPPY): 17349.627433 ms
Parameters(compression -> GZIP): 15447.89787 ms

::Benchmark parquet serialization.parquet-avro serialize - only strings::
Parameters(compression -> UNCOMPRESSED): 16779.399392 ms
Parameters(compression -> SNAPPY): 17172.47809 ms
Parameters(compression -> GZIP): 22477.547579 ms

::Benchmark parquet serialization.parquet-thrift serialize - only strings::
Parameters(compression -> UNCOMPRESSED): 17856.227515 ms
Parameters(compression -> SNAPPY): 18600.951512 ms
Parameters(compression -> GZIP): 23818.573715 ms

::Benchmark parquet serialization.parquet-avro serialize - only longs::
Parameters(compression -> UNCOMPRESSED): 7005.872151 ms
Parameters(compression -> SNAPPY): 7121.721859 ms
Parameters(compression -> GZIP): 7614.317721 ms

::Benchmark parquet serialization.parquet-thrift serialize - only longs::
Parameters(compression -> UNCOMPRESSED): 13440.809873 ms
Parameters(compression -> SNAPPY): 13529.675534 ms
Parameters(compression -> GZIP): 17242.291184 ms

::Benchmark parquet deserialization.deserialize-avro - mixed data::
Parameters(compression -> UNCOMPRESSED): 1116.220223 ms
Parameters(compression -> SNAPPY): 1160.43776 ms
Parameters(compression -> GZIP): 1270.950129 ms

::Benchmark parquet deserialization.deserialize-thrift - mixed data::
Parameters(compression -> UNCOMPRESSED): 1306.652127 ms
Parameters(compression -> SNAPPY): 1442.344638 ms
Parameters(compression -> GZIP): 1592.810931 ms

::Benchmark parquet deserialization.deserialize-avro - only strings::
Parameters(compression -> UNCOMPRESSED): 1631.461872 ms
Parameters(compression -> SNAPPY): 1691.013253 ms
Parameters(compression -> GZIP): 2136.751124 ms

::Benchmark parquet deserialization.deserialize-thrift - only strings::
Parameters(compression -> UNCOMPRESSED): 1319.10091 ms
Parameters(compression -> SNAPPY): 1435.536838 ms
Parameters(compression -> GZIP): 1868.112762 ms

::Benchmark parquet deserialization.deserialize-avro - only longs::
Parameters(compression -> UNCOMPRESSED): 767.6748 ms
Parameters(compression -> SNAPPY): 815.833926 ms
Parameters(compression -> GZIP): 855.741205 ms

::Benchmark parquet deserialization.deserialize-thrift - only longs::
Parameters(compression -> UNCOMPRESSED): 937.663629 ms
Parameters(compression -> SNAPPY): 1111.400913 ms
Parameters(compression -> GZIP): 1284.935349 ms


28M mixedDataInput.csv
13M mixedDataParquetAvroSerializationGZIP.out
19M mixedDataParquetAvroSerializationSNAPPY.out
21M mixedDataParquetAvroSerializationUNCOMPRESSED.out
14M mixedDataParquetThriftSerializationGZIP.out
20M mixedDataParquetThriftSerializationSNAPPY.out
22M mixedDataParquetThriftSerializationUNCOMPRESSED.out

20M onlyLongsInput.csv
7.7M onlyLongsParquetAvroSerializationGZIP.out
7.7M onlyLongsParquetAvroSerializationSNAPPY.out
7.9M onlyLongsParquetAvroSerializationUNCOMPRESSED.out
9.7M onlyLongsParquetThriftSerializationGZIP.out
11M onlyLongsParquetThriftSerializationSNAPPY.out
11M onlyLongsParquetThriftSerializationUNCOMPRESSED.out

71M onlyStringsInput.csv
42M onlyStringsParquetAvroSerializationGZIP.out
69M onlyStringsParquetAvroSerializationSNAPPY.out
76M onlyStringsParquetAvroSerializationUNCOMPRESSED.out
42M onlyStringsParquetThriftSerializationGZIP.out
69M onlyStringsParquetThriftSerializationSNAPPY.out
76M onlyStringsParquetThriftSerializationUNCOMPRESSED.out

```

## Msgpack
```
::Benchmark msgpack serialization.serialize - mixed data::
Parameters(compression -> none): 1020.163951 ms
Parameters(compression -> gzip): 3114.796838 ms
Parameters(compression -> snappy): 1065.383296 ms
Parameters(compression -> lz4): 1117.194498 ms

::Benchmark msgpack serialization.serialize - only strings::
Parameters(compression -> none): 1897.480828 ms
Parameters(compression -> gzip): 8753.47287 ms
Parameters(compression -> snappy): 1903.218432 ms
Parameters(compression -> lz4): 2228.870718 ms

::Benchmark msgpack serialization.serialize - only longs::
Parameters(compression -> none): 746.000071 ms
Parameters(compression -> gzip): 1709.087779 ms
Parameters(compression -> snappy): 808.893869 ms
Parameters(compression -> lz4): 811.050544 ms


::Benchmark msgpack deserialization.deserialize - mixed data::
Parameters(compression -> none): 299.681239 ms
Parameters(compression -> gzip): 526.834939 ms
Parameters(compression -> snappy): 306.481637 ms
Parameters(compression -> lz4): 322.101246 ms

::Benchmark msgpack deserialization.deserialize - only strings::
Parameters(compression -> none): 417.600146 ms
Parameters(compression -> gzip): 1083.538239 ms
Parameters(compression -> snappy): 481.48993 ms
Parameters(compression -> lz4): 544.200988 ms

::Benchmark msgpack deserialization.deserialize - only longs::
Parameters(compression -> none): 83.994804 ms
Parameters(compression -> gzip): 159.456138 ms
Parameters(compression -> snappy): 87.196718 ms
Parameters(compression -> lz4): 87.100244 ms

28M  mixedDataInput.csv
21M  mixedDataMsgpackSerialization.out
16M  mixedDataMsgpackSerializationGzip.out
20M  mixedDataMsgpackSerializationLz4.out
21M  mixedDataMsgpackSerializationSnappy.out

20M  onlyLongsInput.csv
9.6M onlyLongsMsgpackSerialization.out
9.0M onlyLongsMsgpackSerializationGzip.out
9.6M onlyLongsMsgpackSerializationLz4.out
9.6M onlyLongsMsgpackSerializationSnappy.out

71M  onlyStringsInput.csv
73M  onlyStringsMsgpackSerialization.out
41M  onlyStringsMsgpackSerializationGzip.out
66M  onlyStringsMsgpackSerializationLz4.out
73M  onlyStringsMsgpackSerializationSnappy.out

```
