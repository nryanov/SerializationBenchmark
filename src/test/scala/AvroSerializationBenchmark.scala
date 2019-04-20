import java.io.{BufferedOutputStream, File, FileOutputStream}

import com.sksamuel.avro4s.{AvroDataOutputStream, AvroOutputStream, AvroSchema, RecordFormat}
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroSerializer}
import org.apache.avro.file.{CodecFactory, DataFileWriter}
import org.apache.avro.generic.{GenericDatumWriter, GenericRecord}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{Data, DataUtils}

object AvroSerializationBenchmark extends Bench.LocalTime {
  val gen = Gen.single("input file")("50000.csv")
  val schema = AvroSchema[Data]

  performance of "avro serialization" in {
    measure method "serialize with schema" in {
      using(gen) config (
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = AvroDataOutputStream[Data](new FileOutputStream(new File("avroDataSerialization.out")), schema, CodecFactory.nullCodec())
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            out.write(data)
            i += 1

            if (i == 1000) {out.flush(); i = 0}
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }

    measure method "binary serialization without schema" in {
      using(gen) config (
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = AvroOutputStream.binary[Data].to(new FileOutputStream(new File("avroBinarySerialization.out"))).build(schema)
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            out.write(data)
            i += 1

            if (i == 1000) {out.flush(); i = 0}
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }

    measure method "low-level API avro serialization" in {
      using(gen) config (
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val out = new DataFileWriter[GenericRecord](new GenericDatumWriter[GenericRecord](schema)).create(schema, new File("lowLevelAvroSerialization.out"))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            out.append(DataUtils.dataToGenericRecord(data, schema))
            i += 1

            if (i == 1000) {out.flush(); i = 0}
          })
        })

        out.flush()
        out.close()
        in.close()
      }
    }

    class SchemaRegistryGenericRecordSerializer(schemaRegistryUrl: String) {
      import scala.collection.JavaConverters._

      private val serializer: KafkaAvroSerializer = {
        val cfg = Map(
          AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> schemaRegistryUrl
        )
        val s = new KafkaAvroSerializer()
        s.configure(cfg.asJava, false)

        s
      }

      private val format = RecordFormat[Data]

      // some predefined topic
      def serialize(data: Data): Array[Byte] = serializer.serialize("source", format.to(data))
    }

    measure method "schema registry" in {
      using(gen) config (
        exec.benchRuns -> 1,
        exec.minWarmupRuns -> 1,
        exec.maxWarmupRuns -> 1
      ) in { file =>
        val serializer = new SchemaRegistryGenericRecordSerializer("http://localhost:8081")
        val out = new BufferedOutputStream(new FileOutputStream(new File("schemaRegistrySerialization.out")))
        var i = 0

        val in = DataUtils.readCsv(file)
        in.foreach(rs => {
          rs.foreach(data => {
            out.write(serializer.serialize(data))
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
