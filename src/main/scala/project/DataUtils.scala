package project

import java.io.File

import kantan.csv._
import kantan.csv.ops._


object DataUtils {
  def toCsvFile[A <: Data : DataOps : RowEncoder : HeaderEncoder](path: String, count: Int): Unit = {
    val writer: CsvWriter[A] = new File(path).asCsvWriter[A](rfc.withoutHeader)
    val generator = implicitly[DataOps[A]]

    (0 until count).foreach(_ => {
      writer.write(generator.generate())
    })

    writer.close()
  }

  def readCsv[A <: Data : RowDecoder : HeaderDecoder](path: String): CsvReader[ReadResult[A]] = {
    val reader = new File(path).asCsvReader[A](rfc.withoutHeader)

    reader
  }

  def mixedDataToScalaThrift(data: MixedData): thriftBenchmark.scala.MixedData = thriftBenchmark.scala.MixedData(
    data.f1, data.f2, data.f3, data.f4, data.f5, data.f6, data.f7,
    data.f8, data.f9, data.f10, data.f11.map(_.toDouble), data.f12, data.f13,
    data.f14, data.f15, data.f16, data.f17, data.f18, data.f19, data.f20
  )

  def onlyStringsToScalaThrift(data: OnlyStrings): thriftBenchmark.scala.OnlyStrings = thriftBenchmark.scala.OnlyStrings(
    data.f1, data.f2, data.f3, data.f4, data.f5, data.f6, data.f7,
    data.f8, data.f9, data.f10, data.f11, data.f12, data.f13,
    data.f14, data.f15, data.f16, data.f17, data.f18, data.f19, data.f20
  )

  def onlyLongsToScalaThrift(data: OnlyLongs): thriftBenchmark.scala.OnlyLongs = thriftBenchmark.scala.OnlyLongs(
    data.f1, data.f2, data.f3, data.f4, data.f5, data.f6, data.f7,
    data.f8, data.f9, data.f10, data.f11, data.f12, data.f13,
    data.f14, data.f15, data.f16, data.f17, data.f18, data.f19, data.f20
  )

  def mixedDataToJavaThrift(data: MixedData): thriftBenchmark.java.MixedData = {
    val r = new thriftBenchmark.java.MixedData()

    r.f1 = data.f1.orNull
    r.f2 = data.f2.getOrElse(0.0)
    r.f3 = data.f3.getOrElse(0)
    r.f4 = data.f4.getOrElse(0)
    r.f5 = data.f5.orNull
    r.f6 = data.f6.getOrElse(0.0)
    r.f7 = data.f7.getOrElse(0)
    r.f8 = data.f8.getOrElse(0)
    r.f9 = data.f9.getOrElse(0)
    r.f10 = data.f10.getOrElse(0)
    r.f11 = data.f11.map(_.toDouble).getOrElse(0.0)
    r.f12 = data.f12.getOrElse(0.0)
    r.f13 = data.f13.orNull
    r.f14 = data.f14.orNull
    r.f15 = data.f15.getOrElse(0)
    r.f16 = data.f16.getOrElse(0)
    r.f17 = data.f17.getOrElse(0)
    r.f18 = data.f18.orNull
    r.f19 = data.f19.orNull
    r.f20 = data.f20.orNull

    r
  }

  def onlyStringsToJavaThrift(data: OnlyStrings): thriftBenchmark.java.OnlyStrings = {
    val r = new thriftBenchmark.java.OnlyStrings()

    r.f1 = data.f1.orNull
    r.f2 = data.f2.orNull
    r.f3 = data.f3.orNull
    r.f4 = data.f4.orNull
    r.f5 = data.f5.orNull
    r.f6 = data.f6.orNull
    r.f7 = data.f7.orNull
    r.f8 = data.f8.orNull
    r.f9 = data.f9.orNull
    r.f10 = data.f10.orNull
    r.f11 = data.f11.orNull
    r.f12 = data.f12.orNull
    r.f13 = data.f13.orNull
    r.f14 = data.f14.orNull
    r.f15 = data.f15.orNull
    r.f16 = data.f16.orNull
    r.f17 = data.f17.orNull
    r.f18 = data.f18.orNull
    r.f19 = data.f19.orNull
    r.f20 = data.f20.orNull

    r
  }

