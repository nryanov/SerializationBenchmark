# Serialized output file sizes (2026)

Sizes are **bytes on disk** for serialized output (100k records), from `ls -l`.
## Results by format

### Java

|              | none       | gzip       | snappy     | lz4        | xz         |
|--------------|------------|------------|------------|------------|------------|
| mixedData    | 37,951,093 | 17,930,372 | 25,773,738 | 25,605,777 | 14,933,072 |
| onlyLongs    | 25,610,320 | 10,207,717 | 12,839,417 | 12,447,877 | 9,403,032  |
| onlyStrings  | 90,606,625 | 44,426,569 | 73,441,510 | 69,936,284 | 37,960,440 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 900, "height": 420}}}}%%
xychart-beta
    title "Java — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "gzip · onlyStrings", "gzip · onlyLongs", "gzip · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "lz4 · onlyStrings", "lz4 · onlyLongs", "lz4 · mixedData", "xz · onlyStrings", "xz · onlyLongs", "xz · mixedData"]
    y-axis "bytes" 0 --> 100000000
    bar [90606625, 25610320, 37951093, 44426569, 10207717, 17930372, 73441510, 12839417, 25773738, 69936284, 12447877, 25605777, 37960440, 9403032, 14933072]
```

### Json

|              | none       | gzip       | snappy     | lz4        | xz         |
|--------------|------------|------------|------------|------------|------------|
| mixedData    | 54,364,018 | 22,472,516 | 52,396,341 | 36,181,902 | 18,685,252 |
| onlyLongs    | 38,173,122 | 13,959,884 | 36,099,183 | 23,733,303 | 12,915,648 |
| onlyStrings  | 30,400,002 | 57,259,186 | 15,544,549 | 90,992,389 | 44,949,704 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 900, "height": 420}}}}%%
xychart-beta
    title "Json — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "gzip · onlyStrings", "gzip · onlyLongs", "gzip · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "lz4 · onlyStrings", "lz4 · onlyLongs", "lz4 · mixedData", "xz · onlyStrings", "xz · onlyLongs", "xz · mixedData"]
    y-axis "bytes" 0 --> 100000000
    bar [30400002, 38173122, 54364018, 57259186, 13959884, 22472516, 15544549, 36099183, 52396341, 90992389, 23733303, 36181902, 44949704, 12915648, 18685252]
```

### Avro

|              | none       | deflate    | snappy     | xz         | bzip2      | zstd       |
|--------------|------------|------------|------------|------------|------------|------------|
| mixedData    | 22,723,794 | 16,694,512 | 22,370,461 | 15,005,240 | 14,728,978 | 16,588,948 |
| onlyLongs    | 11,602,655 | 9,823,440  | 11,398,717 | 9,299,892  | 9,447,654  | 9,855,216  |
| onlyStrings  | 76,127,354 | 43,457,387 | 75,005,139 | 37,599,808 | 38,821,030 | 40,686,773 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 1056, "height": 420}}}}%%
xychart-beta
    title "Avro — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "deflate · onlyStrings", "deflate · onlyLongs", "deflate · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "xz · onlyStrings", "xz · onlyLongs", "xz · mixedData", "bzip2 · onlyStrings", "bzip2 · onlyLongs", "bzip2 · mixedData", "zstd · onlyStrings", "zstd · onlyLongs", "zstd · mixedData"]
    y-axis "bytes" 0 --> 80000000
    bar [76127354, 11602655, 22723794, 43457387, 9823440, 16694512, 75005139, 11398717, 22370461, 37599808, 9299892, 15005240, 38821030, 9447654, 14728978, 40686773, 9855216, 16588948]
```

### Thrift (binary protocol)

|              | none        | gzip       | snappy     | lz4        | xz         |
|--------------|-------------|------------|------------|------------|------------|
| mixedData    | 38,673,152  | 17,677,927 | 23,759,227 | 22,880,908 | 14,869,400 |
| onlyLongs    | 16,307,456  | 10,196,439 | 11,585,112 | 11,517,779 | 8,933,784  |
| onlyStrings  | 102,800,000 | 46,339,274 | 77,798,386 | 72,930,488 | 38,288,884 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 900, "height": 420}}}}%%
xychart-beta
    title "Thrift (binary protocol) — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "gzip · onlyStrings", "gzip · onlyLongs", "gzip · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "lz4 · onlyStrings", "lz4 · onlyLongs", "lz4 · mixedData", "xz · onlyStrings", "xz · onlyLongs", "xz · mixedData"]
    y-axis "bytes" 0 --> 200000000
    bar [102800000, 16307456, 38673152, 46339274, 10196439, 17677927, 77798386, 11585112, 23759227, 72930488, 11517779, 22880908, 38288884, 8933784, 14869400]
```

