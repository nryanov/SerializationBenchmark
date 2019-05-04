package bench.java

import java.io._

import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.Data
import org.xerial.snappy._
import java.util.zip.GZIPInputStream

import bench.Settings


object JavaDeserialization extends Bench.LocalTime {
  val javaFile = Gen.single("input file")("javaSerialization.out")
  val javaFileSnappy = Gen.single("input file")("javaSerializationSnappyCompression.out")
  val javaFileGzip = Gen.single("input file")("javaSerializationGzipCompression.out")

  performance of "java deserialization" in {
    measure method "deserialize" in {
      using(javaFile) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new ObjectInputStream(new FileInputStream(new File(file)))
        var i = 0

        var next = in.readObject()
        while (next != null) {
          i += 1
          val data = next.asInstanceOf[Data]
          next = in.readObject()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }
    }

    measure method "deserialize - snappy" in {
      using(javaFileSnappy) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new ObjectInputStream(new SnappyInputStream(new FileInputStream(new File(file))))
        var i = 0

        var next = in.readObject()
        while (next != null) {
          i += 1
          val data = next.asInstanceOf[Data]
          next = in.readObject()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }
    }

    measure method "deserialize - gzip" in {
      using(javaFileGzip) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(new File(file))))
        var i = 0

        var next = in.readObject()
        while (next != null) {
          i += 1
          val data = next.asInstanceOf[Data]
          next = in.readObject()
        }

        assert(i == Settings.recordsCount)
        in.close()
      }
    }
  }
}
