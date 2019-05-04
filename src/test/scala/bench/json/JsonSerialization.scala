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
import java.util.zip.GZIPOutputStream

import bench.Settings
import project.{Data, DataUtils}

object JsonSerialization extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")
  implicit val noTypeHintsFormat = Serialization.formats(NoTypeHints)

  performance of "json serialization" in {
    measure method "serialize" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("jsonSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            val bytes = mapper.writeValueAsBytes(write[Data](data))
            val length = bytes.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(bytes)

            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }

    measure method "serialize using snappy compression - snappy output stream" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new SnappyOutputStream(new FileOutputStream(new File("jsonSerializationSnappyCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            val bytes = mapper.writeValueAsBytes(write[Data](data))
            val length = bytes.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(bytes)

            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }

    measure method "serialize using gzip compression" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new GZIPOutputStream(new FileOutputStream(new File("jsonSerializationGzipCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            val bytes = mapper.writeValueAsBytes(write[Data](data))
            val length = bytes.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(bytes)

            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }
  }
}
