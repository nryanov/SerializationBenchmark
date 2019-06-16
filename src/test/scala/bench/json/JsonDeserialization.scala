package bench.json

import java.io._

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.JsonMethods.mapper
import org.json4s.jackson.Serialization._
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy._
import project.{Data, MixedData, OnlyLongs, OnlyStrings}
import bench.Settings
import com.fasterxml.jackson.core.JsonParser.Feature
import net.jpountz.lz4.LZ4BlockInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

object JsonDeserialization extends Bench.LocalTime {
  implicit val noTypeHintsFormat = Serialization.formats(NoTypeHints)

  override def aggregator: Aggregator[Double] = Aggregator.min

  @volatile
  var data: Data = _

  val streams = Map(
    "none" -> ((dataType: String) => new BufferedInputStream(new FileInputStream(new File(s"${dataType}JsonSerialization.out")))),
    "gzip" -> ((dataType: String) => new GzipCompressorInputStream(new FileInputStream(new File(s"${dataType}JsonSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyInputStream(new FileInputStream(new File(s"${dataType}JsonSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockInputStream(new FileInputStream(new File(s"${dataType}JsonSerializationLz4.out")))),
  )

  val bytesToData = Map(
    "onlyLongs" -> ((b: Array[Byte]) => read[OnlyLongs](mapper.readTree(b).asText())),
    "mixedData" -> ((b: Array[Byte]) => read[MixedData](mapper.readTree(b).asText())),
    "onlyStrings" -> ((b: Array[Byte]) => read[OnlyStrings](mapper.readTree(b).asText()))
  )

  val dataType = Gen.enumeration("input file")("onlyLongs", "mixedData", "onlyStrings")
  val compression = Gen.enumeration("compression")("none", "gzip", "snappy", "lz4")

  mapper.configure(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
  mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)

  performance of "json deserialization" in {
    measure method "deserialize" in {
      using(Gen.crossProduct(dataType, compression)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        var i = 0
        val in = streams(gen._2)(gen._1)
        val sr = mapper.readerFor(classOf[Array[Byte]]).readValues[Array[Byte]](in)
        val b2D = bytesToData(gen._1)

        while (sr.hasNext) {
          i += 1
          data = b2D(sr.next())
        }

        sr.close()
        assert(i == Settings.recordsCount)
      }
    }
  }
}
