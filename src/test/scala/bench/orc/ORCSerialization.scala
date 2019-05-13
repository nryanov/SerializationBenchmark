package bench.orc

import java.nio.charset.StandardCharsets

import bench.Settings
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hive.ql.exec.vector._
import org.apache.orc.{CompressionKind, OrcFile, TypeDescription}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{DataUtils, MixedData}
import project.Implicits._

object ORCSerialization extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")
  val compression = Gen.enumeration("compression")(
    CompressionKind.NONE,
    CompressionKind.SNAPPY,
    CompressionKind.ZLIB,
    CompressionKind.LZO,
    CompressionKind.LZ4
  )

  val conf: Configuration = new Configuration()
  val schema: TypeDescription = TypeDescription.createStruct()
    .addField("f1", TypeDescription.createString())
    .addField("f2", TypeDescription.createDouble())
    .addField("f3", TypeDescription.createLong())
    .addField("f4", TypeDescription.createInt())
    .addField("f5", TypeDescription.createString())
    .addField("f6", TypeDescription.createDouble())
    .addField("f7", TypeDescription.createLong())
    .addField("f8", TypeDescription.createInt())
    .addField("f9", TypeDescription.createInt())
    .addField("f10", TypeDescription.createLong())
    .addField("f11", TypeDescription.createFloat())
    .addField("f12", TypeDescription.createDouble())
    .addField("f13", TypeDescription.createString())
    .addField("f14", TypeDescription.createString())
    .addField("f15", TypeDescription.createLong())
    .addField("f16", TypeDescription.createInt())
    .addField("f17", TypeDescription.createInt())
    .addField("f18", TypeDescription.createString())
    .addField("f19", TypeDescription.createString())
    .addField("f20", TypeDescription.createString())

  performance of "orc serialization" in {
    measure method "serialize" in {
      using(Gen.crossProduct(gen, compression)) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { fileCompression =>
        val writer = OrcFile.createWriter(
          new Path(s"orcSerialization${fileCompression._2.name()}.orc"),
          OrcFile.writerOptions(conf).setSchema(schema).overwrite(true).compress(fileCompression._2)
        )
        val batch: VectorizedRowBatch = schema.createRowBatch(1000)

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


        val in = DataUtils.readCsv[MixedData](fileCompression._1)
        in.foreach(rs => {
          rs.foreach(data => {
            val row = batch.size
            batch.size += 1

            f1ColumnVector.setVal(row, data.f1.map(_.getBytes(StandardCharsets.UTF_8)).getOrElse(Array[Byte]()))
            f2ColumnVector.vector(row) = if (data.f2.isEmpty) 0.0 else data.f2.get
            f3ColumnVector.vector(row) = if (data.f3.isEmpty) 0 else data.f3.get
            f4ColumnVector.vector(row) = if (data.f4.isEmpty) 0 else data.f4.get
            f5ColumnVector.setVal(row, data.f5.map(_.getBytes(StandardCharsets.UTF_8)).getOrElse(Array[Byte]()))
            f6ColumnVector.vector(row) = if (data.f6.isEmpty) 0.0 else data.f6.get
            f7ColumnVector.vector(row) = if (data.f7.isEmpty) 0 else data.f7.get
            f8ColumnVector.vector(row) = if (data.f8.isEmpty) 0 else data.f8.get
            f9ColumnVector.vector(row) = if (data.f9.isEmpty) 0 else data.f9.get
            f10ColumnVector.vector(row) = if (data.f10.isEmpty) 0 else data.f10.get
            f11ColumnVector.vector(row) = if (data.f11.isEmpty) 0.0 else data.f11.get
            f12ColumnVector.vector(row) = if (data.f12.isEmpty) 0.0 else data.f12.get
            f13ColumnVector.setVal(row, data.f13.map(_.getBytes(StandardCharsets.UTF_8)).getOrElse(Array[Byte]()))
            f14ColumnVector.setVal(row, data.f14.map(_.getBytes(StandardCharsets.UTF_8)).getOrElse(Array[Byte]()))
            f15ColumnVector.vector(row) = if (data.f15.isEmpty) 0 else data.f15.get
            f16ColumnVector.vector(row) = if (data.f16.isEmpty) 0 else data.f16.get
            f17ColumnVector.vector(row) = if (data.f17.isEmpty) 0 else data.f17.get
            f18ColumnVector.setVal(row, data.f18.map(_.getBytes(StandardCharsets.UTF_8)).getOrElse(Array[Byte]()))
            f19ColumnVector.setVal(row, data.f19.map(_.getBytes(StandardCharsets.UTF_8)).getOrElse(Array[Byte]()))
            f20ColumnVector.setVal(row, data.f20.map(_.getBytes(StandardCharsets.UTF_8)).getOrElse(Array[Byte]()))

            if (batch.size == batch.getMaxSize) {
              writer.addRowBatch(batch)
              batch.reset()
            }
          })
        })

        if (batch.size != 0) {
          writer.addRowBatch(batch)
          batch.reset()
        }

        writer.close()
        in.close()
      }
    }
  }
}

