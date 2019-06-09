package bench.protobuf

import java.io._
import java.nio.ByteBuffer

import bench.Settings
import net.jpountz.lz4.LZ4BlockOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyOutputStream
import project.{DataUtils, MixedData, OnlyLongs, OnlyStrings}
import project.Implicits._

object ProtobufSerialization extends Bench.ForkedTime {
  val streams = Map(
    "none" -> ((dataType: String) => new BufferedOutputStream(new FileOutputStream(new File(s"${dataType}ProtobufSerialization.out")))),
    "gzip" -> ((dataType: String) => new GzipCompressorOutputStream(new FileOutputStream(new File(s"${dataType}ProtobufSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyOutputStream(new FileOutputStream(new File(s"${dataType}ProtobufSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockOutputStream(new FileOutputStream(new File(s"${dataType}ProtobufSerializationLz4.out")))),
  )

  override def aggregator: Aggregator[Double] = Aggregator.average

  val compression = Gen.enumeration("compression")( "none", "gzip", "snappy", "lz4")

  performance of "protobuf serialization" in {
    measure method "serialize - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val out = streams(codec)("mixedData")
        var i = 0

        val in = DataUtils.readCsv[MixedData]("mixedDataInput.csv")
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

    measure method "serialize - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val out = streams(codec)("onlyStrings")
        var i = 0

        val in = DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv")
        in.foreach(rs => {
          rs.foreach(data => {
            val o = DataUtils.onlyStringsToScalaProtobufdata(data).toByteArray
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

    measure method "serialize - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val out = streams(codec)("onlyLongs")
        var i = 0

        val in = DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv")
        in.foreach(rs => {
          rs.foreach(data => {
            val o = DataUtils.onlyLongsToScalaProtobuf(data).toByteArray
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

