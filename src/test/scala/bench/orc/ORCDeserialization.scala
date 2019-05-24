package bench.orc

import java.nio.charset.StandardCharsets

import bench.Settings
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hive.ql.exec.vector._
import org.apache.orc.{CompressionKind, OrcFile, Reader, TypeDescription}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{Data, MixedData, OnlyLongs, OnlyStrings}

object ORCDeserialization extends Bench.LocalTime {
  @volatile
  var data: Data = _

  val conf: Configuration = new Configuration()

  val compression = Gen.enumeration("compression")(
    CompressionKind.NONE,
    CompressionKind.SNAPPY,
    CompressionKind.ZLIB,
    CompressionKind.LZO,
    CompressionKind.LZ4
  )

  performance of "orc deserialization" in {
    measure method "deserialize - mixed data" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val reader: Reader = OrcFile.createReader(new Path(s"mixedDataOrcSerialization${codec.name()}.orc"),
          OrcFile.readerOptions(conf))

        val rows = reader.rows()
        val batch = reader.getSchema.createRowBatch
        var count = 0

        while (rows.nextBatch(batch)) {
          val f1ColumnVector = batch.cols(0).asInstanceOf[BytesColumnVector]
          val f2ColumnVector = batch.cols(1).asInstanceOf[DoubleColumnVector]
          val f3ColumnVector = batch.cols(2).asInstanceOf[LongColumnVector]
          val f4ColumnVector = batch.cols(3).asInstanceOf[LongColumnVector]
          val f5ColumnVector = batch.cols(4).asInstanceOf[BytesColumnVector]
          val f6ColumnVector = batch.cols(5).asInstanceOf[DoubleColumnVector]
          val f7ColumnVector = batch.cols(6).asInstanceOf[LongColumnVector]
          val f8ColumnVector = batch.cols(7).asInstanceOf[LongColumnVector]
          val f9ColumnVector = batch.cols(8).asInstanceOf[LongColumnVector]
          val f10ColumnVector = batch.cols(9).asInstanceOf[LongColumnVector]
          val f11ColumnVector = batch.cols(10).asInstanceOf[DoubleColumnVector]
          val f12ColumnVector = batch.cols(11).asInstanceOf[DoubleColumnVector]
          val f13ColumnVector = batch.cols(12).asInstanceOf[BytesColumnVector]
          val f14ColumnVector = batch.cols(13).asInstanceOf[BytesColumnVector]
          val f15ColumnVector = batch.cols(14).asInstanceOf[LongColumnVector]
          val f16ColumnVector = batch.cols(15).asInstanceOf[LongColumnVector]
          val f17ColumnVector = batch.cols(16).asInstanceOf[LongColumnVector]
          val f18ColumnVector = batch.cols(17).asInstanceOf[BytesColumnVector]
          val f19ColumnVector = batch.cols(18).asInstanceOf[BytesColumnVector]
          val f20ColumnVector = batch.cols(19).asInstanceOf[BytesColumnVector]

          (0 until batch.size).foreach(i => {
            data = MixedData(
              Option(new String(f1ColumnVector.vector(i), f1ColumnVector.start(i), f1ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(f2ColumnVector.vector(i)),
              Option(f3ColumnVector.vector(i)),
              Option(f4ColumnVector.vector(i).toInt),
              Option(new String(f5ColumnVector.vector(i), f5ColumnVector.start(i), f5ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(f6ColumnVector.vector(i)),
              Option(f7ColumnVector.vector(i)),
              Option(f8ColumnVector.vector(i).toInt),
              Option(f9ColumnVector.vector(i).toInt),
              Option(f10ColumnVector.vector(i)),
              Option(f11ColumnVector.vector(i).toFloat),
              Option(f12ColumnVector.vector(i)),
              Option(new String(f13ColumnVector.vector(i), f13ColumnVector.start(i), f13ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f14ColumnVector.vector(i), f14ColumnVector.start(i), f14ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(f15ColumnVector.vector(i)),
              Option(f16ColumnVector.vector(i).toInt),
              Option(f17ColumnVector.vector(i).toInt),
              Option(new String(f18ColumnVector.vector(i), f18ColumnVector.start(i), f18ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f19ColumnVector.vector(i), f19ColumnVector.start(i), f19ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f20ColumnVector.vector(i), f20ColumnVector.start(i), f20ColumnVector.length(i), StandardCharsets.UTF_8)),
            )

            count += 1
          })
        }

        assert(count == Settings.recordsCount)
        rows.close()
      }
    }

    measure method "deserialize - only strings" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val reader: Reader = OrcFile.createReader(new Path(s"onlyStringsOrcSerialization${codec.name()}.orc"),
          OrcFile.readerOptions(conf))

        val rows = reader.rows()
        val batch = reader.getSchema.createRowBatch
        var count = 0

        while (rows.nextBatch(batch)) {
          val f1ColumnVector = batch.cols(0).asInstanceOf[BytesColumnVector]
          val f2ColumnVector = batch.cols(1).asInstanceOf[BytesColumnVector]
          val f3ColumnVector = batch.cols(2).asInstanceOf[BytesColumnVector]
          val f4ColumnVector = batch.cols(3).asInstanceOf[BytesColumnVector]
          val f5ColumnVector = batch.cols(4).asInstanceOf[BytesColumnVector]
          val f6ColumnVector = batch.cols(5).asInstanceOf[BytesColumnVector]
          val f7ColumnVector = batch.cols(6).asInstanceOf[BytesColumnVector]
          val f8ColumnVector = batch.cols(7).asInstanceOf[BytesColumnVector]
          val f9ColumnVector = batch.cols(8).asInstanceOf[BytesColumnVector]
          val f10ColumnVector = batch.cols(9).asInstanceOf[BytesColumnVector]
          val f11ColumnVector = batch.cols(10).asInstanceOf[BytesColumnVector]
          val f12ColumnVector = batch.cols(11).asInstanceOf[BytesColumnVector]
          val f13ColumnVector = batch.cols(12).asInstanceOf[BytesColumnVector]
          val f14ColumnVector = batch.cols(13).asInstanceOf[BytesColumnVector]
          val f15ColumnVector = batch.cols(14).asInstanceOf[BytesColumnVector]
          val f16ColumnVector = batch.cols(15).asInstanceOf[BytesColumnVector]
          val f17ColumnVector = batch.cols(16).asInstanceOf[BytesColumnVector]
          val f18ColumnVector = batch.cols(17).asInstanceOf[BytesColumnVector]
          val f19ColumnVector = batch.cols(18).asInstanceOf[BytesColumnVector]
          val f20ColumnVector = batch.cols(19).asInstanceOf[BytesColumnVector]

          (0 until batch.size).foreach(i => {
            data = OnlyStrings(
              Option(new String(f1ColumnVector.vector(i), f1ColumnVector.start(i), f1ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f2ColumnVector.vector(i), f2ColumnVector.start(i), f2ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f3ColumnVector.vector(i), f3ColumnVector.start(i), f3ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f4ColumnVector.vector(i), f4ColumnVector.start(i), f4ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f5ColumnVector.vector(i), f5ColumnVector.start(i), f5ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f6ColumnVector.vector(i), f6ColumnVector.start(i), f6ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f7ColumnVector.vector(i), f7ColumnVector.start(i), f7ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f8ColumnVector.vector(i), f8ColumnVector.start(i), f8ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f9ColumnVector.vector(i), f9ColumnVector.start(i), f9ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f10ColumnVector.vector(i), f10ColumnVector.start(i), f10ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f11ColumnVector.vector(i), f11ColumnVector.start(i), f11ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f12ColumnVector.vector(i), f12ColumnVector.start(i), f12ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f13ColumnVector.vector(i), f13ColumnVector.start(i), f13ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f14ColumnVector.vector(i), f14ColumnVector.start(i), f14ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f15ColumnVector.vector(i), f15ColumnVector.start(i), f15ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f16ColumnVector.vector(i), f16ColumnVector.start(i), f16ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f17ColumnVector.vector(i), f17ColumnVector.start(i), f17ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f18ColumnVector.vector(i), f18ColumnVector.start(i), f18ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f19ColumnVector.vector(i), f19ColumnVector.start(i), f19ColumnVector.length(i), StandardCharsets.UTF_8)),
              Option(new String(f20ColumnVector.vector(i), f20ColumnVector.start(i), f20ColumnVector.length(i), StandardCharsets.UTF_8))
            )

            count += 1
          })
        }

        assert(count == Settings.recordsCount)
        rows.close()
      }
    }

    measure method "deserialize - only longs" in {
      using(compression) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { codec =>
        val reader: Reader = OrcFile.createReader(new Path(s"onlyLongsOrcSerialization${codec.name()}.orc"),
          OrcFile.readerOptions(conf))

        val rows = reader.rows()
        val batch = reader.getSchema.createRowBatch
        var count = 0

        while (rows.nextBatch(batch)) {
          val f1ColumnVector = batch.cols(0).asInstanceOf[LongColumnVector]
          val f2ColumnVector = batch.cols(1).asInstanceOf[LongColumnVector]
          val f3ColumnVector = batch.cols(2).asInstanceOf[LongColumnVector]
          val f4ColumnVector = batch.cols(3).asInstanceOf[LongColumnVector]
          val f5ColumnVector = batch.cols(4).asInstanceOf[LongColumnVector]
          val f6ColumnVector = batch.cols(5).asInstanceOf[LongColumnVector]
          val f7ColumnVector = batch.cols(6).asInstanceOf[LongColumnVector]
          val f8ColumnVector = batch.cols(7).asInstanceOf[LongColumnVector]
          val f9ColumnVector = batch.cols(8).asInstanceOf[LongColumnVector]
          val f10ColumnVector = batch.cols(9).asInstanceOf[LongColumnVector]
          val f11ColumnVector = batch.cols(10).asInstanceOf[LongColumnVector]
          val f12ColumnVector = batch.cols(11).asInstanceOf[LongColumnVector]
          val f13ColumnVector = batch.cols(12).asInstanceOf[LongColumnVector]
          val f14ColumnVector = batch.cols(13).asInstanceOf[LongColumnVector]
          val f15ColumnVector = batch.cols(14).asInstanceOf[LongColumnVector]
          val f16ColumnVector = batch.cols(15).asInstanceOf[LongColumnVector]
          val f17ColumnVector = batch.cols(16).asInstanceOf[LongColumnVector]
          val f18ColumnVector = batch.cols(17).asInstanceOf[LongColumnVector]
          val f19ColumnVector = batch.cols(18).asInstanceOf[LongColumnVector]
          val f20ColumnVector = batch.cols(19).asInstanceOf[LongColumnVector]

          (0 until batch.size).foreach(i => {
            data = OnlyLongs(
              Option(f1ColumnVector.vector(i)),
              Option(f2ColumnVector.vector(i)),
              Option(f3ColumnVector.vector(i)),
              Option(f4ColumnVector.vector(i)),
              Option(f5ColumnVector.vector(i)),
              Option(f6ColumnVector.vector(i)),
              Option(f7ColumnVector.vector(i)),
              Option(f8ColumnVector.vector(i)),
              Option(f9ColumnVector.vector(i)),
              Option(f10ColumnVector.vector(i)),
              Option(f11ColumnVector.vector(i)),
              Option(f12ColumnVector.vector(i)),
              Option(f13ColumnVector.vector(i)),
              Option(f14ColumnVector.vector(i)),
              Option(f15ColumnVector.vector(i)),
              Option(f16ColumnVector.vector(i)),
              Option(f17ColumnVector.vector(i)),
              Option(f18ColumnVector.vector(i)),
              Option(f19ColumnVector.vector(i)),
              Option(f20ColumnVector.vector(i))
            )

            count += 1
          })
        }

        assert(count == Settings.recordsCount)
        rows.close()
      }
    }
  }
}

