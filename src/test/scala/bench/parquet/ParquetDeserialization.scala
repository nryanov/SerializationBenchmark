package bench.parquet

import org.apache.avro.generic.GenericData
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.parquet.avro.AvroParquetReader
import org.apache.parquet.hadoop.ParquetReader
import org.apache.parquet.thrift.ThriftParquetReader
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import bench.Settings
import org.apache.parquet.hadoop.metadata.CompressionCodecName
import org.apache.parquet.hadoop.util.HadoopInputFile

object ParquetDeserialization extends Bench.LocalTime {
  @volatile
  var data: Any = _

  val compression = Gen.enumeration("compression")(
    CompressionCodecName.UNCOMPRESSED,
    CompressionCodecName.SNAPPY,
    CompressionCodecName.GZIP,
  )

  performance of "parquet deserialization" in {
    measure method "deserialize-avro - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val inputFile = new Path(s"file://${System.getProperty("user.dir")}/mixedDataParquetAvroSerialization${gen.name()}.out")
        val in = AvroParquetReader.builder[GenericData.Record](HadoopInputFile.fromPath(inputFile, new Configuration()))
          .withDataModel(GenericData.get())
          .build()

        var i = 0
        data = in.read()

        while(data != null) {
          i += 1
          data = in.read()
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize-thrift - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val inputFile = new Path(s"file://${System.getProperty("user.dir")}/mixedDataParquetThriftSerialization${gen.name()}.out")
        val in: ParquetReader[thriftBenchmark.java.MixedData] = ThriftParquetReader.build[thriftBenchmark.java.MixedData](inputFile)
          .withThriftClass(classOf[thriftBenchmark.java.MixedData])
          .build()

        var i = 0
        data = in.read()

        while(data != null) {
          i += 1
          data = in.read()
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize-avro - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val inputFile = new Path(s"file://${System.getProperty("user.dir")}/onlyStringsParquetAvroSerialization${gen.name()}.out")
        val in = AvroParquetReader.builder[GenericData.Record](HadoopInputFile.fromPath(inputFile, new Configuration()))
          .withDataModel(GenericData.get())
          .build()

        var i = 0
        data = in.read()

        while(data != null) {
          i += 1
          data = in.read()
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize-thrift - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val inputFile = new Path(s"file://${System.getProperty("user.dir")}/OnlyStringsParquetThriftSerialization${gen.name()}.out")
        val in: ParquetReader[thriftBenchmark.java.OnlyStrings] = ThriftParquetReader.build[thriftBenchmark.java.OnlyStrings](inputFile)
          .withThriftClass(classOf[thriftBenchmark.java.OnlyStrings])
          .build()

        var i = 0
        data = in.read()

        while(data != null) {
          i += 1
          data = in.read()
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize-avro - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val inputFile = new Path(s"file://${System.getProperty("user.dir")}/OnlyLongsParquetAvroSerialization${gen.name()}.out")
        val in = AvroParquetReader.builder[GenericData.Record](HadoopInputFile.fromPath(inputFile, new Configuration()))
          .withDataModel(GenericData.get())
          .build()

        var i = 0
        data = in.read()

        while(data != null) {
          i += 1
          data = in.read()
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize-thrift - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { gen =>
        val inputFile = new Path(s"file://${System.getProperty("user.dir")}/onlyLongsParquetThriftSerialization${gen.name()}.out")
        val in: ParquetReader[thriftBenchmark.java.OnlyLongs] = ThriftParquetReader.build[thriftBenchmark.java.OnlyLongs](inputFile)
          .withThriftClass(classOf[thriftBenchmark.java.OnlyLongs])
          .build()

        var i = 0
        data = in.read()

        while(data != null) {
          i += 1
          data = in.read()
        }

        in.close()
        assert(i == Settings.recordsCount)
      }
    }
  }
}