  def onlyLongsToJavaThrift(data: OnlyLongs): thriftBenchmark.java.OnlyLongs = {
    val r = new thriftBenchmark.java.OnlyLongs()

    r.f1 = data.f1.getOrElse(0)
    r.f2 = data.f2.getOrElse(0)
    r.f3 = data.f3.getOrElse(0)
    r.f4 = data.f4.getOrElse(0)
    r.f5 = data.f5.getOrElse(0)
    r.f6 = data.f6.getOrElse(0)
    r.f7 = data.f7.getOrElse(0)
    r.f8 = data.f8.getOrElse(0)
    r.f9 = data.f9.getOrElse(0)
    r.f10 = data.f10.getOrElse(0)
    r.f11 = data.f11.getOrElse(0)
    r.f12 = data.f12.getOrElse(0)
    r.f13 = data.f13.getOrElse(0)
    r.f14 = data.f14.getOrElse(0)
    r.f15 = data.f15.getOrElse(0)
    r.f16 = data.f16.getOrElse(0)
    r.f17 = data.f17.getOrElse(0)
    r.f18 = data.f18.getOrElse(0)
    r.f19 = data.f19.getOrElse(0)
    r.f20 = data.f20.getOrElse(0)

    r
  }

  def mixedDataToScalaProtobuf(data: MixedData): protobufBenchmark.data.MixedData = protobufBenchmark.data.MixedData(
    data.f1.getOrElse(""),
    data.f2.getOrElse(0.0),
    data.f3.getOrElse(0),
    data.f4.getOrElse(0),
    data.f5.getOrElse(""),
    data.f6.getOrElse(0.0),
    data.f7.getOrElse(0),
    data.f8.getOrElse(0),
    data.f9.getOrElse(0),
    data.f10.getOrElse(0),
    data.f11.map(_.toDouble).getOrElse(0.0),
    data.f12.getOrElse(0.0),
    data.f13.getOrElse(""),
    data.f14.getOrElse(""),
    data.f15.getOrElse(0),
    data.f16.getOrElse(0),
    data.f17.getOrElse(0),
    data.f18.getOrElse(""),
    data.f19.getOrElse(""),
    data.f20.getOrElse(""),
  )

  def onlyStringsToScalaProtobufdata(data: OnlyStrings): protobufBenchmark.data.OnlyStrings = protobufBenchmark.data.OnlyStrings(
    data.f1.getOrElse(""),
    data.f2.getOrElse(""),
    data.f3.getOrElse(""),
    data.f4.getOrElse(""),
    data.f5.getOrElse(""),
    data.f6.getOrElse(""),
    data.f7.getOrElse(""),
    data.f8.getOrElse(""),
    data.f9.getOrElse(""),
    data.f10.getOrElse(""),
    data.f11.getOrElse(""),
    data.f12.getOrElse(""),
    data.f13.getOrElse(""),
    data.f14.getOrElse(""),
    data.f15.getOrElse(""),
    data.f16.getOrElse(""),
    data.f17.getOrElse(""),
    data.f18.getOrElse(""),
    data.f19.getOrElse(""),
    data.f20.getOrElse(""),
  )

  def onlyLongsToScalaProtobuf(data: OnlyLongs): protobufBenchmark.data.OnlyLongs = protobufBenchmark.data.OnlyLongs(
    data.f1.getOrElse(0),
    data.f2.getOrElse(0),
    data.f3.getOrElse(0),
    data.f4.getOrElse(0),
    data.f5.getOrElse(0),
    data.f6.getOrElse(0),
    data.f7.getOrElse(0),
    data.f8.getOrElse(0),
    data.f9.getOrElse(0),
    data.f10.getOrElse(0),
    data.f11.getOrElse(0),
    data.f12.getOrElse(0),
    data.f13.getOrElse(0),
    data.f14.getOrElse(0),
    data.f15.getOrElse(0),
    data.f16.getOrElse(0),
    data.f17.getOrElse(0),
    data.f18.getOrElse(0),
    data.f19.getOrElse(0),
    data.f20.getOrElse(0),
  )
}
