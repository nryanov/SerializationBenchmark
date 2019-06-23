package bench.java

import java.io._

import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.Data
import org.xerial.snappy._
import java.util.zip.GZIPInputStream

import bench.Settings
import net.jpountz.lz4.LZ4BlockInputStream
import org.scalameter.api


object JavaDeserialization extends Bench.LocalTime {
  @volatile
  var data: Data = _

  override def aggregator: Aggregator[Double] = Aggregator.average
  override def measurer: Measurer[Double] = new api.Measurer.IgnoringGC

  val streams = Map(
    "none" -> ((dataType: String) => new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(s"${dataType}JavaSerialization.out"))))),
    "gzip" -> ((dataType: String) => new ObjectInputStream(new GZIPInputStream(new FileInputStream(new File(s"${dataType}JavaSerializationGzip.out"))))),
    "snappy" -> ((dataType: String) => new ObjectInputStream(new SnappyInputStream(new FileInputStream(new File(s"${dataType}JavaSerializationSnappy.out"))))),
    "lz4" -> ((dataType: String) => new ObjectInputStream(new LZ4BlockInputStream(new FileInputStream(new File(s"${dataType}JavaSerializationLz4.out"))))),
  )

  val dataType = Gen.enumeration("input file")( "onlyLongs", "mixedData", "onlyStrings")
  val compression = Gen.enumeration("compression")("none", "gzip", "snappy", "lz4")

  performance of "java deserialization" in {
    measure method "deserialize" in {
      using(Gen.crossProduct(dataType, compression)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        val in = streams(gen._2)(gen._1)
        var i = 0

        var next = in.readObject()
        while (next != null) {
          i += 1
          data = next.asInstanceOf[Data]
          next = in.readObject()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }
    }
  }
}
