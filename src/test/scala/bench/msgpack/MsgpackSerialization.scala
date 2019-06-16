package bench.msgpack

import java.io.{BufferedOutputStream, File, FileOutputStream}

import bench.Settings
import net.jpountz.lz4.LZ4BlockOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{DataUtils, MixedData, OnlyLongs, OnlyStrings}
import org.msgpack.core.MessagePack
import org.xerial.snappy.SnappyOutputStream
import project.Implicits._

object MsgpackSerialization extends Bench.LocalTime {
  val streams = Map(
    "none" -> ((dataType: String) => new BufferedOutputStream(new FileOutputStream(new File(s"${dataType}MsgpackSerialization.out")))),
    "gzip" -> ((dataType: String) => new GzipCompressorOutputStream(new FileOutputStream(new File(s"${dataType}MsgpackSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyOutputStream(new FileOutputStream(new File(s"${dataType}MsgpackSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockOutputStream(new FileOutputStream(new File(s"${dataType}MsgpackSerializationLz4.out")))),
  )

  override def aggregator: Aggregator[Double] = Aggregator.min

  val compression = Gen.enumeration("compression")( "none", "gzip", "snappy", "lz4")

  performance of "msgpack serialization" in {
    measure method "serialize - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val out = MessagePack.newDefaultPacker(streams(gen)("mixedData"))
        val packer = MessagePack.newDefaultBufferPacker
        var i = 0

        val in = DataUtils.readCsv[MixedData]("mixedDataInput.csv")
        in.foreach(rs => {
          rs.foreach(data => {
            mixedDataOps.msgpack(data, packer)
            val d = packer.toByteArray

            out.packInt(d.length)
            out.writePayload(d)

            packer.clear()
            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        packer.close()
        in.close()
      }
    }

    measure method "serialize - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val out = MessagePack.newDefaultPacker(streams(gen)("onlyStrings"))
        val packer = MessagePack.newDefaultBufferPacker
        var i = 0

        val in = DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv")
        in.foreach(rs => {
          rs.foreach(data => {
            onlyStringOps.msgpack(data, packer)
            val d = packer.toByteArray

            out.packInt(d.length)
            out.writePayload(d)

            packer.clear()
            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        packer.close()
        in.close()
      }
    }

    measure method "serialize - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val out = MessagePack.newDefaultPacker(streams(gen)("onlyLongs"))
        val packer = MessagePack.newDefaultBufferPacker
        var i = 0

        val in = DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv")
        in.foreach(rs => {
          rs.foreach(data => {
            onlyLongsOps.msgpack(data, packer)
            val d = packer.toByteArray

            out.packInt(d.length)
            out.writePayload(d)

            packer.clear()
            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        packer.close()
        in.close()
      }
    }
  }
}
