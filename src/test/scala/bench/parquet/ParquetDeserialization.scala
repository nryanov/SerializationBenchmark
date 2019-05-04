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
import org.apache.parquet.hadoop.util.HadoopInputFile

object ParquetDeserialization extends Bench.LocalTime {
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
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
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
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize-thrift" in {
      using(thriftInput) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
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
        assert(i == Settings.recordsCount)
      }
    }
  }
}