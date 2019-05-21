package bench.avro

import java.io.File

import bench.Settings
import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.file.DataFileReader
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project._


object AvroLowLevelApiDeserialization extends Bench.LocalTime {
  @volatile
  var data: Data = _

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

  val dataType = Gen.enumeration("data type")("onlyLongs", "mixedData", "onlyStrings")

  performance of "avro deserialization" in {
    measure method "deserialize - low-level" in {
      using(Gen.crossProduct(dataType, codec)) config (
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { s =>
        val schema = schemas(s._1)
        val in = new DataFileReader[GenericRecord](new File(s"${s._1}LowLevelAvroSerialization${s._2}.out"), new GenericDatumReader[GenericRecord](schema))
        var i = 0

        val iter = in.iterator

        while(iter.hasNext) {
          val next = iter.next()
          i += 1
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }
}