### Thrift (compact protocol)

|              | none        | gzip       | snappy     | lz4        | xz         |
|--------------|-------------|------------|------------|------------|------------|
| mixedData    | 32,519,040  | 17,356,376 | 22,782,492 | 22,070,581 | 14,805,864 |
| onlyLongs    | 14,909,184  | 9,900,293  | 11,341,566 | 11,213,188 | 9,303,548  |
| onlyStrings  | 102,800,000 | 44,132,682 | 75,268,129 | 69,349,947 | 37,160,328 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 900, "height": 420}}}}%%
xychart-beta
    title "Thrift (compact protocol) — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "gzip · onlyStrings", "gzip · onlyLongs", "gzip · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "lz4 · onlyStrings", "lz4 · onlyLongs", "lz4 · mixedData", "xz · onlyStrings", "xz · onlyLongs", "xz · mixedData"]
    y-axis "bytes" 0 --> 200000000
    bar [102800000, 14909184, 32519040, 44132682, 9900293, 17356376, 75268129, 11341566, 22782492, 69349947, 11213188, 22070581, 37160328, 9303548, 14805864]
```

### Protobuf

|              | none       | gzip       | snappy     | lz4        | xz         |
|--------------|------------|------------|------------|------------|------------|
| mixedData    | 22,913,304 | 16,985,043 | 22,874,142 | 21,904,990 | 14,656,644 |
| onlyLongs    | 11,145,897 | 10,064,294 | 11,149,808 | 11,141,548 | 9,629,540  |
| onlyStrings  | 76,900,000 | 44,451,702 | 76,567,456 | 71,012,666 | 37,794,744 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 900, "height": 420}}}}%%
xychart-beta
    title "Protobuf — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "gzip · onlyStrings", "gzip · onlyLongs", "gzip · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "lz4 · onlyStrings", "lz4 · onlyLongs", "lz4 · mixedData", "xz · onlyStrings", "xz · onlyLongs", "xz · mixedData"]
    y-axis "bytes" 0 --> 90000000
    bar [76900000, 11145897, 22913304, 44451702, 10064294, 16985043, 76567456, 11149808, 22874142, 71012666, 11141548, 21904990, 37794744, 9629540, 14656644]
```

### ORC

|              | none       | snappy     | zlib       | lz0        | lz4        | zstd       | brotli     |
|--------------|------------|------------|------------|------------|------------|------------|------------|
| mixedData    | 22,231,827 | 20,573,373 | 14,799,829 | 21,534,931 | 21,486,356 | 14,509,311 | 13,809,149 |
| onlyLongs    | 12,514,162 | 9,705,197  | 9,259,784  | 10,004,456 | 9,967,321  | 9,045,890  | 9,375,716  |
| onlyStrings  | 72,044,901 | 68,512,618 | 42,072,546 | 72,025,871 | 71,905,093 | 40,952,617 | 36,939,624 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 1212, "height": 420}}}}%%
xychart-beta
    title "ORC — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "zlib · onlyStrings", "zlib · onlyLongs", "zlib · mixedData", "lz0 · onlyStrings", "lz0 · onlyLongs", "lz0 · mixedData", "lz4 · onlyStrings", "lz4 · onlyLongs", "lz4 · mixedData", "zstd · onlyStrings", "zstd · onlyLongs", "zstd · mixedData", "brotli · onlyStrings", "brotli · onlyLongs", "brotli · mixedData"]
    y-axis "bytes" 0 --> 80000000
    bar [72044901, 12514162, 22231827, 68512618, 9705197, 20573373, 42072546, 9259784, 14799829, 72025871, 10004456, 21534931, 71905093, 9967321, 21486356, 40952617, 9045890, 14509311, 36939624, 9375716, 13809149]
