package bench.msgpack

import java.io.{BufferedInputStream, File, FileInputStream}

import bench.Settings
import net.jpountz.lz4.LZ4BlockInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.msgpack.core.MessagePack
import org.scalameter.api
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyInputStream
import project.Data
import project.Implicits._

object MsgpackDeserialization extends Bench.LocalTime {
  @volatile
  var data: Data = _

  override def aggregator: Aggregator[Double] = Aggregator.average
  override def measurer: Measurer[Double] = new api.Measurer.IgnoringGC

  val streams = Map(
    "none" -> ((dataType: String) => new BufferedInputStream(new FileInputStream(new File(s"${dataType}MsgpackSerialization.out")))),
    "gzip" -> ((dataType: String) => new GzipCompressorInputStream(new FileInputStream(new File(s"${dataType}MsgpackSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyInputStream(new FileInputStream(new File(s"${dataType}MsgpackSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockInputStream(new FileInputStream(new File(s"${dataType}MsgpackSerializationLz4.out")))),
  )

  val compression = Gen.enumeration("compression")( "none", "gzip", "snappy", "lz4")
  performance of "msgpack deserialization" in {
    measure method "deserialize - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        val in = MessagePack.newDefaultUnpacker(streams(gen)("mixedData"))
        var i = 0

        while (in.hasNext) {
          val length = in.unpackInt()
          val buffer = in.readPayload(length)

          val unpacker = MessagePack.newDefaultUnpacker(buffer)
          data = mixedDataOps.msgunpack(unpacker)
          i += 1
          unpacker.close()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }
    }

    measure method "deserialize - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        val in = MessagePack.newDefaultUnpacker(streams(gen)("onlyStrings"))
        var i = 0

        while (in.hasNext) {
          val length = in.unpackInt()
          val buffer = in.readPayload(length)

          val unpacker = MessagePack.newDefaultUnpacker(buffer)
          data = onlyStringOps.msgunpack(unpacker)
          i += 1
          unpacker.close()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }
    }

    measure method "deserialize - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        val in = MessagePack.newDefaultUnpacker(streams(gen)("onlyLongs"))
        var i = 0

        while (in.hasNext) {
          val length = in.unpackInt()
          val buffer = in.readPayload(length)

          val unpacker = MessagePack.newDefaultUnpacker(buffer)
          data = onlyLongsOps.msgunpack(unpacker)
          i += 1
          unpacker.close()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }
    }
  }
}
