import org.apache.avro.generic.GenericData
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.parquet.avro.{AvroParquetReader, AvroParquetWriter}
import org.apache.parquet.hadoop.{ParquetFileWriter, ParquetReader, ParquetWriter}
import org.apache.parquet.hadoop.metadata.CompressionCodecName
import org.apache.parquet.thrift.{ThriftParquetReader, ThriftParquetWriter}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.DataUtils
import InitialDataGenerator.recordsCount
import org.apache.parquet.hadoop.util.HadoopInputFile

object ParquetSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")
  val compression = Gen.enumeration("compression")(
    CompressionCodecName.UNCOMPRESSED,
    CompressionCodecName.SNAPPY,
    CompressionCodecName.GZIP
  )

  performance of "parquet serialization" in {
    measure method "parquet-avro serialize" in {
      using(Gen.crossProduct(gen, compression)) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
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
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
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

  val avroInput = Gen.enumeration("input")(
    "parquetAvroSerializationGZIP.out",
    "parquetAvroSerializationSNAPPY.out",
    "parquetAvroSerializationUNCOMPRESSED.out"
  )

  val thriftInput = Gen.enumeration("input")(
    "parquetThriftSerializationGZIP.out",
    "parquetThriftSerializationSNAPPY.out",
    "parquetThriftSerializationUNCOMPRESSED.out"
  )

  performance of "parquet deserialization" in {
    measure method "deserialize-avro" in {
      using(avroInput) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val inputFile = new Path(s"file://${System.getProperty("user.dir")}/$file")
        val in = AvroParquetReader.builder[GenericData.Record](HadoopInputFile.fromPath(inputFile, new Configuration()))
          .withDataModel(GenericData.get())
          .build()

        var i = 0
        var next = in.read()

        while(next != null) {
          i += 1
          next = in.read()
        }

        in.close()
        assert(i == recordsCount)
      }
    }

    measure method "deserialize-thrift" in {
      using(thriftInput) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val inputFile = new Path(s"file://${System.getProperty("user.dir")}/$file")
        val in: ParquetReader[thriftBenchmark.java.DataThrift] = ThriftParquetReader.build[thriftBenchmark.java.DataThrift](inputFile)
          .withThriftClass(classOf[thriftBenchmark.java.DataThrift])
          .build()

        var i = 0
        var next = in.read()

        while(next != null) {
          i += 1
          next = in.read()
        }

        in.close()
        assert(i == recordsCount)
      }
    }
  }
}
