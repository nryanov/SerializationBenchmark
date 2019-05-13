package project

import java.util.UUID

import com.sksamuel.avro4s.AvroSchema
import kantan.csv.{HeaderDecoder, HeaderEncoder, RowDecoder, RowEncoder}
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.msgpack.core.{MessageBufferPacker, MessageUnpacker}

import scala.util.Random

sealed trait DataOps[A <: Data] {
  final def valueOrNull[A](opt: Option[A]): Any = opt match {
    case Some(v) => v
    case None => null
  }

  def generate(): A

  def toGenericRecord(data: A): GenericRecord

  def msgpack(data: A, buffer: MessageBufferPacker): Unit

  def msgunpack(buffer: MessageUnpacker): A
}

class MixedDataOps extends DataOps[MixedData] {
  val schema: Schema = AvroSchema[MixedData]

  override def generate(): MixedData = MixedData(
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

  override def toGenericRecord(data: MixedData): GenericRecord = {
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

  override def msgpack(data: MixedData, buffer: MessageBufferPacker): Unit = {
    buffer.packString(data.f1.getOrElse(""))
    buffer.packDouble(data.f2.getOrElse(0.0))
    buffer.packLong(data.f3.getOrElse(0))
    buffer.packInt(data.f4.getOrElse(0))
    buffer.packString(data.f5.getOrElse(""))
    buffer.packDouble(data.f6.getOrElse(0.0))
    buffer.packLong(data.f7.getOrElse(0))
    buffer.packInt(data.f8.getOrElse(0))
    buffer.packInt(data.f9.getOrElse(0))
    buffer.packLong(data.f10.getOrElse(0))
    buffer.packFloat(data.f11.getOrElse(0.0f))
    buffer.packDouble(data.f12.getOrElse(0.0))
    buffer.packString(data.f13.getOrElse(""))
    buffer.packString(data.f14.getOrElse(""))
    buffer.packLong(data.f15.getOrElse(0))
    buffer.packInt(data.f16.getOrElse(0))
    buffer.packInt(data.f17.getOrElse(0))
    buffer.packString(data.f18.getOrElse(""))
    buffer.packString(data.f19.getOrElse(""))
    buffer.packString(data.f20.getOrElse(""))
  }

  override def msgunpack(buffer: MessageUnpacker): MixedData = MixedData(
    Option(buffer.unpackString()),
    Option(buffer.unpackDouble()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackInt()),
    Option(buffer.unpackString()),
    Option(buffer.unpackDouble()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackInt()),
    Option(buffer.unpackInt()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackFloat()),
    Option(buffer.unpackDouble()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackInt()),
    Option(buffer.unpackInt()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString())
  )
}

trait MixedDataInstances {
  implicit val mixedDataOps: MixedDataOps = new MixedDataOps
  implicit val mixedDataEncoder: RowEncoder[MixedData] = RowEncoder.ordered((d: MixedData) => (d.f1, d.f2, d.f3, d.f4, d.f5, d.f6, d.f7, d.f8, d.f9,
    d.f10, d.f11, d.f12, d.f13, d.f14, d.f15, d.f16, d.f17, d.f18, d.f19, d.f20))
  implicit val mixedDataHeaderEncoder: HeaderEncoder[MixedData] = HeaderEncoder.defaultHeaderEncoder[MixedData]
  implicit val mixedDataDecoder: RowDecoder[MixedData] = RowDecoder.ordered(MixedData.apply _)
  implicit val mixedDataHeaderDecoder: HeaderDecoder[MixedData] = HeaderDecoder.defaultHeaderDecoder[MixedData]
}

class OnlyStringOps extends DataOps[OnlyStrings] {
  val schema: Schema = AvroSchema[OnlyStrings]

  override def generate(): OnlyStrings = OnlyStrings(
    f1 = Some(UUID.randomUUID().toString),
    f2 = Some(UUID.randomUUID().toString),
    f3 = Some(UUID.randomUUID().toString),
    f4 = Some(UUID.randomUUID().toString),
    f5 = Some(UUID.randomUUID().toString),
    f6 = Some(UUID.randomUUID().toString),
    f7 = Some(UUID.randomUUID().toString),
    f8 = Some(UUID.randomUUID().toString),
    f9 = Some(UUID.randomUUID().toString),
    f10 = Some(UUID.randomUUID().toString),
    f11 = Some(UUID.randomUUID().toString),
    f12 = Some(UUID.randomUUID().toString),
    f13 = Some(UUID.randomUUID().toString),
    f14 = Some(UUID.randomUUID().toString),
    f15 = Some(UUID.randomUUID().toString),
    f16 = Some(UUID.randomUUID().toString),
    f17 = Some(UUID.randomUUID().toString),
    f18 = Some(UUID.randomUUID().toString),
    f19 = Some(UUID.randomUUID().toString),
    f20 = Some(UUID.randomUUID().toString)
  )

  override def toGenericRecord(data: OnlyStrings): GenericRecord = {
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

  override def msgpack(data: OnlyStrings, buffer: MessageBufferPacker): Unit = {
    buffer.packString(data.f1.getOrElse(""))
    buffer.packString(data.f2.getOrElse(""))
    buffer.packString(data.f3.getOrElse(""))
    buffer.packString(data.f4.getOrElse(""))
    buffer.packString(data.f5.getOrElse(""))
    buffer.packString(data.f6.getOrElse(""))
    buffer.packString(data.f7.getOrElse(""))
    buffer.packString(data.f8.getOrElse(""))
    buffer.packString(data.f9.getOrElse(""))
    buffer.packString(data.f10.getOrElse(""))
    buffer.packString(data.f11.getOrElse(""))
    buffer.packString(data.f12.getOrElse(""))
    buffer.packString(data.f13.getOrElse(""))
    buffer.packString(data.f14.getOrElse(""))
    buffer.packString(data.f15.getOrElse(""))
    buffer.packString(data.f16.getOrElse(""))
    buffer.packString(data.f17.getOrElse(""))
    buffer.packString(data.f18.getOrElse(""))
    buffer.packString(data.f19.getOrElse(""))
    buffer.packString(data.f20.getOrElse(""))
  }

  override def msgunpack(buffer: MessageUnpacker): OnlyStrings = OnlyStrings(
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString()),
    Option(buffer.unpackString())
  )
}

trait OnlyStringInstances {
  implicit val onlyStringOps: OnlyStringOps = new OnlyStringOps
  implicit val onlyStringsEncoder: RowEncoder[OnlyStrings] = RowEncoder.ordered((d: OnlyStrings) => (d.f1, d.f2, d.f3, d.f4, d.f5, d.f6, d.f7, d.f8, d.f9,
    d.f10, d.f11, d.f12, d.f13, d.f14, d.f15, d.f16, d.f17, d.f18, d.f19, d.f20))
  implicit val onlyStringsHeaderEncoder: HeaderEncoder[OnlyStrings] = HeaderEncoder.defaultHeaderEncoder[OnlyStrings]
  implicit val onlyStringsDecoder: RowDecoder[OnlyStrings] = RowDecoder.ordered(OnlyStrings.apply _)
  implicit val onlyStringsHeaderDecoder: HeaderDecoder[OnlyStrings] = HeaderDecoder.defaultHeaderDecoder[OnlyStrings]
}

class OnlyLongsOps extends DataOps[OnlyLongs] {
  val schema: Schema = AvroSchema[OnlyLongs]

  override def generate(): OnlyLongs = OnlyLongs(
    f1 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f2 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f3 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f4 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f5 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f6 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f7 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f8 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f9 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f10 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f11 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f12 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f13 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f14 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f15 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f16 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f17 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f18 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f19 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)},
    f20 = {val r = Random.nextLong(); if (r % 2 == 0) None else Some(r)}
  )

