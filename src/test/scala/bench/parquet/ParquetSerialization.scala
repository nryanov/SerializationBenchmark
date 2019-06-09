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
import project.{DataUtils, MixedData, OnlyLongs, OnlyStrings}
import bench.Settings
import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.Schema
import project.Implicits._

object ParquetSerialization extends Bench.ForkedTime {
  val compression = Gen.enumeration("compression")(
    CompressionCodecName.UNCOMPRESSED,
    CompressionCodecName.SNAPPY,
    CompressionCodecName.GZIP,
  )

  override def aggregator: Aggregator[Double] = Aggregator.average

  val mixedDataschema: Schema = AvroSchema[MixedData]
  val onlyStringsSchema: Schema = AvroSchema[OnlyStrings]
  val onlyLongsSchema: Schema = AvroSchema[OnlyLongs]

  performance of "parquet serialization" in {
    measure method "parquet-avro serialize - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val in = DataUtils.readCsv[MixedData]("mixedDataInput.csv")
        val parquetWriter: ParquetWriter[GenericRecord] = AvroParquetWriter.builder[GenericRecord](new Path(s"file://${System.getProperty("user.dir")}/mixedDataParquetAvroSerialization${gen.name()}.out"))
          .withSchema(mixedDataschema)
          .enableValidation()
          .enableDictionaryEncoding()
          .withCompressionCodec(gen)
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

    measure method "parquet-thrift serialize - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val in = DataUtils.readCsv[MixedData]("mixedDataInput.csv")
        val out = new Path(s"file://${System.getProperty("user.dir")}/mixedDataParquetThriftSerialization${gen.name()}.out")
        val fs = FileSystem.get(new Configuration())
        if (fs.exists(out)) {
          fs.delete(out, false)
        }

        val parquetWriter = new ThriftParquetWriter[thriftBenchmark.java.MixedData](
          out,
          classOf[thriftBenchmark.java.MixedData],
          gen,
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

    measure method "parquet-avro serialize - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val in = DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv")
        val parquetWriter: ParquetWriter[GenericRecord] = AvroParquetWriter.builder[GenericRecord](new Path(s"file://${System.getProperty("user.dir")}/onlyStringsParquetAvroSerialization${gen.name()}.out"))
          .withSchema(onlyStringsSchema)
          .enableValidation()
          .enableDictionaryEncoding()
          .withCompressionCodec(gen)
          .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
          .build()

        in.foreach(rs => {
          rs.foreach(data => {
            parquetWriter.write(onlyStringOps.toGenericRecord(data))
          })
        })

        parquetWriter.close()
        in.close()
      }
    }

    measure method "parquet-thrift serialize - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val in = DataUtils.readCsv[OnlyStrings]("onlyStringsInput.csv")
        val out = new Path(s"file://${System.getProperty("user.dir")}/onlyStringsParquetThriftSerialization${gen.name()}.out")
        val fs = FileSystem.get(new Configuration())
        if (fs.exists(out)) {
          fs.delete(out, false)
        }

        val parquetWriter = new ThriftParquetWriter[thriftBenchmark.java.OnlyStrings](
          out,
          classOf[thriftBenchmark.java.OnlyStrings],
          gen,
          ParquetWriter.DEFAULT_BLOCK_SIZE,
          ParquetWriter.DEFAULT_PAGE_SIZE,
          true,
          true
        )

        in.foreach(rs => {
          rs.foreach(data => {
            parquetWriter.write(DataUtils.onlyStringsToJavaThrift(data))
          })
        })

        parquetWriter.close()
        in.close()
      }
    }

    measure method "parquet-avro serialize - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val in = DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv")
        val parquetWriter: ParquetWriter[GenericRecord] = AvroParquetWriter.builder[GenericRecord](new Path(s"file://${System.getProperty("user.dir")}/onlyLongsParquetAvroSerialization${gen.name()}.out"))
          .withSchema(onlyLongsSchema)
          .enableValidation()
          .enableDictionaryEncoding()
          .withCompressionCodec(gen)
          .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
          .build()

        in.foreach(rs => {
          rs.foreach(data => {
            parquetWriter.write(onlyLongsOps.toGenericRecord(data))
          })
        })

        parquetWriter.close()
        in.close()
      }
    }

    measure method "parquet-thrift serialize - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val in = DataUtils.readCsv[OnlyLongs]("onlyLongsInput.csv")
        val out = new Path(s"file://${System.getProperty("user.dir")}/onlyLongsParquetThriftSerialization${gen.name()}.out")
        val fs = FileSystem.get(new Configuration())
        if (fs.exists(out)) {
          fs.delete(out, false)
        }

        val parquetWriter = new ThriftParquetWriter[thriftBenchmark.java.OnlyLongs](
          out,
          classOf[thriftBenchmark.java.OnlyLongs],
          gen,
          ParquetWriter.DEFAULT_BLOCK_SIZE,
          ParquetWriter.DEFAULT_PAGE_SIZE,
          true,
          true
        )

        in.foreach(rs => {
          rs.foreach(data => {
            parquetWriter.write(DataUtils.onlyLongsToJavaThrift(data))
          })
        })

        parquetWriter.close()
        in.close()
      }
    }
  }
}
