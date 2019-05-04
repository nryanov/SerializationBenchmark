package bench

import project.DataUtils

object InitialDataGenerator {
  def main(args: Array[String]): Unit = {
    DataUtils.toCsvFile("input.csv", Settings.recordsCount)
  }
}
