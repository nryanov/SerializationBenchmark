import java.io.{BufferedOutputStream, File, FileInputStream, FileOutputStream}

import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol}
import org.apache.thrift.transport.{TFileTransport, TIOStreamTransport, TMemoryBuffer, TMemoryInputTransport}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{Data, DataUtils}
import thriftBenchmark.DataThrift

object ThriftSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("50000.csv")

  def dataToThrift(data: Data): DataThrift = DataThrift(
    data.f1, data.f2, data.f3, data.f4, data.f5, data.f6, data.f7,
    data.f8, data.f9, data.f10, data.f11.map(_.toDouble), data.f12, data.f13,
    data.f14, data.f15, data.f16, data.f17, data.f18, data.f19, data.f20
  )

  performance of "thrift serialization" in {
    measure method "serialize using binary protocol" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("binaryThriftSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        val protocolFactory: TBinaryProtocol.Factory = new TBinaryProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            DataThrift.encode(dataToThrift(data), protocol)

            out.write(buffer.getArray)
            buffer.close()

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

    measure method "serialize using compact protocol" in {
      using(gen) config(
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("compactThriftSerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        val protocolFactory: TCompactProtocol.Factory = new TCompactProtocol.Factory()

        in.foreach(rs => {
          rs.foreach(data => {
            val buffer = new TMemoryBuffer(64)
            val protocol = protocolFactory.getProtocol(buffer)
            DataThrift.encode(dataToThrift(data), protocol)

            out.write(buffer.getArray)
            buffer.close()

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

