package project

import java.io.File
import java.util.UUID

import kantan.csv._
import kantan.csv.ops._
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}

import scala.util.Random


object DataUtils {
  def toCsvFile(path: String, count: Int): Unit = {
    implicit val dataEncoder: RowEncoder[Data] = RowEncoder.ordered((d: Data) => (d.f1, d.f2, d.f3, d.f4, d.f5, d.f6, d.f7, d.f8, d.f9,
    d.f10, d.f11, d.f12, d.f13, d.f14, d.f15, d.f16, d.f17, d.f18, d.f19, d.f20))
    implicit val headerEncoder: HeaderEncoder[Data] = HeaderEncoder.defaultHeaderEncoder[Data]
    val writer: CsvWriter[Data] = new File(path).asCsvWriter[Data](rfc.withoutHeader)

    (0 to count).foreach(_ => {
      writer.write(generate())
    })

    writer.close()
  }

  def readCsv(path: String): CsvReader[ReadResult[Data]] = {
    implicit val dataDecoder: RowDecoder[Data] = RowDecoder.ordered(Data.apply _)
    implicit val headerDecoder: HeaderDecoder[Data] = HeaderDecoder.defaultHeaderDecoder[Data]
    val reader = new File(path).asCsvReader[Data](rfc.withoutHeader)

    reader
  }

  def generate(): Data = Data(
    f1 = Some(UUID.randomUUID().toString),
    f2 = Some(Random.nextDouble()),
    f3 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f4 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(r)},
    f5 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(UUID.randomUUID().toString)},
    f6 = Some(Random.nextDouble()),
    f7 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f8 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(r)},
    f9 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(r)},
    f10 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f11 = Some(Random.nextFloat()),
    f12 = Some(Random.nextDouble()),
    f13 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(UUID.randomUUID().toString)},
    f14 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(UUID.randomUUID().toString)},
    f15 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f16 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(r.toShort)},
    f17 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(r)},
    f18 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(UUID.randomUUID().toString)},
    f19 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(UUID.randomUUID().toString)},
    f20 = {val r = Random.nextInt(); if (r % 2 == 0) None else Some(UUID.randomUUID().toString)}
  )

  def dataToGenericRecord(data: Data, schema: Schema): GenericRecord = {
    def valueOrNull[A](opt: Option[A]): Any = opt match {
      case Some(v) => v
      case None => null
    }

    val record = new GenericData.Record(schema)

    record.put("f1", valueOrNull(data.f1))
    record.put("f2", valueOrNull(data.f2))
    record.put("f3", valueOrNull(data.f3))
    record.put("f4", valueOrNull(data.f4))
    record.put("f5", valueOrNull(data.f5))
    record.put("f6", valueOrNull(data.f6))
    record.put("f7", valueOrNull(data.f7))
    record.put("f8", valueOrNull(data.f8))
    record.put("f9", valueOrNull(data.f9))
    record.put("f10", valueOrNull(data.f10))
    record.put("f11", valueOrNull(data.f11))
    record.put("f12", valueOrNull(data.f12))
    record.put("f13", valueOrNull(data.f13))
    record.put("f14", valueOrNull(data.f14))
    record.put("f15", valueOrNull(data.f15))
    record.put("f16", valueOrNull(data.f16))
    record.put("f17", valueOrNull(data.f17))
    record.put("f18", valueOrNull(data.f18))
    record.put("f19", valueOrNull(data.f19))
    record.put("f20", valueOrNull(data.f20))

    record
  }

  def dataToScalaThrift(data: Data): thriftBenchmark.scala.DataThrift = thriftBenchmark.scala.DataThrift(
    data.f1, data.f2, data.f3, data.f4, data.f5, data.f6, data.f7,
    data.f8, data.f9, data.f10, data.f11.map(_.toDouble), data.f12, data.f13,
    data.f14, data.f15, data.f16, data.f17, data.f18, data.f19, data.f20
  )

  def dataToJavaThrift(data: Data): thriftBenchmark.java.DataThrift = {
    val r = new thriftBenchmark.java.DataThrift()

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

  def dataToScalaProtobuf(data: Data): protobufBenchmark.data.Data = protobufBenchmark.data.Data(
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
}
