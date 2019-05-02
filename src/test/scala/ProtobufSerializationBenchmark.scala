import java.io._
import java.nio.ByteBuffer

import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.{SnappyInputStream, SnappyOutputStream}
import project.DataUtils
import InitialDataGenerator.recordsCount


object ProtobufSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")

  performance of "protobuf serialization" in {
    measure method "serialize" in {
      using(gen) config (
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("protobufSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            val o = DataUtils.dataToScalaProtobuf(data).toByteArray
            val length = o.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(o)
            i += 1

            if (i == 1000) {out.flush(); i = 0}
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }

    measure method "serialize - snappy compression" in {
      using(gen) config (
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new SnappyOutputStream(new FileOutputStream(new File("protobufSerializationSnappy.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            val o = DataUtils.dataToScalaProtobuf(data).toByteArray
            val length = o.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(o)
            i += 1

            if (i == 1000) {out.flush(); i = 0}
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }
  }

  val protobufInput = Gen.single("input")("protobufSerialization.out")
  val protobufInputSnappy = Gen.single("input")("protobufSerializationSnappy.out")

  performance of "protobuf deserialization" in {
    measure method "deserialize" in {
      using(protobufInput) config (
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val in = new BufferedInputStream(new FileInputStream(new File(file)))
        var i = 0

        in.mark(1)
        while (in.read() != -1) {
          in.reset()
          val lengthBytes = new Array[Byte](4)
          in.read(lengthBytes)
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          in.read(buffer)
          val obj = protobufBenchmark.data.Data.parseFrom(buffer)
          i += 1
          in.mark(1)
        }

        in.close()
        assert(i == recordsCount)
      }
    }

    measure method "deserialize - snappy" in {
      using(protobufInputSnappy) config (
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val in = new SnappyInputStream(new FileInputStream(new File(file)))
        var i = 0

        while (in.available() > 0) {
          val lengthBytes = new Array[Byte](4)
          in.read(lengthBytes)
          val length = ByteBuffer.wrap(lengthBytes).getInt
          val buffer = new Array[Byte](length)
          in.read(buffer)
          val obj = protobufBenchmark.data.Data.parseFrom(buffer)
          i += 1
        }

        in.close()
        assert(i == recordsCount)
      }
    }
  }
}
