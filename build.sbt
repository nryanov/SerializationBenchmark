name := "SerializationBenchmark"

version := "0.1"

scalaVersion := "2.12.8"

javaOptions += "-Dfile.encoding=UTF-8"

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

  "com.storm-enroute" %% "scalameter" % scalameterVersion % Test
)

// to generate java and scala versions
scroogeLanguages in Compile := Seq("java", "scala")

// for protobuf
PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

(Compile / compile) := ((Compile / compile) dependsOn (Compile / scroogeGen)).value

parallelExecution in Test := false