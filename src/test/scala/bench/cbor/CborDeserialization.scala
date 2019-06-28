package bench.cbor

import java.io.{BufferedInputStream, File, FileInputStream, InputStream}
import java.nio.ByteBuffer

import bench.Settings
import io.bullet.borer.{Cbor, Decoder}
import net.jpountz.lz4.LZ4BlockInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyInputStream
import project._

object CborDeserialization extends Bench.LocalTime {
  override def aggregator: Aggregator[Double] = Aggregator.average

  override def measurer: Measurer[Double] = new Measurer.IgnoringGC

  @volatile
  var data: Data = _

  val streams = Map(
    "none" -> ((dataType: String) => new BufferedInputStream(new FileInputStream(new File(s"${dataType}CborSerialization.out")))),
    "gzip" -> ((dataType: String) => new GzipCompressorInputStream(new FileInputStream(new File(s"${dataType}CborSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyInputStream(new FileInputStream(new File(s"${dataType}CborSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockInputStream(new FileInputStream(new File(s"${dataType}CborSerializationLz4.out")))),
  )

  implicit val mixedDataDecoder = Decoder.forCaseClass[MixedData]
  implicit val onlyStringsDecoder = Decoder.forCaseClass[OnlyStrings]
  implicit val onlyLongsDecoder = Decoder.forCaseClass[OnlyLongs]

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

  performance of "mixedData cbor deserialization" in {
    measure method "deserialize" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { codec =>
        val in = streams(codec)("mixedData")
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = Cbor.decode(buffer).to[MixedData].value
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }

  performance of "onlyStrings cbor deserialization" in {
    measure method "deserialize" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { codec =>
        val in = streams(codec)("onlyStrings")
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = Cbor.decode(buffer).to[OnlyStrings].value
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }

  performance of "onlyLongs cbor deserialization" in {
    measure method "deserialize" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { codec =>
        val in = streams(codec)("onlyLongs")
        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = Cbor.decode(buffer).to[OnlyLongs].value
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }
}
