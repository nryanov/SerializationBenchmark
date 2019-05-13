package bench.avro

import java.io.{BufferedOutputStream, File, FileOutputStream}

import bench.Settings
import com.sksamuel.avro4s.RecordFormat
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroSerializer}
import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import project.{Data, DataUtils, MixedData}
import project.Implicits._


object AvroSerializationSchemaRegistry extends Bench.LocalTime {
  val input = Gen.single("input file")("input.csv")
  performance of "avro serialization" in {
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
      using(input) config(
        exec.benchRuns -> Settings.benchRuns,
        exec.minWarmupRuns -> Settings.minWarmupRuns,
        exec.maxWarmupRuns -> Settings.maxWarmupRuns
      ) in { file =>
        val serializer = new SchemaRegistryGenericRecordSerializer("http://localhost:8081")
        val out = new BufferedOutputStream(new FileOutputStream(new File("schemaRegistrySerialization.out")))
        var i = 0

        val in = DataUtils.readCsv[MixedData](file)
        in.foreach(rs => {
          rs.foreach(data => {
            out.write(serializer.serialize(data))
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
}
