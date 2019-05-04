import java.io._
import java.nio.ByteBuffer

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.JsonMethods.mapper
import org.json4s.jackson.Serialization._
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import org.xerial.snappy._
import project.{Data, DataUtils}
import InitialDataGenerator.recordsCount

import scala.util.Try

object JsonSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")
  implicit val noTypeHintsFormat = Serialization.formats(NoTypeHints)

  performance of "json serialization" in {
    measure method "serialize" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("jsonSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            val bytes = mapper.writeValueAsBytes(write[Data](data))
            val length = bytes.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(bytes)

            i += 1

            if (i == 1000) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }

    measure method "serialize using snappy compression - snappy output stream" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new SnappyOutputStream(new FileOutputStream(new File("jsonSerializationSnappyCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            val bytes = mapper.writeValueAsBytes(write[Data](data))
            val length = bytes.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(bytes)

            i += 1

            if (i == 1000) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }

    measure method "serialize using snappy compression - snappy framed output stream" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new SnappyFramedOutputStream(new FileOutputStream(new File("jsonSerializationSnappyFramedCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            val bytes = mapper.writeValueAsBytes(write[Data](data))
            val length = bytes.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(bytes)
            i += 1

            if (i == 1000) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }

    measure method "serialize using snappy compression - snappy compress" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("jsonSerializationSnappySeparateCompressCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            val bytes = Snappy.compress(mapper.writeValueAsBytes(write[Data](data)))
            val length = bytes.length

            out.write(ByteBuffer.allocate(4).putInt(length).array())
            out.write(bytes)

            i += 1

            if (i == 1000) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }
  }

  val jsonInput = Gen.single("input")("jsonSerialization.out")
  val jsonInputSnappy = Gen.single("input")("jsonSerializationSnappyCompression.out")

  performance of "json deserialization" in {
    measure method "deserialize" in {
      using(jsonInput) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val in = new BufferedInputStream(new FileInputStream(new File(file)))
        var i = 0

        Try {
          while(true) {
            val lengthBytes = new Array[Byte](4)
            in.read(lengthBytes)
            val length = ByteBuffer.wrap(lengthBytes).getInt
            val data = new Array[Byte](length)
            in.read(data)
            val obj = read[Data](mapper.readTree(data).asText())
            i += 1
          }
        }

        in.close()
        assert(i == recordsCount)
      }
    }

    measure method "deserialize - snappy" in {
      using(jsonInputSnappy) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val in = new SnappyInputStream(new FileInputStream(new File(file)))
        var i = 0

        Try {
          while(true) {
            val lengthBytes = new Array[Byte](4)
            in.read(lengthBytes)
            val length = ByteBuffer.wrap(lengthBytes).getInt
            val data = new Array[Byte](length)
            in.read(data)
            val obj = read[Data](mapper.readTree(data).asText())

            i += 1
          }
        }

        in.close()
        assert(i == recordsCount)
      }
    }
  }
}
