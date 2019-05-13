package bench

import project.{DataUtils, MixedData}
import project.Implicits._

object InitialDataGenerator {
  def main(args: Array[String]): Unit = {
    DataUtils.toCsvFile[MixedData]("input.csv", Settings.recordsCount)
  }
}
