package bench.json

import java.io._
import java.nio.ByteBuffer

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.JsonMethods.mapper
import org.json4s.jackson.Serialization._
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy._
import project.Implicits._
import bench.Settings
import com.fasterxml.jackson.core.JsonParser.Feature
import net.jpountz.lz4.LZ4BlockOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import project._

object JsonSerialization extends Bench.LocalTime {
  implicit val noTypeHintsFormat = Serialization.formats(NoTypeHints)

  val streams = Map(
    "none" -> ((dataType: String) => new BufferedOutputStream(new FileOutputStream(new File(s"${dataType}JsonSerialization.out")))),
    "gzip" -> ((dataType: String) => new GzipCompressorOutputStream(new FileOutputStream(new File(s"${dataType}JsonSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyOutputStream(new FileOutputStream(new File(s"${dataType}JsonSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockOutputStream(new FileOutputStream(new File(s"${dataType}JsonSerializationLz4.out")))),
  )

  val inputs = Map(
    "mixedData" -> (() => DataUtils.readCsv[MixedData]("mixedDataInput.csv")),
    "onlyStrings" -> (() => DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv")),
    "onlyLongs" -> (() => DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv"))
  )

  val dataType = Gen.enumeration("input file")("onlyLongs", "mixedData", "onlyStrings")
  val compression = Gen.enumeration("compression")( "none", "gzip", "snappy", "lz4")

  mapper.configure(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
  mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)

  performance of "json serialization" in {
    measure method "serialize" in {
      using(Gen.crossProduct(dataType, compression)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val out = streams(gen._2)(gen._1)
        val sw = mapper.writerWithDefaultPrettyPrinter().writeValues(out)
        sw.init(true)

        var i = 0

        val in = inputs(gen._1)()

        in.foreach(rs => {
          rs.foreach(data => {
            sw.write(mapper.writeValueAsBytes(write[Data](data)))
            i += 1

            if (i == Settings.flushInterval) {
              sw.flush()
              i = 0
            }
          })
        })

        sw.flush()
        sw.close()
        in.close()
      }
    }
  }
}
