import java.io._
import java.nio.ByteBuffer

import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol}
import org.apache.thrift.transport.{TIOStreamTransport, TMemoryBuffer}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.{SnappyInputStream, SnappyOutputStream}
import project.DataUtils
import thriftBenchmark.scala.DataThrift
import InitialDataGenerator.recordsCount


object ThriftSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")

  performance of "thrift serialization" in {
    measure method "serialize using binary protocol" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("binaryThriftSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            DataThrift.encode(DataUtils.dataToScalaThrift(data), protocol)

            out.write(ByteBuffer.allocate(4).putInt(buffer.getArray.length).array())
            out.write(buffer.getArray)
            buffer.close()

            i += 1

            if (i == 1000) {
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

    measure method "serialize using binary protocol - snappy" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new SnappyOutputStream(new FileOutputStream(new File("binaryThriftSerializationSnappyCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            DataThrift.encode(DataUtils.dataToScalaThrift(data), protocol)

            out.write(ByteBuffer.allocate(4).putInt(buffer.getArray.length).array())
            out.write(buffer.getArray)
            buffer.close()

            i += 1

            if (i == 1000) {
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

    measure method "serialize using compact protocol" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("compactThriftSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            DataThrift.encode(DataUtils.dataToScalaThrift(data), protocol)

            out.write(ByteBuffer.allocate(4).putInt(buffer.getArray.length).array())
            out.write(buffer.getArray)
            buffer.close()

            i += 1

            if (i == 1000) {
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

    measure method "serialize using binary protocol - snappy" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new SnappyOutputStream(new FileOutputStream(new File("compactThriftSerializationSnappyCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            DataThrift.encode(DataUtils.dataToScalaThrift(data), protocol)

            out.write(ByteBuffer.allocate(4).putInt(buffer.getArray.length).array())
            out.write(buffer.getArray)
            buffer.close()

            i += 1

            if (i == 1000) {
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

  val binary = Gen.single("input file")("binaryThriftSerialization.out")
  val binarySnappy = Gen.single("input file")("binaryThriftSerializationSnappyCompression.out")
  val compact = Gen.single("input file")("compactThriftSerialization.out")
  val compactSnappy = Gen.single("input file")("compactThriftSerializationSnappyCompression.out")

  performance of "thrift deserialization" in {
    measure method "binary deserialization" in {
      using(binary) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
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
          val obj = DataThrift.decode(protocol)
          i += 1
          buffer.close()
        }

        in.close()
        assert(i == recordsCount)
      }
    }

    measure method "binary deserialization - snappy" in {
      using(binarySnappy) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
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
          val obj = DataThrift.decode(protocol)
          i += 1
          buffer.close()
        }

        in.close()
        assert(i == recordsCount)
      }
    }

    measure method "compact deserialization" in {
      using(compact) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
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
          val obj = DataThrift.decode(protocol)
          i += 1
          buffer.close()
        }

        in.close()
        assert(i == recordsCount)
      }
    }

    measure method "compact deserialization - snappy" in {
      using(compactSnappy) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
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
          val obj = DataThrift.decode(protocol)
          i += 1
          buffer.close()
        }

        in.close()
        assert(i == recordsCount)
      }
    }
  }
}
