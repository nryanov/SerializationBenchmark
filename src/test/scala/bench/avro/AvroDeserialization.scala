package bench.avro

import java.io.{File, FileInputStream}

import bench.Settings
import com.sksamuel.avro4s.{AvroInputStream, AvroSchema}
import org.apache.avro.Schema
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project._


object AvroDeserialization extends Bench.LocalTime {
  @volatile
  var data: Data = _

  val streams = Map(
    "data" -> ((dataType: String, codec: String, schema: Schema) => AvroInputStream.data[Data].from(new FileInputStream(new File(s"${dataType}AvroDataSerialization$codec.out"))).build(schema)),
    "binary" -> ((dataType: String, codec: String, schema: Schema) => AvroInputStream.binary[Data].from(new FileInputStream(new File(s"${dataType}AvroBinarySerialization$codec.out"))).build(schema))
  )

  val schemas = Map(
    "mixedData" -> AvroSchema[MixedData],
    "onlyStrings" -> AvroSchema[OnlyStrings],
    "onlyLongs" -> AvroSchema[OnlyLongs]
  )

  val codec = Gen.enumeration("codec")(
    "none",
    "snappy",
    "deflate",
    "bzip2",
    "xz"
  )

  val format = Gen.enumeration("format")(
    "data",
//    "binary"
  )
  val dataType = Gen.enumeration("data type")("onlyLongs", "mixedData", "onlyStrings")

  performance of "deserialization avro" in {
    measure method "deserialize - data" in {
      using(Gen.crossProduct(dataType, codec, format)) config (
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val schema = schemas(gen._1)
        val in = streams(gen._3)(gen._1, gen._2, schema)
        var i = 0
        val iter = in.iterator

        while(iter.nonEmpty) {
          data = iter.next()
          i += 1
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }
}
