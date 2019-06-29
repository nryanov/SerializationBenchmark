name := "SerializationBenchmark"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions := List("-encoding", "utf8")

Compile / fork := true

Compile / javaOptions += "-Dfile.encoding=UTF-8"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Confluent" at "http://packages.confluent.io/maven/"
// to fix problem with hadoop codec dependency import
resolvers += "Twitter Maven Repo" at "https://maven.twttr.com"

val hdfsVersion = "2.7.3"
val kantanVersion = "0.5.0"
val json4sVersion = "3.6.5"
val avro4sVersion = "2.0.4"
val confluent = "5.2.1"
val kafkaVersion = "2.2.0"
val scroogeVersion = "19.4.0"
val scalameterVersion = "0.17"
val finagleThriftVersion = "19.4.0"
val libthriftVersion = "0.12.0"
val orcVersion = "1.5.5"
val parquetVersion = "1.10.1"
val snappyVersion = "1.1.7.3"
val lz4Version = "1.5.1"
val apacheCommonCompressVersion = "1.18"
val msgpackVersion = "0.8.16"
val borerVersion = "0.9.0"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-client" % hdfsVersion,
  "com.nrinaudo" %% "kantan.csv" % kantanVersion,
  "com.nrinaudo" %% "kantan.csv-generic" % kantanVersion,
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "com.sksamuel.avro4s" %% "avro4s-core" % avro4sVersion,
  "io.confluent" % "kafka-avro-serializer" % confluent,
  "org.apache.kafka" %% "kafka" % kafkaVersion,
  "org.apache.thrift" % "libthrift" % libthriftVersion,
  "com.twitter" %% "scrooge-core" % scroogeVersion exclude("com.twitter", "libthrift"),
  "com.twitter" %% "finagle-thrift" % finagleThriftVersion exclude("com.twitter", "libthrift"),
  "org.apache.orc" % "orc-core" % orcVersion,
  "org.apache.parquet" % "parquet-avro" % parquetVersion,
  "org.apache.parquet" % "parquet-thrift" % parquetVersion,
  "org.apache.parquet" % "parquet-protobuf" % parquetVersion,
  "org.apache.parquet" % "parquet-hadoop" % parquetVersion,
  "org.msgpack" % "msgpack-core" % msgpackVersion,
  "io.bullet" %% "borer-core" % borerVersion,
  "io.bullet" %% "borer-derivation" % borerVersion,

  "org.xerial.snappy" % "snappy-java" % snappyVersion,
  "org.lz4" % "lz4-java" % lz4Version,
  "org.apache.commons" % "commons-compress" % apacheCommonCompressVersion,

  "com.storm-enroute" %% "scalameter" % scalameterVersion
)

// to generate java and scala versions
scroogeLanguages in Compile := Seq("java", "scala")

// for protobuf
PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)
PB.deleteTargetDirectory := false

(Compile / compile) := ((Compile / compile) dependsOn (Compile / scroogeGen)).value

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

parallelExecution in Test := false

val generateDataSets = inputKey[Unit]("Generate data sets")
generateDataSets := (runMain in Test).fullInput(" bench.InitialDataGenerator").evaluated

val avroSerializingBench = inputKey[Unit]("Run avro serialization  benchmark")
val avroDeserializingBench = inputKey[Unit]("Run avro deserialization  benchmark")
val avroBench = inputKey[Unit]("Run avro benchmark")

avroSerializingBench := (testOnly in Test).fullInput(" bench.avro.AvroSerialization").evaluated
avroDeserializingBench := (testOnly in Test).fullInput(" bench.avro.AvroDeserialization").evaluated
avroBench := {
  val serialization = avroSerializingBench
  val deserialization = avroDeserializingBench
}

val cborSerializingBench = inputKey[Unit]("Run cbor serialization  benchmark")
val cborDeserializingBench = inputKey[Unit]("Run cbor deserialization  benchmark")
val cborBench = inputKey[Unit]("Run cbor benchmark")

cborSerializingBench := (testOnly in Test).fullInput(" bench.cbor.CborManualSerialization").evaluated
cborDeserializingBench := (testOnly in Test).fullInput(" bench.cbor.CborManualDeserialization").evaluated
cborBench := {
  val serialization = cborSerializingBench
  val deserialization = cborDeserializingBench
}

