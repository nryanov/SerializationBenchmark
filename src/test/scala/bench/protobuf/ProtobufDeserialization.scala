package bench.protobuf

import java.io._
import java.nio.ByteBuffer

import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyInputStream
import bench.Settings
import net.jpountz.lz4.LZ4BlockInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

object ProtobufDeserialization extends Bench.LocalTime {
  val streams = Map(
    "none" -> ((dataType: String) => new BufferedInputStream(new FileInputStream(new File(s"${dataType}ProtobufSerialization.out")))),
    "gzip" -> ((dataType: String) => new GzipCompressorInputStream(new FileInputStream(new File(s"${dataType}ProtobufSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyInputStream(new FileInputStream(new File(s"${dataType}ProtobufSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockInputStream(new FileInputStream(new File(s"${dataType}ProtobufSerializationLz4.out")))),
  )

  override def aggregator: Aggregator[Double] = Aggregator.min

  val compression = Gen.enumeration("compression")( "none", "gzip", "snappy", "lz4")

  def readAll(in: InputStream, buffer: Array[Byte], off: Int, len: Int): Int = {
    var got = 0
    var ret = 0

    while (got < len) {
      ret = in.read(buffer, off + got, len - got)

      if (ret > 0) {
        got += ret
      } else {
        return -1
      }
    }

    got
  }

  @volatile
  var data: Any = _

  performance of "protobuf deserialization" in {
    measure method "deserialize - mixed data" in {
      using(compression) config (
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val in = streams(codec)("mixedData")
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = protobufBenchmark.data.MixedData.parseFrom(buffer)
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize - only strings" in {
      using(compression) config (
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val in = streams(codec)("onlyStrings")
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = protobufBenchmark.data.OnlyStrings.parseFrom(buffer)
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize - only longs" in {
      using(compression) config (
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val in = streams(codec)("onlyLongs")
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = protobufBenchmark.data.OnlyLongs.parseFrom(buffer)
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }
}
