package bench.msgpack

import java.io.{BufferedOutputStream, File, FileOutputStream, ObjectOutputStream}
import java.nio.ByteBuffer
import java.util.zip.GZIPOutputStream

import bench.Settings
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.DataUtils
import org.msgpack.core.MessagePack
import org.xerial.snappy.SnappyOutputStream

object MsgpackSerialization extends Bench.LocalTime {
  val gen = Gen.single("input file")("input.csv")

  performance of "msgpack serialization" in {
    measure method "serialize" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new BufferedOutputStream(new FileOutputStream(new File("msgpackSerialization.out")))
        val packer = MessagePack.newDefaultBufferPacker
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            DataUtils.msgpackData(data, packer)
            val d = packer.toByteArray

            out.write(ByteBuffer.allocate(4).putInt(d.length).array())
            out.write(d)

            packer.clear()
            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        packer.close()
        in.close()
      }
    }

    measure method "serialize - snappy" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new SnappyOutputStream(new FileOutputStream(new File("msgpackSerializationSnappy.out")))
        val packer = MessagePack.newDefaultBufferPacker
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            DataUtils.msgpackData(data, packer)
            val d = packer.toByteArray

            out.write(ByteBuffer.allocate(4).putInt(d.length).array())
            out.write(d)

            packer.clear()
            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        packer.close()
        in.close()
      }
    }

    measure method "serialize - gzip" in {
      using(gen) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val out = new GZIPOutputStream(new FileOutputStream(new File("msgpackSerializationGzip.out")))
        val packer = MessagePack.newDefaultBufferPacker
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            DataUtils.msgpackData(data, packer)
            val d = packer.toByteArray

            out.write(ByteBuffer.allocate(4).putInt(d.length).array())
            out.write(d)

            packer.clear()
            i += 1

            if (i == Settings.flushInterval) {
              out.flush()
              i = 0
            }
          })
        })

        out.flush()
        out.close()
        packer.close()
        in.close()
      }
    }
  }
}
