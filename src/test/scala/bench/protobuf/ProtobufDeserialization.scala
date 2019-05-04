package bench.protobuf

import java.io._
import java.nio.ByteBuffer

import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy.SnappyInputStream
import bench.Settings

object ProtobufDeserialization extends Bench.LocalTime {
  val protobufInput = Gen.single("input")("protobufSerialization.out")
  val protobufInputSnappy = Gen.single("input")("protobufSerializationSnappy.out")

  performance of "protobuf deserialization" in {
    measure method "deserialize" in {
      using(protobufInput) config (
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
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
        assert(i == Settings.recordsCount)
      }
    }

    measure method "deserialize - snappy" in {
      using(protobufInputSnappy) config (
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
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
        assert(i == Settings.recordsCount)
      }
    }
  }
}
