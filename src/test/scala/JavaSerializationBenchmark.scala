import java.io._

import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{Data, DataUtils}
import org.xerial.snappy.{Snappy, SnappyFramedOutputStream, SnappyOutputStream}


object JavaSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("50000.csv")

  performance of "java serialization" in {
    measure method "serialize" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new ObjectOutputStream(new FileOutputStream(new File("javaSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            out.writeObject(data)
            i += 1

            if (i == 1000) {
              out.flush(); i = 0
            }
          })
        })

        // to avoid catching EOF in deserialization
        out.writeObject(null)

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
        val bytes = new ByteArrayOutputStream()
        val stream = new ObjectOutputStream(bytes)
        val out = new SnappyOutputStream(new FileOutputStream(new File("javaSerializationSnappyCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            stream.writeObject(data)
            stream.flush()

            out.write(bytes.toByteArray)
            bytes.reset()

            i += 1

            if (i == 1000) {
              out.flush(); i = 0
            }
          })
        })

        stream.close()
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
        val bytes = new ByteArrayOutputStream()
        val stream = new ObjectOutputStream(bytes)
        val out = new SnappyFramedOutputStream(new FileOutputStream(new File("javaSerializationSnappyFramedCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            stream.writeObject(data)
            stream.flush()

            out.write(bytes.toByteArray)
            bytes.reset()

            i += 1

            if (i == 1000) {
              out.flush(); i = 0
            }
          })
        })

        stream.close()
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
        val bytes = new ByteArrayOutputStream()
        val stream = new ObjectOutputStream(bytes)
        val out = new BufferedOutputStream(new FileOutputStream(new File("javaSerializationSnappySeparateCompressCompression.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            stream.writeObject(data)
            stream.flush()

            out.write(Snappy.compress(bytes.toByteArray))
            bytes.reset()

            i += 1

            if (i == 1000) {
              out.flush(); i = 0
            }
          })
        })

        stream.close()
        out.flush()
        out.close()
        in.close()
      }
    }
  }

  val javaFile = Gen.single("input file")("javaSerialization.out")

  performance of "java deserialization" in {
    measure method "deserialize" in {
      using(javaFile) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val in = new ObjectInputStream(new FileInputStream(new File(file)))
        var i = 0

        var next = in.readObject()
        while (next != null) {
          val data = next.asInstanceOf[Data]
          next = in.readObject()
          i += 1
        }

        println(i)
        in.close()
      }
    }
  }
}
