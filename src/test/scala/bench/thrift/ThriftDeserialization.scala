package bench.thrift

import java.io._
import java.nio.ByteBuffer

import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol}
import org.apache.thrift.transport.{TIOStreamTransport, TMemoryBuffer}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyInputStream
import thriftBenchmark.scala.MixedData
import bench.Settings
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

object ThriftDeserialization extends Bench.LocalTime {
  val binary = Gen.single("input file")("binaryThriftSerialization.out")
  val binarySnappy = Gen.single("input file")("binaryThriftSerializationSnappyCompression.out")
  val binaryGzip = Gen.single("input file")("binaryThriftSerializationGzipCompression.out")
  val compact = Gen.single("input file")("compactThriftSerialization.out")
  val compactSnappy = Gen.single("input file")("compactThriftSerializationSnappyCompression.out")
  val compactGzip = Gen.single("input file")("compactThriftSerializationGzipCompression.out")

  performance of "thrift deserialization" in {
    measure method "binary deserialization" in {
      using(binary) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new BufferedInputStream(new FileInputStream(new File(file)))
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()
        var i = 0

        while (in.available() > 0) {
          val lengthBytes = new Array[Byte](4)
          in.read(lengthBytes)
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val data = new Array[Byte](length)
          in.read(data)
          buffer.write(data)
          val protocol = protocolFactory.getProtocol(buffer)
          val obj = MixedData.decode(protocol)
          i += 1
          buffer.close()
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "binary deserialization - snappy" in {
      using(binarySnappy) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new SnappyInputStream(new FileInputStream(new File(file)))
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()
        var i = 0

        while (in.available() > 0) {
          val lengthBytes = new Array[Byte](4)
          in.read(lengthBytes)
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val data = new Array[Byte](length)
          in.read(data)
          buffer.write(data)
          val protocol = protocolFactory.getProtocol(buffer)
          val obj = MixedData.decode(protocol)
          i += 1
          buffer.close()
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "binary deserialization - gzip" in {
      using(binaryGzip) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new GzipCompressorInputStream(new FileInputStream(new File(file)))
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var cnt = in.read(lengthBytes)

        while (cnt != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val data = new Array[Byte](length)
          in.read(data)
          buffer.write(data)
          val protocol = protocolFactory.getProtocol(buffer)
          val obj = MixedData.decode(protocol)
          i += 1
          buffer.close()
          cnt = in.read(lengthBytes)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "compact deserialization" in {
      using(compact) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new BufferedInputStream(new FileInputStream(new File(file)))
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()
        val transport: TIOStreamTransport = new TIOStreamTransport(in)

        var i = 0

        while (in.available() > 0) {
          val lengthBytes = new Array[Byte](4)
          in.read(lengthBytes)
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val data = new Array[Byte](length)
          in.read(data)
          buffer.write(data)
          val protocol = protocolFactory.getProtocol(buffer)
          val obj = MixedData.decode(protocol)
          i += 1
          buffer.close()
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "compact deserialization - snappy" in {
      using(compactSnappy) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new SnappyInputStream(new FileInputStream(new File(file)))
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()
        val transport: TIOStreamTransport = new TIOStreamTransport(in)

        var i = 0

        while (in.available() > 0) {
          val lengthBytes = new Array[Byte](4)
          in.read(lengthBytes)
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val data = new Array[Byte](length)
          in.read(data)
          buffer.write(data)
          val protocol = protocolFactory.getProtocol(buffer)
          val obj = MixedData.decode(protocol)
          i += 1
          buffer.close()
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }


    measure method "compact deserialization - gzip" in {
      using(compactGzip) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new GzipCompressorInputStream(new FileInputStream(new File(file)))
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()
        val transport: TIOStreamTransport = new TIOStreamTransport(in)

        var i = 0
        val lengthBytes = new Array[Byte](4)
        var cnt = in.read(lengthBytes)

        while (cnt != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new TMemoryBuffer(length)
          val data = new Array[Byte](length)
          in.read(data)
          buffer.write(data)
          val protocol = protocolFactory.getProtocol(buffer)
          val obj = MixedData.decode(protocol)
          i += 1
          buffer.close()
          cnt = in.read(lengthBytes)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }
}
