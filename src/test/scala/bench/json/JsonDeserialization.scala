package bench.json

import java.io._
import java.nio.ByteBuffer

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.JsonMethods.mapper
import org.json4s.jackson.Serialization._
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy._
import java.util.zip.GZIPInputStream

import project.Data
import bench.Settings

object JsonDeserialization extends Bench.LocalTime {
  implicit val noTypeHintsFormat = Serialization.formats(NoTypeHints)

  val jsonInput = Gen.single("input")("jsonSerialization.out")
  val jsonInputSnappy = Gen.single("input")("jsonSerializationSnappyCompression.out")
  val jsonInputGzip = Gen.single("input")("jsonSerializationGzipCompression.out")

  performance of "json deserialization" in {
    measure method "deserialize" in {
      using(jsonInput) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new BufferedInputStream(new FileInputStream(new File(file)))
        var i = 0

        while (in.available() > 0) {
          val lengthBytes = new Array[Byte](4)
          in.read(lengthBytes)
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val data = new Array[Byte](length)
          in.read(data)
          val obj = read[Data](mapper.readTree(data).asText())
          i += 1
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize - snappy" in {
      using(jsonInputSnappy) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new SnappyInputStream(new FileInputStream(new File(file)))
        var i = 0

        while (in.available() > 0) {
          val lengthBytes = new Array[Byte](4)
          in.read(lengthBytes)
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val data = new Array[Byte](length)
          in.read(data)
          val obj = read[Data](mapper.readTree(data).asText())

          i += 1
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize - gzip" in {
      using(jsonInputGzip) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new GZIPInputStream(new FileInputStream(new File(file)))
        var i = 0

        while (in.available() > 0) {
          val lengthBytes = new Array[Byte](4)
          in.read(lengthBytes)
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val data = new Array[Byte](length)
          in.read(data)
          val obj = read[Data](mapper.readTree(data).asText())

          i += 1
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }
}
