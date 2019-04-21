name := "SerializationBenchmark"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += "Confluent" at "http://packages.confluent.io/maven/"

val kantanVersion = "0.5.0"
val avro4sVersion = "3.6.5"
val json4sVersion = "2.0.4"
val confluent = "5.2.1"
val kafkaVersion = "2.2.0"
val scroogeVersion = "19.4.0"
val scalameterVersion = "0.17"
val finagleThriftVersion = "19.4.0"
val libthriftVersion = "0.12.0"
val orcVersion = "1.5.5"

libraryDependencies ++= Seq(
  "com.nrinaudo" %% "kantan.csv" % kantanVersion,
  "com.nrinaudo" %% "kantan.csv-generic" % kantanVersion,
  "org.json4s" %% "json4s-jackson" % avro4sVersion,
  "com.sksamuel.avro4s" %% "avro4s-core" % json4sVersion,
  "io.confluent" % "kafka-avro-serializer" % confluent,
  "org.apache.kafka" %% "kafka" % kafkaVersion,
  "org.apache.thrift" % "libthrift" % libthriftVersion,
  "com.twitter" %% "scrooge-core" % scroogeVersion exclude("com.twitter", "libthrift"),
  "com.twitter" %% "finagle-thrift" % finagleThriftVersion exclude("com.twitter", "libthrift"),
  "org.apache.orc" % "orc-core" % orcVersion,
  "com.storm-enroute" %% "scalameter" % scalameterVersion % Test
)