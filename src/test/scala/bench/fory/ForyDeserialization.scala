package bench.fory

import bench.Settings
import bench.ScalameterImplicits._
import bench.fory.ForySerialization.inputs
import org.scalameter.picklers.Implicits._
import org.scalameter.api._
import com.github.luben.zstd.ZstdInputStream
import net.jpountz.lz4.LZ4BlockInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.fory.Fory
import org.apache.fory.io.ForyInputStream
import org.scalameter.api
import org.scalameter.api.{Aggregator, Bench, Gen, Measurer}
import org.tukaani.xz.XZInputStream
import org.xerial.snappy.SnappyInputStream
import project.{Data, MixedData, NarrowMixedData, NarrowOnlyLongs, NarrowOnlyStrings, OnlyLongs, OnlyStrings}

import java.io.{BufferedInputStream, FileInputStream}

object ForyDeserialization extends Bench.LocalTime {
  @volatile
  var data: Data = _

  override def aggregator: Aggregator[Double] = Aggregator.average
  override def measurer: Measurer[Double] = new api.Measurer.IgnoringGC

  val streams = Map(
    "none" -> ((dataType: String) => new ForyInputStream(new BufferedInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerialization.out"))))),
    "gzip" -> ((dataType: String) => new ForyInputStream(new GzipCompressorInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerializationGzip.out"))))),
    "snappy" -> ((dataType: String) => new ForyInputStream(new SnappyInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerializationSnappy.out"))))),
    "lz4" -> ((dataType: String) => new ForyInputStream(new LZ4BlockInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerializationLz4.out"))))),
    "xz" -> ((dataType: String) => new ForyInputStream(new XZInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerializationXz.out"))))),
    "zstd" -> ((dataType: String) => new ForyInputStream(new ZstdInputStream(new FileInputStream(Settings.file(s"${dataType}ForySerializationZstd.out")))))
  )

  val dataType: Gen[String] = Gen.enumeration("input file")("onlyLongs", "mixedData", "onlyStrings", "narrowMixedData", "narrowOnlyStrings", "narrowOnlyLongs")
  val compression: Gen[String] = Gen.enumeration("compression")("none", "gzip", "snappy", "lz4", "xz", "zstd")

  performance.of("fory deserialization") in {
    measure.method("deserialize") in {
      using(Gen.crossProduct(dataType, compression)).config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        // https://github.com/apache/fory/blob/57b0c9edde14eeb3557c1a015c48930d847cbc04/docs/guide/java/basic-serialization.md
        val fory = Fory.builder().withXlang(false).withRefTracking(false).withCompatible(false).requireClassRegistration(true).withAsyncCompilation(true).build()
        val in = streams(gen._2)(gen._1)

        gen._1 match {
          case "onlyLongs"         => fory.register(OnlyLongs.getClass)
          case "mixedData"         => fory.register(MixedData.getClass)
          case "onlyStrings"       => fory.register(OnlyStrings.getClass)
          case "narrowMixedData"   => fory.register(NarrowMixedData.getClass)
          case "narrowOnlyStrings" => fory.register(NarrowOnlyStrings.getClass)
          case "narrowOnlyLongs"   => fory.register(NarrowOnlyLongs.getClass)
        }

        var i = 0

        fory.deserialize()

        while (in.available()) {
          val length = in.unpackInt()
          val buffer = in.readPayload(length)

          val unpacker = MessagePack.newDefaultUnpacker(buffer)
          data = mixedDataOps.msgunpack(unpacker)
          i += 1
          unpacker.close()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }

//        fory.register()
//        fory.deserialize()
//        fory.serialize()
      }
    }
  }
}
