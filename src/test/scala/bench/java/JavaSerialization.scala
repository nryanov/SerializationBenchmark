package bench.java

import java.io._
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{DataUtils, MixedData, OnlyLongs, OnlyStrings}
import org.xerial.snappy._

import java.util.zip.GZIPOutputStream
import project.Implicits._
import bench.Settings
import bench.ScalameterImplicits._
import com.github.luben.zstd.ZstdOutputStream
import net.jpountz.lz4.LZ4BlockOutputStream
import org.scalameter.api
import org.tukaani.xz.{LZMA2Options, XZOutputStream}


object JavaSerialization extends Bench.LocalTime {
  override def aggregator: Aggregator[Double] = Aggregator.average
  override def measurer: Measurer[Double] = new api.Measurer.IgnoringGC

  val streams = Map(
    "none" -> ((dataType: String) => new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(Settings.file(s"${dataType}JavaSerialization.out"))))),
    "gzip" -> ((dataType: String) => new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(Settings.file(s"${dataType}JavaSerializationGzip.out"))))),
    "snappy" -> ((dataType: String) => new ObjectOutputStream(new SnappyOutputStream(new FileOutputStream(Settings.file(s"${dataType}JavaSerializationSnappy.out"))))),
    "lz4" -> ((dataType: String) => new ObjectOutputStream(new LZ4BlockOutputStream(new FileOutputStream(Settings.file(s"${dataType}JavaSerializationLz4.out"))))),
    "xz" -> ((dataType: String) => new ObjectOutputStream(new XZOutputStream(new FileOutputStream(Settings.file(s"${dataType}JavaSerializationXz.out")), new LZMA2Options()))),
    "zstd" -> ((dataType: String) => new ObjectOutputStream(new ZstdOutputStream(new FileOutputStream(Settings.file(s"${dataType}JavaSerializationZstd.out"))))),
  )

  val inputs = Map(
    "mixedData" -> (() => DataUtils.readCsv[MixedData](Settings.pathString(Settings.InputCsv.mixedData))),
    "onlyStrings" -> (() => DataUtils.readCsv[OnlyStrings](Settings.pathString(Settings.InputCsv.onlyStrings))),
    "onlyLongs" -> (() => DataUtils.readCsv[OnlyLongs](Settings.pathString(Settings.InputCsv.onlyLongs)))
  )

  val dataType = Gen.enumeration("input file")("onlyLongs", "mixedData", "onlyStrings")
  val compression = Gen.enumeration("compression")( "none", "gzip", "snappy", "lz4", "xz", "zstd")


  performance of "java serialization" in {
    measure method "serialize" in {
      using(Gen.crossProduct(dataType, compression)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        var i = 0

        val in = inputs(gen._1)()
        val out = streams(gen._2)(gen._1)
        in.foreach(rs => {
          rs.foreach(data => {
            out.writeObject(data)
            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              out.reset()
              i = 0
            }
          })
        })

        // to avoid catching EOF in deserialization
        out.writeObject(null)

        out.flush()
        out.close()
        in.close()
      }
    }
  }
}
