package bench.msgpack

import java.io.{BufferedInputStream, File, FileInputStream}
import java.nio.ByteBuffer
import java.util.zip.GZIPInputStream

import bench.Settings
import org.msgpack.core.MessagePack
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyInputStream
import project.Implicits._

object MsgpackDeserialization extends Bench.LocalTime {
  val input = Gen.single("input file")("msgpackSerialization.out")
  val inputSnappy = Gen.single("input file")("msgpackSerializationSnappy.out")
  val inputGzip = Gen.single("input file")("msgpackSerializationGzip.out")

  performance of "msgpack deserialization" in {
    measure method "deserialize" in {
      using(input) config(
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
          val unpacker = MessagePack.newDefaultUnpacker(data)
          val obj = mixedDataOps.msgunpack(unpacker)
          i += 1
          unpacker.close()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }
    }

    measure method "deserialize - snappy" in {
      using(inputSnappy) config(
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
          val unpacker = MessagePack.newDefaultUnpacker(data)
          val obj = mixedDataOps.msgunpack(unpacker)
          i += 1
          unpacker.close()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }
    }

    measure method "deserialize - gzip" in {
      using(inputGzip) config(
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
          val unpacker = MessagePack.newDefaultUnpacker(data)
          val obj = mixedDataOps.msgunpack(unpacker)
          i += 1
          unpacker.close()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }
    }
  }
}
