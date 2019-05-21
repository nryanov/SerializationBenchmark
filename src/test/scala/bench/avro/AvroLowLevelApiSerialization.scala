package bench.avro

import java.io.File

import bench.Settings
import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.file.{CodecFactory, DataFileWriter}
import org.apache.avro.generic.{GenericDatumWriter, GenericRecord}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project._
import project.Implicits._


object AvroLowLevelApiSerialization extends Bench.LocalTime {
  val codecs = Map(
    "none" -> CodecFactory.nullCodec(),
    "snappy" -> CodecFactory.snappyCodec(),
    "deflate" -> CodecFactory.deflateCodec(CodecFactory.DEFAULT_DEFLATE_LEVEL),
    "bzip2" -> CodecFactory.bzip2Codec(),
    "xz" -> CodecFactory.xzCodec(CodecFactory.DEFAULT_XZ_LEVEL)
  )

  val codec = Gen.enumeration("codec")(
    "none",
    "snappy",
    "deflate",
    "bzip2",
    "xz"
  )

  performance of "avro serialization" in {
    measure method "low-level API avro serialization - mixed data" in {
      using(codec) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { s =>
        val schema = AvroSchema[MixedData]
        val out = new DataFileWriter[GenericRecord](new GenericDatumWriter[GenericRecord](schema)).setCodec(codecs(s)).create(schema, new File(s"mixedDataLowLevelAvroSerialization${s}.out"))
        var i = 0

        val in = DataUtils.readCsv[MixedData]("mixedDataInput.csv")
        in.foreach(rs => {
          rs.foreach(data => {
            out.append(mixedDataOps.toGenericRecord(data))
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

    measure method "low-level API avro serialization - only strings" in {
      using(codec) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { s =>
        val schema = AvroSchema[OnlyStrings]
        val out = new DataFileWriter[GenericRecord](new GenericDatumWriter[GenericRecord](schema)).setCodec(codecs(s)).create(schema, new File(s"onlyStringsLowLevelAvroSerialization${s}.out"))
        var i = 0

        val in = DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv")
        in.foreach(rs => {
          rs.foreach(data => {
            out.append(onlyStringOps.toGenericRecord(data))
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

    measure method "low-level API avro serialization - only longs" in {
      using(codec) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { s =>
        val schema = AvroSchema[OnlyLongs]
        val out = new DataFileWriter[GenericRecord](new GenericDatumWriter[GenericRecord](schema)).setCodec(codecs(s)).create(schema, new File(s"onlyLongsLowLevelAvroSerialization${s}.out"))
        var i = 0

        val in = DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv")
        in.foreach(rs => {
          rs.foreach(data => {
            out.append(onlyLongsOps.toGenericRecord(data))
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