```

### Parquet (parquet-avro)

|              | none       | snappy     | gzip       | lz4        | zstd       |
|--------------|------------|------------|------------|------------|------------|
| mixedData    | 21,595,786 | 20,103,928 | 14,143,560 | 20,845,124 | 13,659,354 |
| onlyLongs    | 8,267,072  | 8,247,028  | 8,223,377  | 8,270,791  | 8,268,275  |
| onlyStrings  | 80,018,707 | 72,219,127 | 43,747,037 | 76,164,430 | 41,104,998 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 900, "height": 420}}}}%%
xychart-beta
    title "Parquet (parquet-avro) — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "gzip · onlyStrings", "gzip · onlyLongs", "gzip · mixedData", "lz4 · onlyStrings", "lz4 · onlyLongs", "lz4 · mixedData", "zstd · onlyStrings", "zstd · onlyLongs", "zstd · mixedData"]
    y-axis "bytes" 0 --> 90000000
    bar [80018707, 8267072, 21595786, 72219127, 8247028, 20103928, 43747037, 8223377, 14143560, 76164430, 8270791, 20845124, 41104998, 8268275, 13659354]
```

### Parquet (parquet-thrift)

|              | none       | snappy     | gzip       | lz4        | zstd       |
|--------------|------------|------------|------------|------------|------------|
| mixedData    | 23,483,233 | 21,502,596 | 15,085,304 | 22,265,594 | 14,674,280 |
| onlyLongs    | 11,813,874 | 11,168,920 | 10,358,921 | 11,278,716 | 10,531,601 |
| onlyStrings  | 80,021,698 | 72,222,118 | 43,750,028 | 76,167,421 | 41,107,989 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 900, "height": 420}}}}%%
xychart-beta
    title "Parquet (parquet-thrift) — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "gzip · onlyStrings", "gzip · onlyLongs", "gzip · mixedData", "lz4 · onlyStrings", "lz4 · onlyLongs", "lz4 · mixedData", "zstd · onlyStrings", "zstd · onlyLongs", "zstd · mixedData"]
    y-axis "bytes" 0 --> 90000000
    bar [80021698, 11813874, 23483233, 72222118, 11168920, 21502596, 43750028, 10358921, 15085304, 76167421, 11278716, 22265594, 41107989, 10531601, 14674280]
```

### Msgpack

|              | none       | gzip       | snappy     | lz4        | xz         |
|--------------|------------|------------|------------|------------|------------|
| mixedData    | 22,312,244 | 16,380,733 | 22,337,073 | 21,331,339 | 14,641,100 |
| onlyLongs    | 10,105,770 | 9,407,456  | 10,106,959 | 10,032,253 | 9,300,864  |
| onlyStrings  | 76,300,000 | 42,940,659 | 76,324,268 | 68,733,101 | 37,335,128 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 900, "height": 420}}}}%%
xychart-beta
    title "Msgpack — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "gzip · onlyStrings", "gzip · onlyLongs", "gzip · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "lz4 · onlyStrings", "lz4 · onlyLongs", "lz4 · mixedData", "xz · onlyStrings", "xz · onlyLongs", "xz · mixedData"]
    y-axis "bytes" 0 --> 90000000
    bar [76300000, 10105770, 22312244, 42940659, 9407456, 16380733, 76324268, 10106959, 22337073, 68733101, 10032253, 21331339, 37335128, 9300864, 14641100]
```

### CBOR

|              | none       | gzip       | snappy     | lz4        | xz         |
|--------------|------------|------------|------------|------------|------------|
| mixedData    | 22,489,054 | 16,421,929 | 22,459,267 | 21,404,592 | 14,714,196 |
| onlyLongs    | 10,400,072 | 9,498,079  | 10,271,873 | 10,244,659 | 9,121,880  |
| onlyStrings  | 76,400,000 | 42,966,763 | 75,562,031 | 68,721,503 | 37,280,848 |

```mermaid
%%{init: {"config": {"xyChart": {"width": 900, "height": 420}}}}%%
xychart-beta
    title "CBOR — file size (bytes)"
    x-axis ["none · onlyStrings", "none · onlyLongs", "none · mixedData", "gzip · onlyStrings", "gzip · onlyLongs", "gzip · mixedData", "snappy · onlyStrings", "snappy · onlyLongs", "snappy · mixedData", "lz4 · onlyStrings", "lz4 · onlyLongs", "lz4 · mixedData", "xz · onlyStrings", "xz · onlyLongs", "xz · mixedData"]
    y-axis "bytes" 0 --> 90000000
    bar [76400000, 10400072, 22489054, 42966763, 9498079, 16421929, 75562031, 10271873, 22459267, 68721503, 10244659, 21404592, 37280848, 9121880, 14714196]
```

