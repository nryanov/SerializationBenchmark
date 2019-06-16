package bench.avro

import java.io.{File, FileOutputStream}

import bench.Settings
import com.sksamuel.avro4s.{AvroOutputStream, AvroSchema}
import org.apache.avro.Schema
import org.apache.avro.file.CodecFactory
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project._
import project.Implicits._

object AvroSerialization extends Bench.LocalTime {
  val codecs = Map(
    "none" -> CodecFactory.nullCodec(),
    "snappy" -> CodecFactory.snappyCodec(),
    "deflate" -> CodecFactory.deflateCodec(CodecFactory.DEFAULT_DEFLATE_LEVEL),
    "bzip2" -> CodecFactory.bzip2Codec(),
    "xz" -> CodecFactory.xzCodec(CodecFactory.DEFAULT_XZ_LEVEL)
  )

  override def aggregator: Aggregator[Double] = Aggregator.min

  val streams = Map(
    "data" -> ((dataType: String, codec: String, schema: Schema) => AvroOutputStream.data[Data].to(new FileOutputStream(new File(s"${dataType}AvroDataSerialization$codec.out"))).withCodec(codecs(codec)).build(schema)),
    "binary" -> ((dataType: String, codec: String, schema: Schema) => AvroOutputStream.binary[Data].to(new FileOutputStream(new File(s"${dataType}AvroBinarySerialization$codec.out"))).withCodec(codecs(codec)).build(schema))
  )

  val inputs = Map(
    "mixedData" -> (() => (AvroSchema[MixedData], DataUtils.readCsv[MixedData]("mixedDataInput.csv"))),
    "onlyStrings" -> (() => (AvroSchema[OnlyStrings], DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv"))),
    "onlyLongs" -> (() => (AvroSchema[OnlyLongs], DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv")))
  )

  val codec = Gen.enumeration("codec")(
    "none",
    "snappy",
    "deflate",
    "bzip2",
    "xz"
  )

  val format = Gen.enumeration("format")(
    "data",
    // binary do not use compression codec
//    "binary"
  )
  val dataType = Gen.enumeration("data type")("onlyLongs", "mixedData", "onlyStrings")

  performance of "avro serialization" in {
    measure method "serialize with schema" in {
      using(Gen.crossProduct(dataType, codec, format)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        var i = 0

        val (schema, in) = inputs(gen._1)()
        val out = streams(gen._3)(gen._1, gen._2, schema)

        in.foreach(rs => {
          rs.foreach(data => {
            out.write(data)
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