  override def toGenericRecord(data: OnlyLongs): GenericRecord = {
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

  override def msgpack(data: OnlyLongs, buffer: MessageBufferPacker): Unit = {
    buffer.packLong(data.f1.getOrElse(0))
    buffer.packLong(data.f2.getOrElse(0))
    buffer.packLong(data.f3.getOrElse(0))
    buffer.packLong(data.f4.getOrElse(0))
    buffer.packLong(data.f5.getOrElse(0))
    buffer.packLong(data.f6.getOrElse(0))
    buffer.packLong(data.f7.getOrElse(0))
    buffer.packLong(data.f8.getOrElse(0))
    buffer.packLong(data.f9.getOrElse(0))
    buffer.packLong(data.f10.getOrElse(0))
    buffer.packLong(data.f11.getOrElse(0))
    buffer.packLong(data.f12.getOrElse(0))
    buffer.packLong(data.f13.getOrElse(0))
    buffer.packLong(data.f14.getOrElse(0))
    buffer.packLong(data.f15.getOrElse(0))
    buffer.packLong(data.f16.getOrElse(0))
    buffer.packLong(data.f17.getOrElse(0))
    buffer.packLong(data.f18.getOrElse(0))
    buffer.packLong(data.f19.getOrElse(0))
    buffer.packLong(data.f20.getOrElse(0))
  }

  override def msgunpack(buffer: MessageUnpacker): OnlyLongs = OnlyLongs(
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong()),
    Option(buffer.unpackLong())
  )
}

trait OnlyLongsInstances {
  implicit val onlyLongsOps: OnlyLongsOps = new OnlyLongsOps
  implicit val onlyLongsDataEncoder: RowEncoder[OnlyLongs] = RowEncoder.ordered((d: OnlyLongs) => (d.f1, d.f2, d.f3, d.f4, d.f5, d.f6, d.f7, d.f8, d.f9,
    d.f10, d.f11, d.f12, d.f13, d.f14, d.f15, d.f16, d.f17, d.f18, d.f19, d.f20))
  implicit val onlyLongsHeaderEncoder: HeaderEncoder[OnlyLongs] = HeaderEncoder.defaultHeaderEncoder[OnlyLongs]
  implicit val onlyLongsDecoder: RowDecoder[OnlyLongs] = RowDecoder.ordered(OnlyLongs.apply _)
  implicit val onlyLongsHeaderDecoder: HeaderDecoder[OnlyLongs] = HeaderDecoder.defaultHeaderDecoder[OnlyLongs]
}