val javaSerializingBench = inputKey[Unit]("Run java serialization benchmark")
val javaDeserializingBench = inputKey[Unit]("Run java deserialization benchmark")
val javaBench = inputKey[Unit]("Run java benchmark")

javaSerializingBench := (testOnly in Test).fullInput(" bench.java.JavaSerialization").evaluated
javaDeserializingBench := (testOnly in Test).fullInput(" bench.java.JavaDeserialization").evaluated
javaBench := {
  val serialization = javaSerializingBench
  val deserialization = javaDeserializingBench
}

val jsonSerializingBench = inputKey[Unit]("Run json serialization benchmark")
val jsonDeserializingBench = inputKey[Unit]("Run json deserialization benchmark")
val jsonBench = inputKey[Unit]("Run json benchmark")

jsonSerializingBench := (testOnly in Test).fullInput(" bench.json.JsonSerialization").evaluated
jsonDeserializingBench := (testOnly in Test).fullInput(" bench.json.JsonDeserialization").evaluated
jsonBench := {
  val serialization = jsonSerializingBench
  val deserialization = jsonDeserializingBench
}

val msgpackSerializingBench = inputKey[Unit]("Run msgpack serialization benchmark")
val msgpackDeserializingBench = inputKey[Unit]("Run msgpack deserialization benchmark")
val msgpackBench = inputKey[Unit]("Run msgpack benchmark")

msgpackSerializingBench := (testOnly in Test).fullInput(" bench.msgpack.MsgpackSerialization").evaluated
msgpackDeserializingBench := (testOnly in Test).fullInput(" bench.msgpack.MsgpackDeserialization").evaluated
msgpackBench := {
  val serialization = msgpackSerializingBench
  val deserialization = msgpackDeserializingBench
}

val orcSerializingBench = inputKey[Unit]("Run orc serialization benchmark")
val orcDeserializingBench = inputKey[Unit]("Run orc deserialization benchmark")
val orcBench = inputKey[Unit]("Run orc benchmark")

orcSerializingBench := (testOnly in Test).fullInput(" bench.orc.ORCSerialization").evaluated
orcDeserializingBench := (testOnly in Test).fullInput(" bench.orc.ORCDeserialization").evaluated
orcBench := {
  val serialization = orcSerializingBench
  val deserialization = orcDeserializingBench
}

val parquetSerializingBench = inputKey[Unit]("Run parquet serialization benchmark")
val parquetDeserializingBench = inputKey[Unit]("Run parquet deserialization benchmark")
val parquetBench = inputKey[Unit]("Run parquet benchmark")

parquetSerializingBench := (testOnly in Test).fullInput(" bench.parquet.ParquetSerialization").evaluated
parquetDeserializingBench := (testOnly in Test).fullInput(" bench.parquet.ParquetDeserialization").evaluated
parquetBench := {
  val serialization = parquetSerializingBench
  val deserialization = parquetDeserializingBench
}

val protobufSerializingBench = inputKey[Unit]("Run protobuf serialization benchmark")
val protobufDeserializingBench = inputKey[Unit]("Run protobuf deserialization benchmark")
val protobufBench = inputKey[Unit]("Run protobuf benchmark")

protobufSerializingBench := (testOnly in Test).fullInput(" bench.protobuf.ProtobufSerialization").evaluated
protobufDeserializingBench := (testOnly in Test).fullInput(" bench.protobuf.ProtobufDeserialization").evaluated
protobufBench := {
  val serialization = protobufSerializingBench
  val deserialization = protobufDeserializingBench
}

val thriftSerializingBench = inputKey[Unit]("Run thrift serialization benchmark")
val thriftDeserializingBench = inputKey[Unit]("Run thrift deserialization benchmark")
val thriftBench = inputKey[Unit]("Run thrift benchmark")

thriftSerializingBench := (testOnly in Test).fullInput(" bench.thrift.ThriftSerialization").evaluated
thriftDeserializingBench := (testOnly in Test).fullInput(" bench.thrift.ThriftDeserialization").evaluated
thriftBench := {
  val serialization = thriftSerializingBench
  val deserialization = thriftDeserializingBench
}
