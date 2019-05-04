package bench.thrift

import java.io._
import java.nio.ByteBuffer

import bench.Settings
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol}
import org.apache.thrift.transport.TMemoryBuffer
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyOutputStream
import project.DataUtils
import thriftBenchmark.scala.DataThrift

object ThriftSerialization extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")

  performance of "thrift serialization" in {
    measure method "serialize using binary protocol" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
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

    measure method "serialize using binary protocol - snappy" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
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

    measure method "serialize using binary protocol - gzip" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new GzipCompressorOutputStream(new FileOutputStream(new File("binaryThriftSerializationGzipCompression.out")))
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

    measure method "serialize using compact protocol" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
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

    measure method "serialize using binary protocol - snappy" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
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

    measure method "serialize using binary protocol - gzip" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new GzipCompressorOutputStream(new FileOutputStream(new File("compactThriftSerializationGzipCompression.out")))
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
