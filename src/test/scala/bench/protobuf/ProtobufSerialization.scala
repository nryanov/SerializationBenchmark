package bench.protobuf

import java.io._
import java.nio.ByteBuffer

import bench.Settings
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyOutputStream
import project.{DataUtils, MixedData}
import project.Implicits._

object ProtobufSerialization extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")

  performance of "protobuf serialization" in {
    measure method "serialize" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("protobufSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv[MixedData](file)
        in.foreach(rs => {
          rs.foreach(data => {
            val o = DataUtils.mixedDataToScalaProtobuf(data).toByteArray
            val length = o.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(o)
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

    measure method "serialize - snappy compression" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new SnappyOutputStream(new FileOutputStream(new File("protobufSerializationSnappy.out")))
        var i = 0

        val in = DataUtils.readCsv[MixedData](file)
        in.foreach(rs => {
          rs.foreach(data => {
            val o = DataUtils.mixedDataToScalaProtobuf(data).toByteArray
            val length = o.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(o)
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

    measure method "serialize - gzip compression" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new GzipCompressorOutputStream(new FileOutputStream(new File("protobufSerializationGzip.out")))
        var i = 0

        val in = DataUtils.readCsv[MixedData](file)
        in.foreach(rs => {
          rs.foreach(data => {
            val o = DataUtils.mixedDataToScalaProtobuf(data).toByteArray
            val length = o.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(o)
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

