package bench.java

import java.io._

import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.DataUtils
import org.xerial.snappy._
import java.util.zip.GZIPOutputStream

import bench.Settings


object JavaSerialization extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")

  performance of "java serialization" in {
    measure method "serialize" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new ObjectOutputStream(new FileOutputStream(new File("javaSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            out.writeObject(data)
            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
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

    measure method "serialize - gzip compression" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(new File("javaSerializationGzipCompression.out"))))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            out.writeObject(data)
            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
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
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new ObjectOutputStream(new SnappyOutputStream(new FileOutputStream(new File("javaSerializationSnappyCompression.out"))))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            out.writeObject(data)
            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
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
  }
}
