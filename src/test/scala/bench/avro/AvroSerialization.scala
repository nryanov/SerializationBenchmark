package bench.avro

import java.io.{File, FileOutputStream}

import bench.Settings
import com.sksamuel.avro4s.{AvroOutputStream, AvroSchema}
import org.apache.avro.file.{CodecFactory, DataFileWriter}
import org.apache.avro.generic.{GenericDatumWriter, GenericRecord}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{Data, DataUtils}

object AvroSerialization extends Bench.LocalTime {
  val codecs = Map(
    "none" -> CodecFactory.nullCodec(),
    "snappy" -> CodecFactory.snappyCodec(),
    "deflate" -> CodecFactory.deflateCodec(CodecFactory.DEFAULT_DEFLATE_LEVEL),
    "bzip2" -> CodecFactory.bzip2Codec(),
    "xz" -> CodecFactory.xzCodec(CodecFactory.DEFAULT_XZ_LEVEL)
  )

  val input = Gen.single("input file")("input.csv")
  val codec = Gen.enumeration("codec")(
    "none",
    "snappy",
    "deflate",
    "bzip2",
    "xz"
  )

  val schema = AvroSchema[Data]

  performance of "avro serialization" in {
    measure method "serialize with schema" in {
      using(Gen.crossProduct(input, codec)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { s =>
        val out = AvroOutputStream.data[Data].to(new FileOutputStream(new File(s"avroDataSerialization${s._2}.out"))).withCodec(codecs(s._2)).build(schema)
        var i = 0

        val in = DataUtils.readCsv(s._1)
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

    measure method "binary serialization without schema" in {
      using(Gen.crossProduct(input, codec)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { s =>
        val out = AvroOutputStream.binary[Data].to(new FileOutputStream(new File(s"avroBinarySerialization${s._2}.out"))).withCodec(codecs(s._2)).build(schema)
        var i = 0

        val in = DataUtils.readCsv(s._1)
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

    measure method "low-level API avro serialization" in {
      using(Gen.crossProduct(input, codec)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { s =>
        val out = new DataFileWriter[GenericRecord](new GenericDatumWriter[GenericRecord](schema)).setCodec(codecs(s._2)).create(schema, new File(s"lowLevelAvroSerialization${s._2}.out"))
        var i = 0

        val in = DataUtils.readCsv(s._1)
        in.foreach(rs => {
          rs.foreach(data => {
            out.append(DataUtils.dataToGenericRecord(data, schema))
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
