package bench

import _root_.java.io.File
import _root_.java.nio.file.{Path => JPath, Paths}

import org.apache.hadoop.fs.Path

object Settings {
  val recordsCount: Int = Option(System.getenv("BENCH_RECORDS_COUNT").toInt).getOrElse(100000)
  val flushInterval: Int = 5000

  val benchRuns: Int = Option(System.getenv("BENCH_RUNS")).getOrElse("15").toInt
  val warmups: Int = Option(System.getenv("BENCH_WARMUPS")).getOrElse("3").toInt
  val minWarmupRuns: Int = warmups
  val maxWarmupRuns: Int = warmups
  val independentSamples: Int = 1

  val dataRoot: JPath = Paths
    .get(Option(System.getenv("BENCH_DATA_DIR")).getOrElse(System.getProperty("user.dir")))
    .toAbsolutePath
    .normalize()

  def file(relative: String): File = dataRoot.resolve(relative).toFile

  def pathString(relative: String): String = dataRoot.resolve(relative).toString

  def hadoopPath(relative: String): Path = new Path(dataRoot.resolve(relative).toAbsolutePath.toUri)

  object InputCsv {
    val mixedData: String = "mixedDataInput.csv"
    val onlyStrings: String = "onlyStringsInput.csv"
    val onlyLongs: String = "onlyLongsInput.csv"
  }

  object SchemaRegistry {
    val input: String = "input.csv"
    val output: String = "schemaRegistrySerialization.out"
  }
}
