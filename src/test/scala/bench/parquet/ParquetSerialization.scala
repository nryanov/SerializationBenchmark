package bench.parquet

import org.apache.avro.generic.GenericData
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.parquet.avro.AvroParquetWriter
import org.apache.parquet.hadoop.{ParquetFileWriter, ParquetWriter}
import org.apache.parquet.hadoop.metadata.CompressionCodecName
import org.apache.parquet.thrift.ThriftParquetWriter
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.DataUtils
import bench.Settings

object ParquetSerialization extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")
  val compression = Gen.enumeration("compression")(
    CompressionCodecName.UNCOMPRESSED,
    CompressionCodecName.SNAPPY,
    CompressionCodecName.GZIP
  )

  performance of "parquet serialization" in {
    measure method "parquet-avro serialize" in {
      using(Gen.crossProduct(gen, compression)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = DataUtils.readCsv(file._1)
        val parquetWriter: ParquetWriter[GenericData.Record] = AvroParquetWriter.builder[GenericData.Record](new Path(s"file://${System.getProperty("user.dir")}/parquetAvroSerialization${file._2.name()}.out"))
          .withSchema(DataUtils.schema)
          .enableValidation()
          .enableDictionaryEncoding()
          .withCompressionCodec(file._2)
          .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
          .build()

        in.foreach(rs => {
          rs.foreach(data => {
            parquetWriter.write(DataUtils.dataToGenericRecord(data))
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
        val in = DataUtils.readCsv(file._1)
        val out = new Path(s"file://${System.getProperty("user.dir")}/parquetThriftSerialization${file._2.name()}.out")
        val fs = FileSystem.get(new Configuration())
        if (fs.exists(out)) {
          fs.delete(out, false)
        }

        val parquetWriter = new ThriftParquetWriter[thriftBenchmark.java.DataThrift](
          out,
          classOf[thriftBenchmark.java.DataThrift],
          file._2,
          ParquetWriter.DEFAULT_BLOCK_SIZE,
          ParquetWriter.DEFAULT_PAGE_SIZE,
          true,
          true
        )

        in.foreach(rs => {
          rs.foreach(data => {
            parquetWriter.write(DataUtils.dataToJavaThrift(data))
          })
        })

        parquetWriter.close()
        in.close()
      }
    }
  }
}
