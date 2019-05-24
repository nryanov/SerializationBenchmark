package bench.thrift

import java.io._
import java.nio.ByteBuffer

import bench.Settings
import net.jpountz.lz4.LZ4BlockOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol}
import org.apache.thrift.transport.TMemoryBuffer
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyOutputStream
import project.{DataUtils, MixedData, OnlyLongs, OnlyStrings}
import project.Implicits._

object ThriftSerialization extends Bench.LocalTime {
  val streams = Map(
    "none" -> ((dataType: String) => new FileOutputStream(new File(s"${dataType}ThriftSerialization.out"))),
    "gzip" -> ((dataType: String) => new GzipCompressorOutputStream(new FileOutputStream(new File(s"${dataType}ThriftSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyOutputStream(new FileOutputStream(new File(s"${dataType}ThriftSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockOutputStream(new FileOutputStream(new File(s"${dataType}ThriftSerializationLz4.out")))),
  )

  val compression = Gen.enumeration("compression")( "none", "gzip", "snappy", "lz4")

  performance of "thrift serialization" in {
    measure method "serialize using binary protocol - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val out = streams(codec)("mixedDataBinary")
        var i = 0

        val in = DataUtils.readCsv[MixedData]("mixedDataInput.csv")
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            thriftBenchmark.scala.MixedData.encode(DataUtils.mixedDataToScalaThrift(data), protocol)

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

    measure method "serialize using compact protocol - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val out = streams(codec)("mixedDataCompact")
        var i = 0

        val in = DataUtils.readCsv[MixedData]("mixedDataInput.csv")
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            thriftBenchmark.scala.MixedData.encode(DataUtils.mixedDataToScalaThrift(data), protocol)

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

    measure method "serialize using binary protocol - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val out = streams(codec)("onlyStringsBinary")
        var i = 0

        val in = DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv")
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            thriftBenchmark.scala.OnlyStrings.encode(DataUtils.onlyStringsToScalaThrift(data), protocol)

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

    measure method "serialize using compact protocol - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val out = streams(codec)("onlyStringsCompact")
        var i = 0

        val in = DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv")
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            thriftBenchmark.scala.OnlyStrings.encode(DataUtils.onlyStringsToScalaThrift(data), protocol)

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

    measure method "serialize using binary protocol - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val out = streams(codec)("onlyLongsBinary")
        var i = 0

        val in = DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv")
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            thriftBenchmark.scala.OnlyLongs.encode(DataUtils.onlyLongsToScalaThrift(data), protocol)

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

    measure method "serialize using compact protocol - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val out = streams(codec)("onlyLongsCompact")
        var i = 0

        val in = DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv")
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            thriftBenchmark.scala.OnlyLongs.encode(DataUtils.onlyLongsToScalaThrift(data), protocol)

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
