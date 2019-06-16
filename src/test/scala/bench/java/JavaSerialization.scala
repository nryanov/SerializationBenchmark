package bench.java

import java.io._

import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{DataUtils, MixedData, OnlyLongs, OnlyStrings}
import org.xerial.snappy._
import java.util.zip.GZIPOutputStream

import project.Implicits._
import bench.Settings
import net.jpountz.lz4.LZ4BlockOutputStream


object JavaSerialization extends Bench.LocalTime {
  override def aggregator: Aggregator[Double] = Aggregator.min

  val streams = Map(
    "none" -> ((dataType: String) => new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(s"${dataType}JavaSerialization.out"))))),
    "gzip" -> ((dataType: String) => new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(new File(s"${dataType}JavaSerializationGzip.out"))))),
    "snappy" -> ((dataType: String) => new ObjectOutputStream(new SnappyOutputStream(new FileOutputStream(new File(s"${dataType}JavaSerializationSnappy.out"))))),
    "lz4" -> ((dataType: String) => new ObjectOutputStream(new LZ4BlockOutputStream(new FileOutputStream(new File(s"${dataType}JavaSerializationLz4.out"))))),
  )

  val inputs = Map(
    "mixedData" -> (() => DataUtils.readCsv[MixedData]("mixedDataInput.csv")),
    "onlyStrings" -> (() => DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv")),
    "onlyLongs" -> (() => DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv"))
  )

  val dataType = Gen.enumeration("input file")("onlyLongs", "mixedData", "onlyStrings")
  val compression = Gen.enumeration("compression")( "none", "gzip", "snappy", "lz4")


  performance of "java serialization" in {
    measure method "serialize" in {
      using(Gen.crossProduct(dataType, compression)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
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
