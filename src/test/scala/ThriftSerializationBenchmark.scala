import java.io.{BufferedOutputStream, File, FileInputStream, FileOutputStream}

import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol}
import org.apache.thrift.transport.{TIOStreamTransport, TMemoryBuffer}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyFramedOutputStream
import project.DataUtils
import thriftBenchmark.scala.DataThrift

object ThriftSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("50000.csv")

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
        val out = new SnappyFramedOutputStream(new FileOutputStream(new File("binaryThriftSerializationSnappyCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            DataThrift.encode(DataUtils.dataToScalaThrift(data), protocol)

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
        val out = new SnappyFramedOutputStream(new FileOutputStream(new File("compactThriftSerializationSnappyCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            DataThrift.encode(DataUtils.dataToScalaThrift(data), protocol)

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
  val compact = Gen.single("input file")("compactThriftSerialization.out")

  performance of "thrift deserialization" in {
    measure method "binary deserialization" in {
      using(binary) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val in = new FileInputStream(file)
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()
        val transport: TIOStreamTransport = new TIOStreamTransport(in)
        val protocol = protocolFactory.getProtocol(transport)

        DataThrift.decode(protocol)
      }
    }

    measure method "compact deserialization" in {
      using(compact) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val in = new FileInputStream(file)
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()
        val transport: TIOStreamTransport = new TIOStreamTransport(in)
        val protocol = protocolFactory.getProtocol(transport)

        DataThrift.decode(protocol)
      }
    }
  }
}
