package bench.avro

import java.io.File

import bench.Settings
import com.sksamuel.avro4s.{AvroInputStream, AvroSchema}
import org.apache.avro.file.DataFileReader
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.Data

object AvroDeserialization extends Bench.LocalTime {
  val dataInput = Gen.single("input")("avroDataSerialization")
  val binaryInput = Gen.single("input")("avroBinarySerialization")
  val lowLevelInput = Gen.single("input")("lowLevelAvroSerialization")

  val codec = Gen.enumeration("codec")(
    "none",
    "snappy",
    "deflate",
    "bzip2",
    "xz"
  )

  val schema = AvroSchema[Data]

  performance of "deserialization avro" in {
    measure method "deserialize - data" in {
      using(Gen.crossProduct(dataInput, codec)) config (
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { s =>
        val in = AvroInputStream.data[Data].from(new File(s"${s._1}${s._2}.out")).build(schema)
        var i = 0
        val iter = in.iterator

        while(iter.nonEmpty) {
          val next = iter.next()
          i += 1
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize - binary" in {
      using(Gen.crossProduct(binaryInput, codec)) config (
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { s =>
        val in = AvroInputStream.binary[Data].from(new File(s"${s._1}${s._2}.out")).build(schema)
        var i = 0
        val iter = in.iterator

        while(iter.nonEmpty) {
          val next = iter.next()
          i += 1
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize - low-level" in {
      using(Gen.crossProduct(lowLevelInput, codec)) config (
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { s =>
        val in = new DataFileReader[GenericRecord](new File(s"${s._1}${s._2}.out"), new GenericDatumReader[GenericRecord](schema))
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
