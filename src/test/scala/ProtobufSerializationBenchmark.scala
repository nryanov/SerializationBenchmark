import java.io.{BufferedOutputStream, File, FileOutputStream}

import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.DataUtils

object ProtobufSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("50000.csv")

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
            out.write(DataUtils.dataToScalaProtobuf(data).toByteArray)
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
