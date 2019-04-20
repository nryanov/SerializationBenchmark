import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.nio.charset.StandardCharsets

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{Data, DataUtils}

object JsonSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("50000.csv")
  implicit val noTypeHintsFormat = Serialization.formats(NoTypeHints)

  performance of "json serialization" in {
    measure method "serialize" in {
      using(gen) config (
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("jsonSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            out.write(write[Data](data).getBytes(StandardCharsets.UTF_8))
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
}
