package bench.fory

import project.Implicits._
import bench.Settings
import bench.ScalameterImplicits._
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import com.github.luben.zstd.ZstdOutputStream
import net.jpountz.lz4.LZ4BlockOutputStream
import org.apache.fory.Fory
import org.scalameter.api.{Aggregator, Bench, Gen, Measurer}
import org.tukaani.xz.{LZMA2Options, XZOutputStream}
import org.xerial.snappy.SnappyOutputStream
import project.{DataUtils, MixedData, NarrowMixedData, NarrowOnlyLongs, NarrowOnlyStrings, OnlyLongs, OnlyStrings}

import java.io.{BufferedOutputStream, FileOutputStream}
import java.nio.ByteBuffer
import java.util.zip.GZIPOutputStream

object ForySerialization extends Bench.LocalTime {
  override def aggregator: Aggregator[Double] = Aggregator.average

  override def measurer: Measurer[Double] = new Measurer.IgnoringGC

  val streams = Map(
    "none" -> ((dataType: String) => new BufferedOutputStream(new FileOutputStream(Settings.file(s"${dataType}ForySerialization.out")))),
    "gzip" -> ((dataType: String) => new GZIPOutputStream(new FileOutputStream(Settings.file(s"${dataType}ForySerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyOutputStream(new FileOutputStream(Settings.file(s"${dataType}ForySerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockOutputStream(new FileOutputStream(Settings.file(s"${dataType}ForySerializationLz4.out")))),
    "xz" -> ((dataType: String) => new XZOutputStream(new FileOutputStream(Settings.file(s"${dataType}ForySerializationXz.out")), new LZMA2Options())),
    "zstd" -> ((dataType: String) => new ZstdOutputStream(new FileOutputStream(Settings.file(s"${dataType}ForySerializationZstd.out"))))
  )

  val dataType: Gen[String] = Gen.enumeration("input file")("onlyLongs", "mixedData", "onlyStrings", "narrowMixedData", "narrowOnlyStrings", "narrowOnlyLongs")
  val compression: Gen[String] = Gen.enumeration("compression")("none", "gzip", "snappy", "lz4", "xz", "zstd")

  val inputs = Map(
    "mixedData" -> (() => DataUtils.readCsv[MixedData](Settings.pathString(Settings.InputCsv.mixedData))),
    "onlyStrings" -> (() => DataUtils.readCsv[OnlyStrings](Settings.pathString(Settings.InputCsv.onlyStrings))),
    "onlyLongs" -> (() => DataUtils.readCsv[OnlyLongs](Settings.pathString(Settings.InputCsv.onlyLongs))),
    "narrowMixedData" -> (() => DataUtils.readCsv[NarrowMixedData](Settings.pathString(Settings.InputCsv.narrowMixedData))),
    "narrowOnlyStrings" -> (() => DataUtils.readCsv[NarrowOnlyStrings](Settings.pathString(Settings.InputCsv.narrowOnlyStrings))),
    "narrowOnlyLongs" -> (() => DataUtils.readCsv[NarrowOnlyLongs](Settings.pathString(Settings.InputCsv.narrowOnlyLongs)))
  )

  performance.of("fory serialization") in {
    measure.method("serialize") in {
      using(Gen.crossProduct(dataType, compression)).config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        val fory = Fory.builder().withXlang(false).withRefTracking(false).withCompatible(false).requireClassRegistration(true).withAsyncCompilation(true).build()
        val out = streams(gen._2)(gen._1)
        val in = inputs(gen._1)()

        gen._1 match {
          case "onlyLongs"         => fory.register(OnlyLongs.getClass)
          case "mixedData"         => fory.register(MixedData.getClass)
          case "onlyStrings"       => fory.register(OnlyStrings.getClass)
          case "narrowMixedData"   => fory.register(NarrowMixedData.getClass)
          case "narrowOnlyStrings" => fory.register(NarrowOnlyStrings.getClass)
          case "narrowOnlyLongs"   => fory.register(NarrowOnlyLongs.getClass)
        }

        var i = 0

        in.foreach(rs =>
          rs.foreach { data =>
            val buffer = fory.serialize(data)
            out.write(ByteBuffer.allocate(4).putInt(buffer.length).array())
            out.write(buffer)

            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
            }
          }
        )

        out.flush()
        out.close()
        in.close()
      }
    }
  }
}
