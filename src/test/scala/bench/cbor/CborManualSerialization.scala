package bench.cbor

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.nio.ByteBuffer
import java.util.zip.GZIPOutputStream

import bench.Settings
import io.bullet.borer.{Cbor, Encoder}
import net.jpountz.lz4.LZ4BlockOutputStream
import org.scalameter.api.{Aggregator, Bench, Gen, Measurer}
import org.xerial.snappy.SnappyOutputStream
import project.{DataUtils, MixedData, OnlyLongs, OnlyStrings}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.Implicits._

object CborManualSerialization extends Bench.LocalTime {
  override def aggregator: Aggregator[Double] = Aggregator.average

  override def measurer: Measurer[Double] = new Measurer.IgnoringGC

  val streams = Map(
    "none" -> ((dataType: String) => new BufferedOutputStream(new FileOutputStream(new File(s"${dataType}CborManualSerialization.out")))),
    "gzip" -> ((dataType: String) => new GZIPOutputStream(new FileOutputStream(new File(s"${dataType}CborManualSerializationGzip.out")))),
    "snappy" -> ((dataType: String) => new SnappyOutputStream(new FileOutputStream(new File(s"${dataType}CborManualSerializationSnappy.out")))),
    "lz4" -> ((dataType: String) => new LZ4BlockOutputStream(new FileOutputStream(new File(s"${dataType}CborManualSerializationLz4.out")))),
  )

  val compression = Gen.enumeration("compression")("none", "gzip", "snappy", "lz4")

  implicit val mixedDataEncoder = Encoder[MixedData]((writer, data) => {
    writer.writeString(data.f1.getOrElse(""))
    writer.writeDouble(data.f2.getOrElse(0.0))
    writer.writeLong(data.f3.getOrElse(0))
    writer.writeInt(data.f4.getOrElse(0))
    writer.writeString(data.f5.getOrElse(""))
    writer.writeDouble(data.f6.getOrElse(0.0))
    writer.writeLong(data.f7.getOrElse(0))
    writer.writeInt(data.f8.getOrElse(0))
    writer.writeInt(data.f9.getOrElse(0))
    writer.writeLong(data.f10.getOrElse(0))
    writer.writeFloat(data.f11.getOrElse(0.0f))
    writer.writeDouble(data.f12.getOrElse(0.0))
    writer.writeString(data.f13.getOrElse(""))
    writer.writeString(data.f14.getOrElse(""))
    writer.writeLong(data.f15.getOrElse(0))
    writer.writeInt(data.f16.getOrElse(0))
    writer.writeInt(data.f17.getOrElse(0))
    writer.writeString(data.f18.getOrElse(""))
    writer.writeString(data.f19.getOrElse(""))
    writer.writeString(data.f20.getOrElse(""))
  })
  implicit val onlyStringsEncoder = Encoder[OnlyStrings]((writer, data) => {
    writer.writeString(data.f1.getOrElse(""))
    writer.writeString(data.f2.getOrElse(""))
    writer.writeString(data.f3.getOrElse(""))
    writer.writeString(data.f4.getOrElse(""))
    writer.writeString(data.f5.getOrElse(""))
    writer.writeString(data.f6.getOrElse(""))
    writer.writeString(data.f7.getOrElse(""))
    writer.writeString(data.f8.getOrElse(""))
    writer.writeString(data.f9.getOrElse(""))
    writer.writeString(data.f10.getOrElse(""))
    writer.writeString(data.f11.getOrElse(""))
    writer.writeString(data.f12.getOrElse(""))
    writer.writeString(data.f13.getOrElse(""))
    writer.writeString(data.f14.getOrElse(""))
    writer.writeString(data.f15.getOrElse(""))
    writer.writeString(data.f16.getOrElse(""))
    writer.writeString(data.f17.getOrElse(""))
    writer.writeString(data.f18.getOrElse(""))
    writer.writeString(data.f19.getOrElse(""))
    writer.writeString(data.f20.getOrElse(""))
  })
  implicit val onlyLongsEncoder = Encoder[OnlyLongs]((writer, data) => {
    writer.writeLong(data.f1.getOrElse(0))
    writer.writeLong(data.f2.getOrElse(0))
    writer.writeLong(data.f3.getOrElse(0))
    writer.writeLong(data.f4.getOrElse(0))
    writer.writeLong(data.f5.getOrElse(0))
    writer.writeLong(data.f6.getOrElse(0))
    writer.writeLong(data.f7.getOrElse(0))
    writer.writeLong(data.f8.getOrElse(0))
    writer.writeLong(data.f9.getOrElse(0))
    writer.writeLong(data.f10.getOrElse(0))
    writer.writeLong(data.f11.getOrElse(0))
    writer.writeLong(data.f12.getOrElse(0))
    writer.writeLong(data.f13.getOrElse(0))
    writer.writeLong(data.f14.getOrElse(0))
    writer.writeLong(data.f15.getOrElse(0))
    writer.writeLong(data.f16.getOrElse(0))
    writer.writeLong(data.f17.getOrElse(0))
    writer.writeLong(data.f18.getOrElse(0))
    writer.writeLong(data.f19.getOrElse(0))
    writer.writeLong(data.f20.getOrElse(0))
  })

  performance of "mixedData cbor serialization" in {
    measure method "serialize" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns,
        exec.independentSamples -> Settings.independentSamples
      ) in { gen =>
        val out = streams(gen)("mixedData")
        val in = DataUtils.readCsv[MixedData]("mixedDataInput.csv")

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
        val in = DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv")

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
        val in = DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv")

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