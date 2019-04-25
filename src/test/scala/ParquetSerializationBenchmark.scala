import java.nio.file.{Files, Paths}

import com.sksamuel.avro4s.{AvroSchema, RecordFormat}
import org.apache.avro.generic.GenericRecord
import org.apache.hadoop.fs.Path
import org.apache.parquet.avro.AvroParquetWriter
import org.apache.parquet.hadoop.{ParquetFileWriter, ParquetWriter}
import org.apache.parquet.hadoop.metadata.CompressionCodecName
import org.apache.parquet.thrift.ThriftParquetWriter
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{Data, DataUtils}

object ParquetSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("50000.csv")
  val compression = Gen.enumeration("compression")(
    CompressionCodecName.UNCOMPRESSED,
    CompressionCodecName.SNAPPY,
    CompressionCodecName.GZIP
  )
  val format = RecordFormat[Data]
  val schema = AvroSchema[Data]


  performance of "parquet serialization" in {
    measure method "parquet-avro serialize" in {
      using(Gen.crossProduct(gen, compression)) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val in = DataUtils.readCsv(file._1)
        val parquetWriter: ParquetWriter[GenericRecord] = AvroParquetWriter.builder[GenericRecord](new Path(s"file://${System.getProperty("user.dir")}/parquetAvroSerialization${file._2.name()}.out"))
          .withSchema(schema)
          .enableDictionaryEncoding()
          .enableValidation()
          .withCompressionCodec(file._2)
          .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
          .build()

        in.foreach(rs => {
          rs.foreach(data => {
            parquetWriter.write(format.to(data))
          })
        })

        parquetWriter.close()
        in.close()
      }
    }

    measure method "parquet-thrift serialize" in {
      using(Gen.crossProduct(gen, compression)) setUp { _ =>
        Files.deleteIfExists(Paths.get("parquetThriftSerialization.out"))
      } config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val in = DataUtils.readCsv(file._1)
        val parquetWriter = new ThriftParquetWriter[thriftBenchmark.java.DataThrift](
          new Path(s"file://${System.getProperty("user.dir")}/parquetThriftSerialization${file._2.name()}.out"),
          classOf[thriftBenchmark.java.DataThrift],
          CompressionCodecName.UNCOMPRESSED,
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
