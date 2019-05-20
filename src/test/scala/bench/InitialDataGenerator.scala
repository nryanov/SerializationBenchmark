package bench

import project.{DataUtils, MixedData, OnlyLongs, OnlyStrings}
import project.Implicits._

object InitialDataGenerator {
  def main(args: Array[String]): Unit = {
//    DataUtils.toCsvFile[MixedData]("mixedDataInput.csv", Settings.recordsCount)
//    DataUtils.toCsvFile[OnlyStrings]("onlyStringsInput.csv", Settings.recordsCount)
    DataUtils.toCsvFile[OnlyLongs]("onlyLongsInput.csv", Settings.recordsCount)
  }
}
