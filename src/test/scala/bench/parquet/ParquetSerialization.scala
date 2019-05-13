package bench.parquet

import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.parquet.avro.AvroParquetWriter
import org.apache.parquet.hadoop.{ParquetFileWriter, ParquetWriter}
import org.apache.parquet.hadoop.metadata.CompressionCodecName
import org.apache.parquet.thrift.ThriftParquetWriter
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{DataUtils, MixedData}
import bench.Settings
import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.Schema
import project.Implicits._

object ParquetSerialization extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")
  val compression = Gen.enumeration("compression")(
    CompressionCodecName.UNCOMPRESSED,
    CompressionCodecName.SNAPPY,
    CompressionCodecName.GZIP
  )

  val mixedDataschema: Schema = AvroSchema[MixedData]

  performance of "parquet serialization" in {
    measure method "parquet-avro serialize" in {
      using(Gen.crossProduct(gen, compression)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = DataUtils.readCsv[MixedData](file._1)
        val parquetWriter: ParquetWriter[GenericRecord] = AvroParquetWriter.builder[GenericRecord](new Path(s"file://${System.getProperty("user.dir")}/parquetAvroSerialization${file._2.name()}.out"))
          .withSchema(mixedDataschema)
          .enableValidation()
          .enableDictionaryEncoding()
          .withCompressionCodec(file._2)
          .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
          .build()

        in.foreach(rs => {
          rs.foreach(data => {
            parquetWriter.write(mixedDataOps.toGenericRecord(data))
          })
        })

        parquetWriter.close()
        in.close()
      }
    }

    measure method "parquet-thrift serialize" in {
      using(Gen.crossProduct(gen, compression)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = DataUtils.readCsv[MixedData](file._1)
        val out = new Path(s"file://${System.getProperty("user.dir")}/parquetThriftSerialization${file._2.name()}.out")
        val fs = FileSystem.get(new Configuration())
        if (fs.exists(out)) {
          fs.delete(out, false)
        }

        val parquetWriter = new ThriftParquetWriter[thriftBenchmark.java.MixedData](
          out,
          classOf[thriftBenchmark.java.MixedData],
          file._2,
          ParquetWriter.DEFAULT_BLOCK_SIZE,
          ParquetWriter.DEFAULT_PAGE_SIZE,
          true,
          true
        )

        in.foreach(rs => {
          rs.foreach(data => {
            parquetWriter.write(DataUtils.mixedDataToJavaThrift(data))
          })
        })

        parquetWriter.close()
        in.close()
      }
    }
  }
}
