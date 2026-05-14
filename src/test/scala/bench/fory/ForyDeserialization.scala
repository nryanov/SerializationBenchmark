package bench.fory

import bench.Settings
import bench.ScalameterImplicits._
import org.scalameter.picklers.Implicits._
import org.scalameter.api._
import com.github.luben.zstd.ZstdInputStream
import net.jpountz.lz4.LZ4BlockInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.fory.Fory
import org.scalameter.api
import org.scalameter.api.{Aggregator, Bench, Gen, Measurer}
import org.tukaani.xz.XZInputStream
import org.xerial.snappy.SnappyInputStream
import project.{Data, MixedData, NarrowMixedData, NarrowOnlyLongs, NarrowOnlyStrings, OnlyLongs, OnlyStrings}

import java.io.{BufferedInputStream, FileInputStream, InputStream}
import java.nio.ByteBuffer

object ForyDeserialization extends Bench.LocalTime {
  @volatile
  var data: Data = _

  override def aggregator: Aggregator[Double] = Aggregator.average
  override def measurer: Measurer[Double] = new api.Measurer.IgnoringGC

  val streams = Map(
    "none" -> ((dataType: String) => new BufferedInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerialization.out")))),
    "gzip" -> ((dataType: String) => new GzipCompressorInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerializationLz4.out")))),
    "xz" -> ((dataType: String) => new XZInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerializationXz.out")))),
    "zstd" -> ((dataType: String) => new ZstdInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerializationZstd.out"))))
  )

  val compression: Gen[String] = Gen.enumeration("compression")("none", "gzip", "snappy", "lz4", "xz", "zstd")

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

  performance.of("fory deserialization") in {
    measure.method("deserialize mixedData") in {
      using(compression).config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { compression =>
        // https://github.com/apache/fory/blob/57b0c9edde14eeb3557c1a015c48930d847cbc04/docs/guide/java/basic-serialization.md
        val fory = Fory.builder().withXlang(false).withRefTracking(false).withCompatible(false).requireClassRegistration(false).withAsyncCompilation(true).build()
        val in = streams(compression)("mixedData")
        fory.register(MixedData.getClass)

        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = fory.deserialize(buffer, MixedData.getClass).asInstanceOf[MixedData]
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure.method("deserialize onlyLongs") in {
      using(compression).config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { compression =>
        // https://github.com/apache/fory/blob/57b0c9edde14eeb3557c1a015c48930d847cbc04/docs/guide/java/basic-serialization.md
        val fory = Fory.builder().withXlang(false).withRefTracking(false).withCompatible(false).requireClassRegistration(false).withAsyncCompilation(true).build()
        val in = streams(compression)("onlyLongs")
        fory.register(OnlyLongs.getClass)

        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = fory.deserialize(buffer, OnlyLongs.getClass).asInstanceOf[OnlyLongs]
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure.method("deserialize onlyStrings") in {
      using(compression).config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { compression =>
        // https://github.com/apache/fory/blob/57b0c9edde14eeb3557c1a015c48930d847cbc04/docs/guide/java/basic-serialization.md
        val fory = Fory.builder().withXlang(false).withRefTracking(false).withCompatible(false).requireClassRegistration(false).withAsyncCompilation(true).build()
        val in = streams(compression)("onlyStrings")
        fory.register(OnlyStrings.getClass)

        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = fory.deserialize(buffer, OnlyStrings.getClass).asInstanceOf[OnlyStrings]
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure.method("deserialize narrowMixedData") in {
      using(compression).config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { compression =>
        // https://github.com/apache/fory/blob/57b0c9edde14eeb3557c1a015c48930d847cbc04/docs/guide/java/basic-serialization.md
        val fory = Fory.builder().withXlang(false).withRefTracking(false).withCompatible(false).requireClassRegistration(false).withAsyncCompilation(true).build()
        val in = streams(compression)("narrowMixedData")
        fory.register(NarrowMixedData.getClass)

        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = fory.deserialize(buffer, NarrowMixedData.getClass).asInstanceOf[NarrowMixedData]
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure.method("deserialize narrowOnlyLongs") in {
      using(compression).config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { compression =>
        // https://github.com/apache/fory/blob/57b0c9edde14eeb3557c1a015c48930d847cbc04/docs/guide/java/basic-serialization.md
        val fory = Fory.builder().withXlang(false).withRefTracking(false).withCompatible(false).requireClassRegistration(false).withAsyncCompilation(true).build()
        val in = streams(compression)("narrowOnlyLongs")
        fory.register(NarrowOnlyLongs.getClass)

        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = fory.deserialize(buffer, NarrowOnlyLongs.getClass).asInstanceOf[NarrowOnlyLongs]
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure.method("deserialize narrowOnlyStrings") in {
      using(compression).config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { compression =>
        // https://github.com/apache/fory/blob/57b0c9edde14eeb3557c1a015c48930d847cbc04/docs/guide/java/basic-serialization.md
        val fory = Fory.builder().withXlang(false).withRefTracking(false).withCompatible(false).requireClassRegistration(false).withAsyncCompilation(true).build()
        val in = streams(compression)("narrowOnlyStrings")
        fory.register(NarrowOnlyStrings.getClass)

        var i = 0

        val lengthBytes = new Array[Byte](4)
        var actual = readAll(in, lengthBytes, 0, lengthBytes.length)

        while (actual != -1) {
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          readAll(in, buffer, 0, buffer.length)
          data = fory.deserialize(buffer, NarrowOnlyStrings.getClass).asInstanceOf[NarrowOnlyStrings]
          i += 1
          actual = readAll(in, lengthBytes, 0, lengthBytes.length)
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }
}
