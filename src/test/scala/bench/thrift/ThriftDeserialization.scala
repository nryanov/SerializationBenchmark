package bench.thrift

import java.io._
import java.nio.ByteBuffer

import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol}
import org.apache.thrift.transport.TMemoryBuffer
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyInputStream
import bench.Settings
import net.jpountz.lz4.LZ4BlockInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

object ThriftDeserialization extends Bench.LocalTime {
  @volatile
  var data: Any = _

  override def aggregator: Aggregator[Double] = Aggregator.min

  val streams = Map(
    "none" -> ((dataType: String) => new BufferedInputStream(new FileInputStream(new File(s"${dataType}ThriftSerialization.out")))),
    "gzip" -> ((dataType: String) => new GzipCompressorInputStream(new FileInputStream(new File(s"${dataType}ThriftSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyInputStream(new FileInputStream(new File(s"${dataType}ThriftSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockInputStream(new FileInputStream(new File(s"${dataType}ThriftSerializationLz4.out")))),
  )

  val compression = Gen.enumeration("compression")("none", "gzip", "snappy", "lz4")

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

  performance of "thrift deserialization" in {
    measure method "binary deserialization - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val in = streams(codec)("mixedDataBinary")
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val dataArray = new Array[Byte](length)
          val protocol = protocolFactory.getProtocol(buffer)

          readAll(in, dataArray, 0, dataArray.length)
          buffer.write(dataArray)

          data = thriftBenchmark.scala.MixedData.decode(protocol)
          i += 1

          buffer.close()
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "compact deserialization - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val in = streams(codec)("mixedDataCompact")
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val dataArray = new Array[Byte](length)
          val protocol = protocolFactory.getProtocol(buffer)

          readAll(in, dataArray, 0, dataArray.length)
          buffer.write(dataArray)

          data = thriftBenchmark.scala.MixedData.decode(protocol)
          i += 1

          buffer.close()
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "binary deserialization - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val in = streams(codec)("onlyStringsBinary")
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val dataArray = new Array[Byte](length)
          val protocol = protocolFactory.getProtocol(buffer)

          readAll(in, dataArray, 0, dataArray.length)
          buffer.write(dataArray)

          data = thriftBenchmark.scala.OnlyStrings.decode(protocol)
          i += 1

          buffer.close()
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "compact deserialization - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val in = streams(codec)("onlyStringsCompact")
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val dataArray = new Array[Byte](length)
          val protocol = protocolFactory.getProtocol(buffer)

          readAll(in, dataArray, 0, dataArray.length)
          buffer.write(dataArray)

          data = thriftBenchmark.scala.OnlyStrings.decode(protocol)
          i += 1

          buffer.close()
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "binary deserialization - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val in = streams(codec)("onlyLongsBinary")
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val dataArray = new Array[Byte](length)
          val protocol = protocolFactory.getProtocol(buffer)

          readAll(in, dataArray, 0, dataArray.length)
          buffer.write(dataArray)

          data = thriftBenchmark.scala.OnlyLongs.decode(protocol)
          i += 1

          buffer.close()
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "compact deserialization - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val in = streams(codec)("onlyLongsCompact")
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val dataArray = new Array[Byte](length)
          val protocol = protocolFactory.getProtocol(buffer)

          readAll(in, dataArray, 0, dataArray.length)
          buffer.write(dataArray)

          data = thriftBenchmark.scala.OnlyLongs.decode(protocol)
          i += 1

          buffer.close()
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }
}
