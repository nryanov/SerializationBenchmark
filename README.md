# SerializationBenchmark

## Results
- [2019](./results/2019/index.md)
- [2026](./results/2026/index.md)

## Commands
- To customize benchmark directory use `env: BENCH_DATA_DIR`. Default: current directory
- To customize count of records use `env BENCH_RECORDS_COUNT`. Default: 100k

`sbt clean compile` - will generate Thrift and Protobuf classes

`sbt generateDataSets` - generate data sets  

`sbt avroSerializingBench` - Run avro serialization  benchmark  
`sbt avroDeserializingBench` - Run avro deserialization  benchmark  
`sbt avroBench` - Run avro benchmark  

`sbt cborSerializingBench` - Run cbor serialization  benchmark  
`sbt cborDeserializingBench` - Run cbor deserialization  benchmark  
`sbt cborBench` - Run cbor benchmark  

`sbt javaSerializingBench` - Run java serialization benchmark  
`sbt javaDeserializingBench` - Run java deserialization benchmark  
`sbt javaBench` - Run java benchmark  

`sbt jsonSerializingBench` - Run json serialization benchmark  
`sbt jsonDeserializingBench` - Run json deserialization benchmark  
`sbt jsonBench` - Run json benchmark  

`sbt msgpackSerializingBench` - Run msgpack serialization benchmark  
`sbt msgpackDeserializingBench` - Run msgpack deserialization benchmark  
`sbt msgpackBench` - Run msgpack benchmark  

`sbt orcSerializingBench` - Run orc serialization benchmark  
`sbt orcDeserializingBench` - Run orc deserialization benchmark  
`sbt orcBench` - Run orc benchmark  

`sbt parquetSerializingBench` - Run parquet serialization benchmark  
`sbt parquetDeserializingBench` - Run parquet deserialization benchmark  
`sbt parquetBench` - Run parquet benchmark  

`sbt protobufSerializingBench` - Run protobuf serialization benchmark  
`sbt protobufDeserializingBench` - Run protobuf deserialization benchmark  
`sbt protobufBench` - Run protobuf benchmark  

`sbt thriftSerializingBench` - Run thrift serialization benchmark  
`sbt thriftDeserializingBench` - Run thrift deserialization benchmark  
`sbt thriftBench` - Run thrift benchmark  
