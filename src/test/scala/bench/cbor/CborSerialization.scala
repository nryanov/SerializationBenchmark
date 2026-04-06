package bench.cbor

import java.io.{BufferedOutputStream, FileOutputStream}
import java.nio.ByteBuffer
import java.util.zip.GZIPOutputStream
import bench.Settings
import bench.ScalameterImplicits._
import io.bullet.borer.Encoder
import io.bullet.borer.Cbor
import io.bullet.borer.derivation.MapBasedCodecs._
import net.jpountz.lz4.LZ4BlockOutputStream
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.tukaani.xz.{ARM64Options, XZOutputStream}
import org.xerial.snappy.SnappyOutputStream
import project.{DataUtils, MixedData, OnlyLongs, OnlyStrings}
import project.Implicits._

object CborSerialization extends Bench.LocalTime {
  override def aggregator: Aggregator[Double] = Aggregator.average

  override def measurer: Measurer[Double] = new Measurer.IgnoringGC

  val streams = Map(
    "none" -> ((dataType: String) => new BufferedOutputStream(new FileOutputStream(Settings.file(s"${dataType}CborSerialization.out")))),
    "gzip" -> ((dataType: String) => new GZIPOutputStream(new FileOutputStream(Settings.file(s"${dataType}CborSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyOutputStream(new FileOutputStream(Settings.file(s"${dataType}CborSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockOutputStream(new FileOutputStream(Settings.file(s"${dataType}CborSerializationLz4.out")))),
    "xz" -> ((dataType: String) => new XZOutputStream(new FileOutputStream(Settings.file(s"${dataType}CborSerializationXz.out")), new ARM64Options())),
  )

  val compression = Gen.enumeration("compression")( "none", "gzip", "snappy", "lz4", "xz")

  implicit val mixedDataEncoder: Encoder[MixedData] = deriveEncoder[MixedData]
  implicit val onlyStringsEncoder: Encoder[OnlyStrings] = deriveEncoder[OnlyStrings]
  implicit val onlyLongsEncoder: Encoder[OnlyLongs] = deriveEncoder[OnlyLongs]


  performance of "mixedData cbor serialization" in {
    measure method "serialize" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        val out = streams(gen)("mixedData")
        val in = DataUtils.readCsv[MixedData](Settings.pathString(Settings.InputCsv.mixedData))

        var i = 0
        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = Cbor.encode(data).toByteArray

            out.write(ByteBuffer.allocate(4).putInt(buffer.length).array())
            out.write(buffer)

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

  performance of "onlyStrings cbor serialization" in {
    measure method "serialize" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        val out = streams(gen)("onlyStrings")
        val in = DataUtils.readCsv[OnlyStrings](Settings.pathString(Settings.InputCsv.onlyStrings))

        var i = 0
        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = Cbor.encode(data).toByteArray


            out.write(ByteBuffer.allocate(4).putInt(buffer.length).array())
            out.write(buffer)

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

  performance of "onlyLongs cbor serialization" in {
    measure method "serialize" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        val out = streams(gen)("onlyLongs")
        val in = DataUtils.readCsv[OnlyLongs](Settings.pathString(Settings.InputCsv.onlyLongs))

        var i = 0
        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = Cbor.encode(data).toByteArray

            out.write(ByteBuffer.allocate(4).putInt(buffer.length).array())
            out.write(buffer)

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
